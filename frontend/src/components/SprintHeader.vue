<script setup>
  defineProps({
    sprint: {
      type: Object,
      required: true
    },
    progreso: {
      type: Object,
      required: true
    },
    tareasAbiertas: {
      type: Number,
      required: true
    },
    tareasCerradas: {
      type: Number,
      required: true
    }
  })
</script>

<template>
  <v-card elevation="16" color="blue-grey lighten-4" dark flat rounded="lg">
    <v-row class="pa-4" align="center" justify="start">

      <v-col class="d-flex align-center" cols="auto">
        <h2 class="text-h5">{{ sprint.nombre }}</h2>
      </v-col>

      <v-col class="d-flex align-center" cols="auto">
        <v-icon start>mdi-badge-account-horizontal-outline</v-icon>
        <span class="text-h6 font-weight-bold mr-1">{{ sprint.puntosObjetivo }}</span>
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
        <span class="text-h6 font-weight-bold mr-1">{{ tareasCerradas }}</span>
        <span class="text-caption">cerradas</span>
      </v-col>
    </v-row>
  </v-card>
</template>
