<template>
  <div class="calculator-container">
    <!-- Верхняя часть: Форма и Основные результаты -->
    <div class="calculator-grid">
      <div class="form-column">
        <VoltageDropForm @calculate="handleCalculate" :loading="loading" />
      </div>
      <div class="result-column">
        <VoltageDropResult :result="result" :loading="loading" />
      </div>
    </div>

    <!-- Нижняя часть: Ход расчёта на всю ширину -->
    <div v-if="result && result.steps && result.steps.length > 0" class="full-width-steps">
      <h3 class="steps-title">📐 Ход расчёта</h3>
      <CalculationSteps :steps="result.steps" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import VoltageDropForm from '../components/VoltageDropForm.vue'
import VoltageDropResult from '../components/VoltageDropResult.vue'
import CalculationSteps from '../components/CalculationSteps.vue'
import { calculateVoltageDrop } from '../api/calculations'
import type { VoltageDropRequest, VoltageDropResponse } from '../types'

const toast = useToast()
const result = ref<VoltageDropResponse | null>(null)
const loading = ref(false)

async function handleCalculate(request: VoltageDropRequest) {
  loading.value = true
  result.value = null

  try {
    const response = await calculateVoltageDrop(request)
    result.value = response

    toast.add({
      severity: 'success',
      summary: 'Расчёт выполнен',
      detail: `${result.value.cableName}: ${result.value.deltaUPercent.toFixed(2)}%`,
      life: 3000,
    })
  } catch (error: unknown) {
    const err = error as { response?: { data?: { message?: string } | string }; message?: string }
    const message =
      typeof err.response?.data === 'string'
        ? err.response.data
        : err.response?.data?.message || err.message || 'Неизвестная ошибка'

    toast.add({
      severity: 'error',
      summary: 'Ошибка расчёта',
      detail: message,
      life: 5000,
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.calculator-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.calculator-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

@media (min-width: 992px) {
  .calculator-grid {
    grid-template-columns: 1fr 1fr;
  }
}

.form-column,
.result-column {
  min-width: 0;
}

.full-width-steps {
  width: 100%;
  animation: fadeIn 0.5s ease-in-out;
}

.steps-title {
  margin: 0 0 1rem 0;
  font-size: 1.25rem;
  color: var(--text-color);
  border-bottom: 2px solid var(--border-color);
  padding-bottom: 0.5rem;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
