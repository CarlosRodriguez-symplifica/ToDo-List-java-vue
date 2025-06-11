<script setup>
  defineProps({
    sprints: Array,
    selectedSprintId: [Number, String]
  })

  defineEmits(['select'])
</script>

<template>
  <v-navigation-drawer width="200" permanent>
    <v-list nav>
      <v-list-subheader color="secondary">Lista de sprints</v-list-subheader>

      <v-list-item
        v-for="sprint in sprints"
        :key="sprint.id"
        rounded="shaped"
        color="secondary"
        :title="sprint.nombre"
        :active="sprint.id === selectedSprintId"
        @click="$emit('select', sprint.id)"
      >
        <template #prepend>
          <v-icon icon="mdi-list-box-outline" start></v-icon>
        </template>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>
