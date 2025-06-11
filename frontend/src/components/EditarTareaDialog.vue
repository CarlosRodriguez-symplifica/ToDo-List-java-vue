<script setup>
  import { ref, watch } from 'vue'

  const props = defineProps({
    modelValue: Boolean,
    tarea: Object,
    sprints: Array
  })
  const emit = defineEmits(['update:modelValue', 'actualizada'])
  const abierto = ref(false)
  const editada = ref({})
  const mostrarAlerta = ref(false)
  const mensajeAlerta = ref('')

  const estados = [
    { text: 'Por Hacer', value: 'TODO' },
    { text: 'En Progreso', value: 'DOING' },
    { text: 'Testeando', value: 'TESTING' },
    { text: 'Hechas', value: 'DONE' },
  ]

  watch(() => props.modelValue, (val) => {
    abierto.value = val
    if (val && props.tarea) {
      editada.value = {
        id: props.tarea.id,
        titulo: props.tarea.titulo,
        descripcion: props.tarea.descripcion,
        puntos: props.tarea.puntos,
        estado: props.tarea.estado,
        sprintId: props.tarea.sprintId || null,
      }
    }
  })

  watch(abierto, (val) => emit('update:modelValue', val))

  const guardarCambios = async () => {
    if (editada.value.estado === 'DONE' && editada.value.sprintId !== props.tarea.sprintId) {
      mensajeAlerta.value = 'No se puede cambiar de sprint porque la tarea ya está completada.'
      mostrarAlerta.value = true
      return
    }

    try {
      await fetch(`api/v1/tareas/${editada.value.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          titulo: editada.value.titulo,
          descripcion: editada.value.descripcion,
          puntos: editada.value.puntos,
          estado: editada.value.estado
        }),
      })

      if (editada.value.sprintId !== props.tarea.sprintId) {
        const respuesta = await fetch(`api/v1/tareas/${editada.value.id}/asociar-sprint`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ sprintId: editada.value.sprintId }),
        })

        if(!respuesta.ok) {
          const error = respuesta.json()
          console.log('Error al asociar tarea a nuevo sprint:', error)
          alert('No se puede mover una tarea completada (DONE) a otro sprint.')
          return
        }
      }

      emit('actualizada', { ...editada.value })
      abierto.value = false
    } catch (error) {
      console.error('Error actualizando tarea:', error)
    }
  }
</script>

<template>
  <v-dialog v-model="abierto" persistent max-width="600px">
    <v-card>
      <v-card-title>Editar Tarea</v-card-title>

      <v-card-text>
        <v-text-field v-model="editada.titulo" label="Título" />
        <v-textarea v-model="editada.descripcion" label="Descripción" />
        <v-text-field v-model.number="editada.puntos" label="Puntos" type="number" />

        <v-select
          v-model="editada.estado"
          :items="estados"
          item-title="text"
          item-value="value"
          label="Estado"
        />

        <v-select
          v-model="editada.sprintId"
          :items="sprints"
          item-title="nombre"
          item-value="id"
          label="Sprint"
          :disabled="editada.estado === 'DONE'"
        />

        <v-alert
          v-if="editada.estado === 'DONE'"
          type="info"
          density="compact"
          class="mt-2"
          border="start"
          color="orange-accent-4"
          icon="mdi-information"
        >
          No se puede cambiar de sprint porque la tarea ya está completada.
        </v-alert>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" @click="guardarCambios">Guardar</v-btn>
        <v-btn @click="abierto = false">Cancelar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <v-snackbar
    v-model="mostrarAlerta"
    :timeout="4000"
    color="warning"
    location="top right"
  >
    {{ mensajeAlerta }}
  </v-snackbar>
</template>
