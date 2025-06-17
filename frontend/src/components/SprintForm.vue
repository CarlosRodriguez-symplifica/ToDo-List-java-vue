<script setup>
  import { ref, watch } from 'vue'

  const props = defineProps({
    modelValue: Boolean
  })

  const emit = defineEmits(['update:modelValue', 'creado'])

  const visible = ref(props.modelValue)

  watch(() => props.modelValue, val => visible.value = val)
  watch(visible, val => emit('update:modelValue', val))

  const form = ref({
    nombre: '',
    puntosObjetivo: null
  })

  const errores = ref({
    nombre: null,
    puntosObjetivo: null
  })

  const validar = () => {
    errores.value = { nombre: null, puntosObjetivo: null }

    let valido = true
    if (!form.value.nombre) {
      errores.value.nombre = 'El nombre es requerido'
      valido = false
    }

    if (!form.value.puntosObjetivo || form.value.puntosObjetivo < 1) {
      errores.value.puntosObjetivo = 'Debe ser mayor a 0'
      valido = false
    }

    return valido
  }

  const crearSprint = async () => {
    if (!validar()) return

    try {
      const response = await fetch('api/v1/sprints', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form.value)
      })

      if (!response.ok) throw new Error('Error al crear el sprint')
      const nuevoSprint = await response.json()

      emit('creado', nuevoSprint)
      cerrar()
    } catch (error) {
      console.error(error)
    }
  }

  const cerrar = () => {
    visible.value = false
    form.value = { nombre: '', puntosObjetivo: null }
  }
</script>

<template>
  <v-dialog v-model="visible" persistent max-width="500px">
    <v-card>
      <v-card-title class="text-h6">Crear nuevo Sprint</v-card-title>

      <v-card-text>
        <v-form @submit.prevent="crearSprint">
          <v-text-field
            variant="solo-filled"
            v-model="form.nombre"
            label="Nombre del Sprint"
            :error-messages="errores.nombre"
            required
          />

          <v-text-field
            variant="solo-filled"
            v-model.number="form.puntosObjetivo"
            label="Puntos Objetivo"
            type="number"
            :error-messages="errores.puntosObjetivo"
            required
          />
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn variant="tonal" color="red" @click="cerrar">Cancelar</v-btn>
        <v-btn variant="tonal" color="success" @click="crearSprint">Crear</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style>
</style>
