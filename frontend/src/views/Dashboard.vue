<script setup>
  import { ref, onMounted, watch, computed } from 'vue'
  import draggable from 'vuedraggable'

  const sprints = ref([])
  const selectedSprint = ref([])
  const tareasPorColumna = ref({
    TODO: [],
    DOING: [],
    TESTING: [],
    DONE: []
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

  const getColumnBorderClass = (titulo) => {
    const clases = {
      'Por Hacer': 'border-por-hacer mb-2 mt-4',
      'En Progreso': 'border-en-progreso mb-2 mt-4',
      'Testiando': 'border-testiando mb-2 mt-4',
      'Hechas': 'border-hechas mb-2 mt-4'
    }
    return clases[titulo] || '#FFFFFF'
  }

  const fetchSprints = async () => {
    try {
      const response = await fetch('api/v1/sprints')
      if (!response.ok) throw new Error('Error al cargar los sprints')
      sprints.value = await response.json()
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

  const progreso = ref({ hechos: 0, faltan: 0 })

  const fetchProgreso = async (sprintId) => {
    try {
      const response = await fetch(`api/v1/sprints/${sprintId}/progreso`)
      if (!response.ok) throw new Error('Error al obtener el progreso del sprint')
      progreso.value = await response.json()
    } catch (error) {
      console.error(error)
    }
  }

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
    <v-app-bar title="Sprint Board" color="secondary" dark></v-app-bar>

    <v-navigation-drawer width="250" permanent>
      <v-list nav>
        <v-list-item
          v-for="sprint in sprints"
          :key="sprint.id"
          :title="sprint.nombre"
          :active="selectedSprint?.id === sprint.id"
          @click="selectSprint(sprint.id)"
        />
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <v-container fluid>
        <div v-if="selectedSprint">
          <h2 class="text-h5 mb-4">{{ selectedSprint.nombre }}</h2>

          <v-card elevation="16" color="blue-grey lighten-4" dark flat rounded="lg">
            <v-row class="pa-4" align="center" justify="start">

              <v-col class="d-flex align-center" cols="auto">
                <v-icon start>mdi-badge-account-horizontal-outline</v-icon>
                <span class="text-h6 font-weight-bold mr-1">{{ selectedSprint.puntosObjetivo }}</span>
                <span class="text-caption">puntos totales</span>
              </v-col>

              <v-col class="d-flex align-center" cols="auto">
                <v-icon start>mdi-check-circle-outline</v-icon>
                <span class="text-h6 font-weight-bold mr-1">{{ progreso.hechos }}</span>
                <span class="text-caption">completados</span>
              </v-col>

              <v-col class="d-flex align-center" cols="auto">
                <v-icon start>mdi-clock-outline</v-icon>
                <span class="text-h6 font-weight-bold mr-1">{{ progreso.faltan }}</span>
                <span class="text-caption">pendientes</span>
              </v-col>

              <v-divider vertical class="mx-4" />

              <v-col class="d-flex align-center" cols="auto">
                <v-icon start>mdi-format-list-bulleted</v-icon>
                <span class="text-h6 font-weight-bold mr-1">{{ tareasAbiertas }}</span>
                <span class="text-caption">tareas abiertas</span>
              </v-col>

              <v-col class="d-flex align-center" cols="auto">
                <v-icon start>mdi-check-bold</v-icon>
                <span class="text-h6 font-weight-bold mr-1">{{ tareasPorColumna.DONE.length }}</span>
                <span class="text-caption">cerradas</span>
              </v-col>
            </v-row>
          </v-card>

          <v-row dense class="mt-4">
            <v-col
              cols="12"
              md="3"
              v-for="(col, index) in columnas"
              :key="col.estado"
              :data-estado-index="index"
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
                  >
                    <template #item="{ element: tarea }">
                      <v-card
                        :class="getColumnBorderClass(col.titulo)"
                        elevation="6"
                      >
                        <v-card-title class="text-subtitle-1">{{ tarea.titulo }}</v-card-title>
                        <v-card-text>{{ tarea.descripcion }}</v-card-text>
                        <v-card-subtitle>Puntos: {{ tarea.puntos }}</v-card-subtitle>
                      </v-card>
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
  .border-por-hacer {
    border-left: 5px solid #BBDEFB;
  }

  .border-en-progreso {
    border-left: 5px solid #FFCC80;
  }

  .border-testiando {
    border-left: 5px solid #E1BEE7;
  }

  .border-hechas {
    border-left: 5px solid #C5E1A5;
  }
</style>
