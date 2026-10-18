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
          <strong>{{ slotProps.option.cableType.name }}</strong>
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
import { fetchCables } from '../api/cables'
import type { Cable } from '../types'

const emit = defineEmits<{
  (e: 'select', cable: Cable | null): void
}>()

const cables = ref<(Cable & { fullName: string })[]>([])
const selectedCable = ref<(Cable & { fullName: string }) | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const data = await fetchCables()
    cables.value = data.map((cable) => ({
      ...cable,
      fullName: `${cable.cableType.name} ${cable.conductorCount}×${cable.crossSection} мм²`,
    }))
  } catch (error) {
    console.error('Ошибка загрузки каталога:', error)
  } finally {
    loading.value = false
  }
})

function handleSelect(cable: (Cable & { fullName: string }) | null) {
  emit('select', cable)
}
</script>

<style scoped>
.cable-selector {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.cable-selector label {
  font-weight: 500;
  font-size: 0.9rem;
}

.cable-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0.5rem 0;
}

.cable-specs {
  color: #666;
  font-size: 0.85rem;
}

.w-full {
  width: 100%;
}
</style>
