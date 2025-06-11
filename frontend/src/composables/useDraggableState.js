import { ref } from 'vue'

const isDragging = ref(false)
const currentGrabbingId = ref(null)
const hoveredId = ref(null)

export function useDraggableState() {
  return {
    isDragging,
    currentGrabbingId,
    hoveredId
  }
}
