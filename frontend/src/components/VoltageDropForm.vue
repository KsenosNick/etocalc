<template>
  <Card>
    <template #title>Расчёт падения напряжения</template>
    <template #content>
      <form @submit.prevent="handleSubmit" class="form">
        <CableSelector @select="handleCableSelect" />

        <!-- Ряд 1: Напряжения -->
        <div class="form-row">
          <div class="form-field">
            <label for="networkVoltage">Напряжение в начале участка, В</label>
            <InputNumber
              id="networkVoltage"
              v-model="form.networkVoltage"
              :min="10"
              :max="1000"
              :step="10"
              class="w-full"
            />
          </div>

          <div class="form-field">
            <label for="targetVoltage">Номинальное напряжение потребителя, В</label>
            <InputNumber
              id="targetVoltage"
              v-model="form.targetVoltage"
              :min="10"
              :max="1000"
              :step="10"
              class="w-full"
            />
          </div>
        </div>

        <!-- Ряд 2: Переключатель режима -->
        <div class="mode-switcher">
          <Button
            :label="'Ток (А)'"
            :severity="inputMode === 'current' ? 'primary' : 'secondary'"
            size="small"
            @click="inputMode = 'current'"
            class="mode-btn"
          />
          <Button
            :label="'Мощность (кВт)'"
            :severity="inputMode === 'power' ? 'primary' : 'secondary'"
            size="small"
            @click="inputMode = 'power'"
            class="mode-btn"
          />
        </div>

        <!-- Ряд 3: Ток / Мощность и расчётное поле -->
        <div class="form-row">
          <!-- Активное поле ввода -->
          <div v-if="inputMode === 'current'" class="form-field">
            <label for="current">Ток нагрузки, А</label>
            <InputNumber
              id="current"
              v-model="form.current"
              :min="0.1"
              :max="10000"
              :step="0.1"
              :minFractionDigits="1"
              :maxFractionDigits="2"
              class="w-full"
            />
          </div>

          <div v-else class="form-field">
            <label for="power">Активная мощность, кВт</label>
            <InputNumber
              id="power"
              v-model="form.power"
              :min="0.1"
              :max="10000"
              :step="0.1"
              :minFractionDigits="1"
              :maxFractionDigits="2"
              class="w-full"
            />
          </div>

          <!-- Расчётное поле (всегда справа) -->
          <div class="form-field">
            <label>{{ inputMode === 'current' ? 'Мощность (расч.)' : 'Ток (расч.)' }}</label>
            <InputNumber
              :modelValue="inputMode === 'current' ? form.power : form.current"
              readonly
              disabled
              :minFractionDigits="2"
              :maxFractionDigits="2"
              class="w-full calculated-field"
            />
          </div>
        </div>

        <!-- Ряд 4: Длина и cosφ -->
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

        <!-- Ряд 5: Температура и параллельные кабели -->
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
import { ref, watch } from 'vue'
import Card from 'primevue/card'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import CableSelector from './CableSelector.vue'
import type { Cable, VoltageDropRequest } from '../types'
import { CalculationSourceType } from '../types'

const emit = defineEmits<{
  (e: 'calculate', request: VoltageDropRequest): void
}>()

defineProps<{ loading: boolean }>()

const selectedCable = ref<Cable | null>(null)

const inputMode = ref<'current' | 'power'>('current')

const form = ref({
  networkVoltage: 230,
  targetVoltage: 220,
  current: 16,
  power: 3.3,
  length: 50,
  cosPhi: 0.9,
  ambientTemperature: 25,
  parallelCableCount: 1,
})

function handleCableSelect(cable: Cable | null) {
  selectedCable.value = cable
  if (cable) {
    const isThreePhase = cable.conductorCount > 3
    form.value.networkVoltage = isThreePhase ? 400 : 230
    form.value.targetVoltage = isThreePhase ? 380 : 220

    recalculatePowerFromCurrent()
  }
}

// Формулы пересчёта
function recalculatePowerFromCurrent() {
  const u = form.value.networkVoltage / 1000
  const is3ph = form.value.networkVoltage >= 380
  const k = is3ph ? Math.sqrt(3) : 1
  form.value.power = parseFloat((k * u * form.value.current * form.value.cosPhi).toFixed(2))
}

function recalculateCurrentFromPower() {
  const u = form.value.networkVoltage / 1000
  const is3ph = form.value.networkVoltage >= 380
  const k = is3ph ? Math.sqrt(3) : 1
  form.value.current = parseFloat((form.value.power! / (k * u * form.value.cosPhi)).toFixed(2))
}

// Watchers для автоматического пересчёта
watch(
  () => [form.value.current, form.value.networkVoltage, form.value.cosPhi],
  () => {
    if (inputMode.value === 'current') recalculatePowerFromCurrent()
  },
)

watch(
  () => [form.value.power, form.value.networkVoltage, form.value.cosPhi],
  () => {
    if (inputMode.value === 'power') recalculateCurrentFromPower()
  },
)

function handleSubmit() {
  if (!selectedCable.value) return

  const request: VoltageDropRequest = {
    sourceType: CalculationSourceType.CABLE,
    sourceId: selectedCable.value.id,
    networkVoltage: form.value.networkVoltage,
    targetVoltage: form.value.targetVoltage,
    current: form.value.current, // Всегда отправляем ток
    power: inputMode.value === 'power' ? form.value.power : undefined,
    length: form.value.length,
    cosPhi: form.value.cosPhi,
    ambientTemperature: form.value.ambientTemperature,
    parallelCableCount: form.value.parallelCableCount,
  }

  emit('calculate', request)
}
</script>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-field label {
  font-weight: 500;
  font-size: var(--text-sm);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.submit-button {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-lg);
  margin-top: var(--spacing-sm);
}

.w-full {
  width: 100%;
}

.mode-switcher {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: center;
  margin-top: calc(var(--spacing-sm) * -1);
}

.mode-btn {
  flex: 1;
}

.calculated-field :deep(input) {
  background-color: var(--surface-ground);
  color: var(--text-color);
  opacity: 0.7;
  cursor: not-allowed;
}

.hint {
  font-size: var(--text-xs);
  color: var(--text-color);
  opacity: 0.6;
  margin-top: var(--spacing-xs);
}
</style>
