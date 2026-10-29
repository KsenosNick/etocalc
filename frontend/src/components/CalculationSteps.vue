<template>
  <div class="steps-container">
    <div class="table-wrapper">
      <table class="steps-table">
        <thead>
          <tr>
            <th style="width: 50px">№</th>
            <th style="width: 25%">Описание</th>
            <th style="width: 25%">Формула</th>
            <th style="width: 30%">Вычисление</th>
            <th style="width: 20%">Результат</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="step in steps" :key="step.order">
            <td class="text-center">{{ step.order }}</td>
            <td>{{ step.title }}</td>
            <td>
              <code class="formula">{{ step.formula }}</code>
            </td>
            <td>
              <code class="expression">{{ step.expression }}</code>
            </td>
            <td>
              <strong class="result-value">{{ step.result }}</strong>
              <span v-if="step.unit" class="unit">{{ step.unit }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CalculationStep } from '../types'

defineProps<{
  steps: CalculationStep[]
}>()
</script>

<style scoped>
.steps-container {
  width: 100%;
  background-color: var(--surface-card);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color);
  padding: var(--spacing-md);
}

.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

.steps-table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--text-sm);
  min-width: 800px;
}

.steps-table th,
.steps-table td {
  padding: var(--spacing-sm) var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  text-align: left;
  vertical-align: middle;
}

.steps-table th {
  background-color: var(--surface-ground);
  font-weight: 600;
  color: var(--text-color);
  position: sticky;
  top: 0;
}

.steps-table tbody tr:hover {
  background-color: var(--surface-ground);
}

.text-center {
  text-align: center;
}

.formula,
.expression {
  font-family: 'Courier New', Courier, monospace;
  font-size: var(--text-sm);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  display: inline-block;
  white-space: nowrap;
}

.formula {
  background-color: var(--formula-bg);
  color: var(--formula-text);
}

.expression {
  background-color: var(--expression-bg);
  color: var(--expression-text);
  border: 1px solid var(--expression-border);
}

.result-value {
  font-size: var(--text-base);
  color: var(--text-color);
}

.unit {
  margin-left: var(--spacing-sm);
  color: var(--text-color);
  opacity: 0.6;
  font-weight: normal;
  font-size: var(--text-xs);
}
</style>
