<template>
  <Card>
    <template #title>Результат расчёта</template>
    <template #content>
      <div v-if="loading" class="loading">
        <ProgressSpinner />
        <p>Выполняется расчёт...</p>
      </div>

      <div v-else-if="result" class="result">
        <div class="result-header">
          <h3>{{ result.cableName }}</h3>
        </div>

        <div class="result-metrics">
          <div class="metric" :class="getSeverityClass(result.deltaUPercent)">
            <div class="metric-label">Потеря напряжения</div>
            <div class="metric-value">{{ result.deltaUPercent.toFixed(2) }}%</div>
            <div class="metric-detail">{{ result.deltaUVolts.toFixed(2) }} В</div>
          </div>

          <div class="metric">
            <div class="metric-label">Напряжение в конце линии</div>
            <div class="metric-value">{{ result.endVoltage.toFixed(1) }} В</div>
          </div>
        </div>

        <div class="result-assessment">
          <Message
            :severity="getSeverity(result.deltaUPercent)"
            :icon="getIcon(result.deltaUPercent)"
          >
            {{ getAssessment(result.deltaUPercent) }}
          </Message>
        </div>
      </div>

      <div v-else class="empty">
        <i class="pi pi-info-circle"></i>
        <p>Заполните форму слева и нажмите "Рассчитать"</p>
      </div>
    </template>
  </Card>
</template>

<script setup lang="ts">
import Card from 'primevue/card'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import type { VoltageDropResponse } from '../types'

defineProps<{
  result: VoltageDropResponse | null
  loading: boolean
}>()

function getSeverity(percent: number): 'success' | 'warn' | 'error' {
  if (percent <= 3) return 'success'
  if (percent <= 5) return 'warn'
  return 'error'
}

function getSeverityClass(percent: number): string {
  if (percent <= 3) return 'severity-success'
  if (percent <= 5) return 'severity-warning'
  return 'severity-error'
}

function getIcon(percent: number): string {
  if (percent <= 3) return 'pi pi-check-circle'
  if (percent <= 5) return 'pi pi-exclamation-triangle'
  return 'pi pi-times-circle'
}

function getAssessment(percent: number): string {
  if (percent <= 3) return 'Отлично! Потери в пределах нормы (≤3% для осветительных сетей)'
  if (percent <= 5) return 'Допустимо. Потери в пределах 5% (норма для силовых сетей)'
  return 'Превышение нормы! Рекомендуется увеличить сечение кабеля или уменьшить длину линии'
}
</script>

<style scoped>
.loading,
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  text-align: center;
  color: var(--text-color);
  opacity: 0.7;
}

.loading i,
.empty i {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.result {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.result-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: var(--text-color);
}

.result-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.metric {
  padding: 1rem;
  border-radius: 8px;
  background-color: var(--surface-ground);
  border: 2px solid transparent;
  transition: all 0.3s;
}

.metric-label {
  font-size: 0.85rem;
  color: var(--text-color);
  opacity: 0.7;
  margin-bottom: 0.5rem;
}

.metric-value {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 0.25rem;
  color: var(--text-color);
}

.metric-detail {
  font-size: 1rem;
  color: var(--text-color);
  opacity: 0.8;
}

.severity-success {
  border-color: #4caf50;
  background-color: rgba(76, 175, 80, 0.1);
}
.severity-warning {
  border-color: #ff9800;
  background-color: rgba(255, 152, 0, 0.1);
}
.severity-error {
  border-color: #f44336;
  background-color: rgba(244, 67, 54, 0.1);
}

.result-assessment {
  margin-top: 1rem;
}
</style>
