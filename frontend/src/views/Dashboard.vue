<script setup>
  import { ref, onMounted, computed } from 'vue'

  import SprintList from '@/components/SprintList.vue'
  import SprintHeader from '@/components/SprintHeader.vue'
  import TareaCard from '@/components/TareaCard.vue'
  import TareaForm from '@/components/TareaForm.vue'
  import SprintForm from '@/components/SprintForm.vue'

  import draggable from 'vuedraggable'
  import { useDraggableState } from '@/composables/useDraggableState'

  const { isDragging } = useDraggableState()
  const slideActual = ref(0)
  const cargando = ref(true)

  const sprints = ref([])
  const selectedSprint = ref([])
  const tareasPorColumna = ref({
    TODO: [], DOING: [], TESTING: [], DONE: []
  })
  const tareaSeleccionada = ref(null)
  const mostrarEditor = ref(false)
  const modo = ref('crear')

  const mostrarDialogoCrearSprint = ref(false)

  const columnas = [
    { titulo: 'Por Hacer', estado: 'TODO' },
    { titulo: 'En Progreso', estado: 'DOING' },
    { titulo: 'Testiando', estado: 'TESTING' },
    { titulo: 'Hechas', estado: 'DONE' }
  ]

  const getColumnClass = (titulo) => {
    const clases = {
      'Por Hacer': 'bg-blue-lighten-4',
      'En Progreso': 'bg-orange-lighten-3',
      'Testiando': 'bg-purple-lighten-4',
      'Hechas': 'bg-light-green-lighten-3'
    }
    return clases[titulo] || '#FFFFFF'
  }

  const progreso = ref({ hechos: 0, faltan: 0 })

  const fetchSprints = async () => {
    try {
      cargando.value = true
      const response = await fetch('api/v1/sprints')
      if (!response.ok) throw new Error('Error al cargar los sprints')
      const data = await response.json()
      sprints.value = data.reverse()
      if (sprints.value.length) {
        await selectSprint(sprints.value[0].id)
      }
    } catch (error) {
      console.error(error)
    } finally {
      cargando.value = false
    }
  }

  const selectSprint = async (sprintId) => {
    try {
      const response = await fetch(`api/v1/sprints/${sprintId}`)
      if (!response.ok) throw new Error('Error al cargar el sprint seleccionado.')
      const sprint = await response.json()

      selectedSprint.value = sprint
      const tareas = Object.values(sprint.tareas || {})
      tareasPorColumna.value = {
        TODO: tareas.filter(t => t.estado === 'TODO'),
        DOING: tareas.filter(t => t.estado === 'DOING'),
        TESTING: tareas.filter(t => t.estado === 'TESTING'),
        DONE: tareas.filter(t => t.estado === 'DONE')
      }
      await fetchProgreso(sprintId)
    } catch (error) {
      console.error(error)
    }
  }

  const fetchProgreso = async (sprintId) => {
    try {
      const response = await fetch(`api/v1/sprints/${sprintId}/progreso`)
      if (!response.ok) throw new Error('Error al obtener el progreso del sprint')
      progreso.value = await response.json()
    } catch (error) {
      console.error(error)
    }
  }

  const onDragStart = () => { isDragging.value = true }
  const onDragEnd = () => { isDragging.value = false }

  const onDragChange = async (event, nuevoEstado) => {
    const { added } = event

    if (added) {
      const tarea = added.element

      try {
        const response = await fetch(`api/v1/tareas/${tarea.id}/estado`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ estado: nuevoEstado })
        })

        if (!response.ok) {
          throw new Error('Error al actualizar el estado de la tarea.')
        }

        tarea.estado = nuevoEstado
        await fetchProgreso(tarea.sprintId)
      } catch (error) {
        console.error(error)
      }
    }
  }

  const tareasAbiertas = computed(() =>
    tareasPorColumna.value.TODO.length +
    tareasPorColumna.value.DOING.length +
    tareasPorColumna.value.TESTING.length
  )

  const abrirDialogCrear = (sprintId) => {
    modo.value = 'crear'
    tareaSeleccionada.value = null
    mostrarEditor.value = true
  }

  const abrirDialogEditar = (tarea) => {
    modo.value = 'editar'
    tareaSeleccionada.value = tarea
    mostrarEditor.value = true
  }

  const cargarTareas = async (tareaActualizada) => {
    const sprintActualId = selectedSprint.value?.id

    if (tareaActualizada.sprintId !== sprintActualId) {
      try {
        await fetch(`api/v1/tareas/${tareaActualizada.id}/estado`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ estado: 'TODO' })
        })
      } catch (error) {
        console.log('Error al cambiar el estado de la tarea a TODO:', error)
      }

      await selectSprint(sprintActualId)
      await fetchProgreso(sprintActualId)
      return
    }

    const todasColumnas = Object.keys(tareasPorColumna.value)

    for (const estado of todasColumnas) {
      tareasPorColumna.value[estado] = tareasPorColumna.value[estado].filter(
        t => t.id !== tareaActualizada.id
      )
    }

    tareasPorColumna.value[tareaActualizada.estado] = [
      ...tareasPorColumna.value[tareaActualizada.estado],
      tareaActualizada
    ]

    await fetchProgreso(sprintActualId)
  }

  const sprintCreado = async (nuevoSprint) => {
    sprints.value.unshift(nuevoSprint)
    await selectSprint(nuevoSprint.id)
  }

  onMounted(fetchSprints)
