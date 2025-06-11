<script setup>
  import { ref, onMounted, computed } from 'vue'

  import SprintList from '@/components/SprintList.vue'
  import SprintHeader from '@/components/SprintHeader.vue'
  import TareaCard from '@/components/TareaCard.vue'

  import draggable from 'vuedraggable'
  import { useDraggableState } from '@/composables/useDraggableState'

  const { isDragging } = useDraggableState()

  const sprints = ref([])
  const selectedSprint = ref([])
  const tareasPorColumna = ref({
    TODO: [], DOING: [], TESTING: [], DONE: []
  })

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
      const response = await fetch('api/v1/sprints')
      if (!response.ok) throw new Error('Error al cargar los sprints')
      const data = await response.json()
      sprints.value = data.reverse()
      if (sprints.value.length) {
        await selectSprint(sprints.value[0].id)
      }
    } catch (error) {
      console.error(error)
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
        fetchProgreso(tarea.id)
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

  onMounted(fetchSprints)
</script>

<template>
  <v-layout>
    <v-app-bar title="To-Do Board" color="secondary" dark></v-app-bar>

    <SprintList
      :sprints="sprints"
      :selectedSprintId="selectedSprint?.id"
      @select="selectSprint"
    />

    <v-main>
      <v-container fluid>
        <div v-if="selectedSprint">

          <SprintHeader
            :sprint="selectedSprint"
            :progreso="progreso"
            :tareas-abiertas="tareasAbiertas"
            :tareas-cerradas="tareasPorColumna.DONE.length"
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
                      <TareaCard :tarea="tarea" :columnTitle="col.titulo" />
                    </template>
                  </draggable>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </div>
        <div v-else>
          <p>Selecciona un sprint del menú lateral para ver el tablero.</p>
        </div>
      </v-container>
    </v-main>
  </v-layout>
</template>

<style>
</style>
