<script setup>
  import { computed } from 'vue'
  import { useDraggableState } from '@/composables/useDraggableState'

  const props = defineProps({
    tarea: Object,
    columnTitle: String
  })

  const { isDragging, currentGrabbingId, hoveredId } = useDraggableState()

  const borderClass = computed(() => {
    const clases = {
      'Por Hacer': 'border-por-hacer',
      'En Progreso': 'border-en-progreso',
      'Testiando': 'border-testiando',
      'Hechas': 'border-hechas'
    }
    return clases[props.columnTitle] || ''
  })

  const chipColor = computed(() => {
      const clases = {
        'Por Hacer': 'blue',
        'En Progreso': 'orange',
        'Testiando': 'purple',
        'Hechas': 'green'
      }
      return clases[props.columnTitle] || 'primary'
    })

  const cursorClass = computed(() => {
    if (isDragging.value || currentGrabbingId.value === props.tarea.id) {
      return 'cursor-grabbing'
    }
    if (hoveredId.value === props.tarea.id) {
      return 'cursor-grab'
    }
    return ''
  })

  const onMouseDown = () => {
    currentGrabbingId.value = props.tarea.id
  }
  const onMouseUp = () => {
    currentGrabbingId.value = null
  }
  const onMouseOver = () => {
    hoveredId.value = props.tarea.id
  }
</script>

<template>
  <v-card
    :class="[
      'mb-2 mt-4 tarea-card tarea',
      borderClass,
      cursorClass,
      isDragging ? 'elevation-6' : 'elevation-2'
    ]"
    outlined
    rounded="lg"
    @mousedown="onMouseDown"
    @mouseup="onMouseUp"
    @mouseleave="onMouseUp"
    @mouseover="onMouseOver"
  >
    <v-card-title class="text-subtitle-1 font-weight-medium">{{ tarea.titulo }}</v-card-title>
    <v-card-text class="text-caption">{{ tarea.descripcion }}</v-card-text>
    <v-card-actions>
      <v-chip :color="chipColor" label size="small">
        <v-icon icon="mdi-label" start></v-icon>
        {{ tarea.puntos }} pts
      </v-chip>
    </v-card-actions>
  </v-card>
</template>

<style>
  .border-por-hacer-pts { color: 5px solid #BBDEFB; }

  .border-por-hacer { border-left: 5px solid #BBDEFB; }
  .border-en-progreso { border-left: 5px solid #FFCC80; }
  .border-testiando { border-left: 5px solid #E1BEE7; }
  .border-hechas { border-left: 5px solid #C5E1A5; }

  .tarea { transition: transform 1.5s ease; }
  .tarea:active { transform: scale(1.05); }

  .cursor-grab { cursor: grab !important; }
  .cursor-grabbing { cursor: grabbing !important; }

  .tarea-card {
    border-left-width: 6px;
    border-left-style: solid;
  }
</style>
