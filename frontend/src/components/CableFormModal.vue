<template>
  <Dialog
    :visible="visible"
    :header="cableId ? 'Редактировать кабель' : 'Новый кабель'"
    modal
    :style="{ width: '600px' }"
    @update:visible="$emit('update:visible', $event)"
    @hide="$emit('cancel')"
  >
    <form @submit.prevent="handleSubmit" class="cable-form">
      <div class="form-field">
        <label>Тип кабеля</label>
        <Dropdown
          v-model="form.cableTypeId"
          :options="cableTypes"
          optionLabel="name"
          optionValue="id"
          placeholder="Выберите тип кабеля"
          class="w-full"
          required
        />
      </div>

      <div class="form-row">
        <div class="form-field">
          <label>Номинальное напряжение, В</label>
          <InputNumber
            v-model="form.nominalVoltage"
            :min="100"
            :max="1000"
            :step="100"
            class="w-full"
            required
          />
        </div>
        <div class="form-field">
          <label>Количество жил</label>
          <InputNumber
            v-model="form.conductorCount"
            :min="1"
            :max="10"
            :step="1"
            class="w-full"
            required
          />
        </div>
      </div>

      <div class="form-field">
        <label>Сечение жилы, мм²</label>
        <InputNumber
          v-model="form.crossSection"
          :min="0.5"
          :max="1000"
          :step="0.5"
          :minFractionDigits="2"
          :maxFractionDigits="2"
          class="w-full"
          required
        />
      </div>

      <div class="form-row">
        <div class="form-field">
          <label>R активного (фаза), Ом/км</label>
          <InputNumber
            v-model="form.rPhActive"
            :min="0"
            :step="0.01"
            :minFractionDigits="6"
            :maxFractionDigits="6"
            class="w-full"
            required
          />
        </div>
        <div class="form-field">
          <label>R активного (ноль), Ом/км</label>
          <InputNumber
            v-model="form.r0PhActive"
            :min="0"
            :step="0.01"
            :minFractionDigits="6"
            :maxFractionDigits="6"
            class="w-full"
            required
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-field">
          <label>X реактивного (фаза), Ом/км</label>
          <InputNumber
            v-model="form.xPhReactive"
            :min="0"
            :step="0.01"
            :minFractionDigits="6"
            :maxFractionDigits="6"
            class="w-full"
            required
          />
        </div>
        <div class="form-field">
          <label>X реактивного (ноль), Ом/км</label>
          <InputNumber
            v-model="form.x0Reactive"
            :min="0"
            :step="0.01"
            :minFractionDigits="6"
            :maxFractionDigits="6"
            class="w-full"
            required
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-field">
          <label>Макс. ток (в земле), А</label>
          <InputNumber
            v-model="form.maxCurrentEarth"
            :min="1"
            :max="10000"
            :step="1"
            class="w-full"
            required
          />
        </div>
        <div class="form-field">
          <label>Макс. ток (в воздухе), А</label>
          <InputNumber
            v-model="form.maxCurrentAir"
            :min="1"
            :max="10000"
            :step="1"
            class="w-full"
            required
          />
        </div>
      </div>

      <div class="form-actions">
        <Button label="Отмена" severity="secondary" @click="$emit('cancel')" />
        <Button :label="cableId ? 'Сохранить' : 'Создать'" type="submit" :loading="loading" />
      </div>
    </form>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import Dialog from 'primevue/dialog'
import Dropdown from 'primevue/dropdown'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import type { CableRequest, CableType } from '../types'
import { getCableTypes, getCableById } from '../api/catalog'

const props = defineProps<{
  visible: boolean
  cableId?: number
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'submit', request: CableRequest): void
  (e: 'cancel'): void
}>()

const cableTypes = ref<CableType[]>([])
const loading = ref(false)

const form = ref<CableRequest>({
  cableTypeId: 0,
  nominalVoltage: 380,
  conductorCount: 3,
  crossSection: 2.5,
  rPhActive: 7.41,
  r0PhActive: 7.41,
  xPhReactive: 0.08,
  x0Reactive: 0.08,
  maxCurrentEarth: 30,
  maxCurrentAir: 25,
})

onMounted(async () => {
  try {
    cableTypes.value = await getCableTypes()
  } catch (error) {
    console.error('Ошибка загрузки типов кабелей:', error)
  }
})

watch(
  () => props.cableId,
  async (newId) => {
    if (newId && props.visible) {
      loading.value = true
      try {
        const cable = await getCableById(newId)
        form.value = {
          cableTypeId: cable.cableTypeId,
          nominalVoltage: cable.nominalVoltage,
          conductorCount: cable.conductorCount,
          crossSection: cable.crossSection,
          rPhActive: cable.rPhActive,
          r0PhActive: cable.r0PhActive,
          xPhReactive: cable.xPhReactive,
          x0Reactive: cable.x0Reactive,
          maxCurrentEarth: cable.maxCurrentEarth,
          maxCurrentAir: cable.maxCurrentAir,
        }
      } catch (error) {
        console.error('Ошибка загрузки кабеля:', error)
      } finally {
        loading.value = false
      }
    }
  },
  { immediate: true },
)

function handleSubmit() {
  emit('submit', { ...form.value })
  emit('update:visible', false)
}
</script>

<style scoped>
.cable-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-lg);
}

.w-full {
  width: 100%;
}
</style>
