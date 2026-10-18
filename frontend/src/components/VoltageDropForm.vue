<template>
  <Card>
    <template #title>Расчёт падения напряжения</template>
    <template #content>
      <form @submit.prevent="handleSubmit" class="form">
        <CableSelector @select="handleCableSelect" />

        <div class="form-row">
          <div class="form-field">
            <label for="networkVoltage">Напряжение сети, В</label>
            <InputNumber
              id="networkVoltage"
              v-model="form.networkVoltage"
              :min="0"
              :max="1000"
              :step="10"
              class="w-full"
            />
          </div>

          <div class="form-field">
            <label for="current">Ток, А</label>
            <InputNumber
              id="current"
              v-model="form.current"
              :min="0"
              :max="1000"
              :step="0.1"
              class="w-full"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-field">
            <label for="length">Длина линии, м</label>
            <InputNumber
              id="length"
              v-model="form.length"
              :min="0"
              :max="10000"
              :step="1"
              class="w-full"
            />
          </div>

          <div class="form-field">
            <label for="cosPhi">cos φ</label>
            <InputNumber
              id="cosPhi"
              v-model="form.cosPhi"
              :min="0.01"
              :max="1"
              :step="0.01"
              :minFractionDigits="2"
              :maxFractionDigits="2"
              class="w-full"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-field">
            <label for="ambientTemperature">Температура, °C</label>
            <InputNumber
              id="ambientTemperature"
              v-model="form.ambientTemperature"
              :min="-40"
              :max="60"
              :step="1"
              class="w-full"
            />
          </div>

          <div class="form-field">
            <label for="parallelCableCount">Кабелей в пучке</label>
            <InputNumber
              id="parallelCableCount"
              v-model="form.parallelCableCount"
              :min="1"
              :max="10"
              :step="1"
              class="w-full"
            />
          </div>
        </div>

        <Button
          type="submit"
          label="Рассчитать"
          icon="pi pi-calculator"
          :loading="loading"
          class="submit-button"
          :disabled="!selectedCable"
        />
      </form>
    </template>
  </Card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Card from 'primevue/card'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import CableSelector from './CableSelector.vue'
import type { Cable, VoltageDropRequest } from '../types'
import { CalculationSourceType } from '../types'

const emit = defineEmits<{
  (e: 'calculate', request: VoltageDropRequest): void
}>()

defineProps<{
  loading: boolean
}>()

const selectedCable = ref<Cable | null>(null)

const form = ref({
  networkVoltage: 230,
  current: 16,
  length: 50,
  cosPhi: 0.9,
  ambientTemperature: 20,
  parallelCableCount: 1,
})

function handleCableSelect(cable: Cable | null) {
  selectedCable.value = cable

  // Умное определение напряжения сети
  if (cable) {
    form.value.networkVoltage = cable.conductorCount <= 3 ? 230 : 400
  }
}

function handleSubmit() {
  if (!selectedCable.value) {
    return
  }

  const request: VoltageDropRequest = {
    sourceType: CalculationSourceType.CABLE,
    sourceId: selectedCable.value.id,
    ...form.value,
  }

  emit('calculate', request)
}
</script>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-field label {
  font-weight: 500;
  font-size: 0.9rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.submit-button {
  width: 100%;
  padding: 0.75rem;
  font-size: 1.1rem;
  margin-top: 0.5rem;
}

.w-full {
  width: 100%;
}
</style>
