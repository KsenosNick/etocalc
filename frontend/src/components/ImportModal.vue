<template>
  <Dialog
    :visible="visible"
    header="Импорт кабелей"
    modal
    :style="{ width: '500px' }"
    @update:visible="$emit('update:visible', $event)"
    @hide="$emit('cancel')"
  >
    <div class="import-form">
      <div class="form-field">
        <label>Файл экспорта (JSON)</label>
        <FileUpload
          mode="basic"
          accept=".json"
          :maxFileSize="10000000"
          @select="onFileSelect"
          chooseLabel="Выбрать файл"
        />
      </div>

      <div class="form-field">
        <label>Режим импорта</label>
        <SelectButton
          v-model="importMode"
          :options="importModes"
          optionLabel="label"
          optionValue="value"
        />
        <small class="hint">{{ importModeDescription }}</small>
      </div>

      <div class="form-actions">
        <Button label="Отмена" severity="secondary" @click="$emit('cancel')" />
        <Button
          label="Импортировать"
          @click="handleImport"
          :loading="loading"
          :disabled="!selectedFile"
        />
      </div>
    </div>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import Dialog from 'primevue/dialog'
import FileUpload from 'primevue/fileupload'
import SelectButton from 'primevue/selectbutton'
import Button from 'primevue/button'
import { ImportMode } from '../types'

defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'import', file: File, mode: ImportMode): void
  (e: 'cancel'): void
}>()

const selectedFile = ref<File | null>(null)
const importMode = ref(ImportMode.SKIP)
const loading = ref(false)

const importModes = [
  { label: 'Пропустить существующие', value: ImportMode.SKIP },
  { label: 'Обновить существующие', value: ImportMode.UPDATE },
  { label: 'Создать дубликаты', value: ImportMode.DUPLICATE },
]

const importModeDescription = computed(() => {
  switch (importMode.value) {
    case ImportMode.SKIP:
      return 'Если кабель уже есть в базе, он будет пропущен'
    case ImportMode.UPDATE:
      return 'Существующие кабели будут перезаписаны данными из файла'
    case ImportMode.DUPLICATE:
      return 'Все кабели будут созданы как новые записи'
  }
})

function onFileSelect(event: { files: File[] }) {
  const file = event.files[0]
  if (file) {
    selectedFile.value = file
  }
}

function handleImport() {
  if (selectedFile.value) {
    emit('import', selectedFile.value, importMode.value)
    emit('update:visible', false)
  }
}
</script>

<style scoped>
.import-form {
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

.hint {
  font-size: var(--text-xs);
  color: var(--text-color);
  opacity: 0.6;
  margin-top: var(--spacing-xs);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-lg);
}
</style>