</script>

<template>
  <v-layout>
    <v-app-bar color="secondary" dark>
      <template #prepend>
        <img src="/to-do-list.ico" alt="Logo" height="28" class="ml-2" />
      </template>

      <v-toolbar-title>To-Do Board</v-toolbar-title>

      <v-spacer />

      <v-btn class="mr-14" elevation="4" @click="mostrarDialogoCrearSprint = true">
        Crear Sprint
      </v-btn>
    </v-app-bar>

    <SprintList
      :sprints="sprints"
      :selectedSprintId="selectedSprint?.id"
      @select="selectSprint"
    />

    <v-main>
      <v-container fluid>
        <div v-if="cargando">
          <v-row dense class="mt-4">
            <v-col cols="12" md="3" v-for="i in 4" :key="i">
              <v-skeleton-loader
                type="card"
                class="mb-4"
                height="300"
                boilerplate
              />
            </v-col>
          </v-row>
        </div>

        <div v-else-if="selectedSprint?.id">

          <SprintHeader
            :sprint="selectedSprint"
            :progreso="progreso"
            :tareas-abiertas="tareasAbiertas"
            :tareas-cerradas="tareasPorColumna.DONE.length"
            @crear-tarea="abrirDialogCrear(selectedSprint.id)"
          />

          <v-row dense class="mt-4">
            <v-col
              cols="12"
              md="3"
              v-for="(col, index) in columnas"
              :key="col.estado"
            >
              <v-card class="mb-2" elevation="16">
                <v-card-title :class="getColumnClass(col.titulo)">{{ col.titulo }}</v-card-title>
                <v-card-text class="mt-4">
                  <draggable
                    :list="tareasPorColumna[col.estado]"
                    group="tareas"
                    item-key="id"
                    class="d-flex flex-column gap-2"
                    @change="(e) => onDragChange(e, col.estado)"
                    @start="onDragStart"
                    @end="onDragEnd"
                  >
                    <template #item="{ element: tarea }">
                      <TareaCard :tarea="tarea" :columnTitle="col.titulo" @editar="abrirDialogEditar" />
                    </template>
                  </draggable>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </div>

        <div v-else>
          <v-container fluid class="fill-height d-flex justify-center align-center">
            <v-window
              v-model="slideActual"
              direction="vertical"
              show-arrows="hover"
              class="w-100"
              style="max-width: 900px;"
            >

              <v-window-item :value="0">
                <v-card class="pa-6 d-flex flex-column align-center justify-center text-center" height="400" elevation="10">
                  <img src="/to-do-list.ico" alt="Logo" class="mb-4" style="width: 200px;" />
                  <h2 class="text-h5 font-weight-bold">¡Bienvenido a To-Do Board!</h2>
                  <p>Organiza tus tareas por sprints y mejora tu productividad.</p>
                </v-card>
              </v-window-item>

              <v-window-item :value="1">
                <v-card class="pa-6 d-flex flex-column align-center justify-center text-center" height="400" elevation="10">
                  <h2 class="text-h6 font-weight-bold">¿Por dónde empezar?</h2>
                  <p>Haz clic en el botón <strong>Crear Sprint</strong> en la parte superior para comenzar a trabajar.</p>
                  <v-icon size="60" class="mt-4" color="primary">mdi-gesture-tap-button</v-icon>
                </v-card>
              </v-window-item>

              <v-window-item :value="2">
                <v-card class="pa-6 d-flex flex-column align-center justify-center text-center" height="400" elevation="10">
                  <h2 class="text-h6 font-weight-bold">¡Gracias por usar To-Do Board!</h2>
                  <p>Estamos felices de ayudarte a cumplir tus metas.</p>
                  <v-icon size="60" class="mt-4" color="success">mdi-emoticon-happy-outline</v-icon>
                </v-card>
              </v-window-item>
            </v-window>
          </v-container>
        </div>
      </v-container>
      <TareaForm
        v-model="mostrarEditor"
        :tarea="tareaSeleccionada"
        :sprints="sprints"
        :modo="modo"
        :sprint="selectedSprint.id"
        @actualizada="cargarTareas"
      />

      <SprintForm
        v-model="mostrarDialogoCrearSprint"
        @creado="sprintCreado"
      />
    </v-main>
  </v-layout>
</template>

<style>
</style>
