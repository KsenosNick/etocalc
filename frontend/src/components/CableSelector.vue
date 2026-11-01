<template>
  <div class="cable-selector">
    <label for="cable">Кабель</label>
    <Select
      id="cable"
      v-model="selectedCable"
      :options="cables"
      :loading="loading"
      optionLabel="fullName"
      placeholder="Выберите кабель"
      class="w-full"
      filter
      showClear
      @update:modelValue="handleSelect"
    >
      <template #option="slotProps">
        <div class="cable-option">
          <strong>{{ slotProps.option.cableTypeName }}</strong>
          <span class="cable-specs">
            {{ slotProps.option.conductorCount }}×{{ slotProps.option.crossSection }} мм²,
            {{ slotProps.option.nominalVoltage }}В
          </span>
        </div>
      </template>
    </Select>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Select from 'primevue/select'
import type { Cable } from '../types'
import { getCables } from '../api/catalog'

const emit = defineEmits<{
  (e: 'select', cable: Cable | null): void
}>()

const cables = ref<Cable[]>([])
const selectedCable = ref<Cable | null>(null)
const loading = ref(true)

onMounted(async () => {
  loading.value = true
  try {
    const page = await getCables({}, 0, 1000, [
      'cableType.name,asc',
      'conductorCount,asc',
      'crossSection,asc',
    ])
    cables.value = page.content
  } catch (error) {
    console.error('Ошибка загрузки кабелей:', error)
  } finally {
    loading.value = false
  }
})

function handleSelect(cable: Cable | null) {
  emit('select', cable)
}
</script>

<style scoped>
.cable-selector {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.cable-selector label {
  font-weight: 500;
  font-size: var(--text-sm);
}

.cable-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: var(--spacing-sm) 0;
}

.cable-specs {
  color: var(--color-gray-500);
  font-size: var(--text-xs);
}

.w-full {
  width: 100%;
}
</style>
