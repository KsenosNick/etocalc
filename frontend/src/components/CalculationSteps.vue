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
  border-radius: 8px;
  border: 1px solid var(--border-color);
  padding: 1rem;
}

.table-wrapper {
  width: 100%;
  overflow-x: auto; /* Горизонтальный скролл на маленьких экранах */
}

.steps-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
  min-width: 800px; /* Гарантирует, что колонки не сожмутся слишком сильно */
}

.steps-table th,
.steps-table td {
  padding: 0.75rem 1rem;
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

/* Контрастные стили для кода формул */
.formula,
.expression {
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9rem;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
  white-space: nowrap; /* Запрещаем перенос строк внутри формулы */
}

/* Светлая тема */
:root:not(.dark-mode) .formula {
  background-color: #e9ecef;
  color: #212529;
}
:root:not(.dark-mode) .expression {
  background-color: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
}

/* Тёмная тема */
:root.dark-mode .formula {
  background-color: #2d2d30;
  color: #e0e0e0;
}
:root.dark-mode .expression {
  background-color: #1e1e1e;
  color: #b0b0b0;
  border: 1px solid #333333;
}

.result-value {
  font-size: 1.05rem;
  color: var(--text-color);
}

.unit {
  margin-left: 6px;
  color: var(--text-color);
  opacity: 0.6;
  font-weight: normal;
  font-size: 0.85rem;
}
</style>
