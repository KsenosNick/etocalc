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
          <div class="metric" :class="getSeverityClass(result.endVoltage, result.targetVoltage)">
            <div class="metric-label">Потеря напряжения</div>
            <div class="metric-value">{{ result.deltaUPercent.toFixed(2) }}%</div>
            <div class="metric-detail">{{ result.deltaUVolts.toFixed(2) }} В</div>
          </div>

          <div class="metric">
            <div class="metric-label">Напряжение в конце линии</div>
            <div class="metric-value">{{ result.endVoltage.toFixed(1) }} В</div>
            <div class="metric-detail" style="font-size: 0.8rem; margin-top: 4px">
              (цель: {{ result.targetVoltage }} В)
            </div>
          </div>
        </div>

        <div class="result-assessment">
          <Message
            :severity="getSeverity(result.endVoltage, result.targetVoltage)"
            :icon="getIcon(result.endVoltage, result.targetVoltage)"
          >
            {{ getAssessment(result.endVoltage, result.targetVoltage) }}
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

function getDeviationPercent(endV: number, targetV: number): number {
  return (Math.abs(endV - targetV) / targetV) * 100
}

function getSeverity(endV: number, targetV: number): 'success' | 'warn' | 'error' {
  const dev = getDeviationPercent(endV, targetV)
  if (dev <= 5) return 'success'
  if (dev <= 10) return 'warn'
  return 'error'
}

function getSeverityClass(endV: number, targetV: number): string {
  const dev = getDeviationPercent(endV, targetV)
  if (dev <= 5) return 'severity-success'
  if (dev <= 10) return 'severity-warning'
  return 'severity-error'
}

function getIcon(endV: number, targetV: number): string {
  const dev = getDeviationPercent(endV, targetV)
  if (dev <= 5) return 'pi pi-check-circle'
  if (dev <= 10) return 'pi pi-exclamation-triangle'
  return 'pi pi-times-circle'
}

function getAssessment(endV: number, targetV: number): string {
  const dev = getDeviationPercent(endV, targetV)
  const direction = endV < targetV ? 'понижено' : 'повышено'

  if (dev <= 5) {
    return `Отлично! Напряжение в норме (${endV.toFixed(1)} В). Отклонение от номинала (${targetV} В) составляет всего ${dev.toFixed(1)}%.`
  }
  return `Превышение нормы! Напряжение ${direction} (${endV.toFixed(1)} В). Отклонение от номинала (${targetV} В) составляет ${dev.toFixed(1)}%.`
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
  margin-bottom: var(--spacing-md);
}

.result {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.result-header h3 {
  margin: 0;
  font-size: var(--text-lg);
  color: var(--text-color);
}

.result-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.metric {
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  background-color: var(--surface-ground);
  border: 2px solid transparent;
  transition: all var(--transition-base);
}

.metric-label {
  font-size: var(--text-sm);
  color: var(--text-color);
  opacity: 0.7;
  margin-bottom: var(--spacing-sm);
}

.metric-value {
  font-size: 2rem; /* Оставляем — это осознанный акцент */
  font-weight: bold;
  margin-bottom: var(--spacing-xs);
  color: var(--text-color);
}

.metric-detail {
  font-size: var(--text-base);
  color: var(--text-color);
  opacity: 0.8;
}

/* Используем семантические переменные вместо хардкода цветов */
.severity-success {
  border-color: var(--color-success);
  background-color: var(--color-success-bg);
}
.severity-warning {
  border-color: var(--color-warning);
  background-color: var(--color-warning-bg);
}
.severity-error {
  border-color: var(--color-error);
  background-color: var(--color-error-bg);
}

.result-assessment {
  margin-top: var(--spacing-md);
}
</style>
