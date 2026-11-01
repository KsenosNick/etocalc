<template>
  <div class="catalog-manager">
    <div class="toolbar">
      <div class="search-bar">
        <InputText
          v-model="filter.search"
          placeholder="Поиск: ВВГ, 3х1.5, 660..."
          class="w-full"
          @keyup.enter="loadCables"
        />
        <Button icon="pi pi-search" @click="loadCables" />
      </div>

      <div class="actions">
        <Button label="Добавить кабель" icon="pi pi-plus" @click="openFormModal()" />
        <Button
          label="Импорт"
          icon="pi pi-upload"
          severity="secondary"
          @click="showImportModal = true"
        />
        <Button
          label="Экспорт всех"
          icon="pi pi-download"
          severity="secondary"
          @click="handleExportAll"
        />
        <Button
          v-if="selectedCables.length > 0"
          :label="`Экспорт выбранных (${selectedCables.length})`"
          icon="pi pi-download"
          severity="secondary"
          @click="handleExportSelected"
        />
        <Button
          v-if="selectedCables.length > 0"
          :label="`Удалить выбранные (${selectedCables.length})`"
          icon="pi pi-trash"
          severity="danger"
          @click="handleBulkDelete"
        />
      </div>
    </div>

    <DataTable
      v-model:selection="selectedCables"
      :value="cables"
      :loading="loading"
      selectionMode="multiple"
      :paginator="true"
      :rows="pageSize"
      :totalRecords="totalRecords"
      :lazy="true"
      :sortField="sortField"
      :sortOrder="sortOrder"
      @page="onPage"
      @sort="onSort"
      stripedRows
      class="cable-table"
    >
      <Column selectionMode="multiple" headerStyle="width: 3rem" />
      <Column field="fullName" header="Кабель" sortable />
      <Column field="cableTypeName" header="Тип" sortable />
      <Column field="conductorMaterialName" header="Материал" sortable />
      <Column field="nominalVoltage" header="Напряжение, В" sortable />
      <Column field="conductorCount" header="Жил" sortable />
      <Column field="crossSection" header="Сечение, мм²" sortable />
      <Column field="maxCurrentEarth" header="I макс (земля), А" sortable />
      <Column field="maxCurrentAir" header="I макс (воздух), А" sortable />
      <Column header="Действия" headerStyle="width: 8rem">
        <template #body="slotProps">
          <div class="action-buttons">
            <Button
              icon="pi pi-pencil"
              severity="info"
              size="small"
              text
              @click="openFormModal(slotProps.data.id)"
            />
            <Button
              icon="pi pi-trash"
              severity="danger"
              size="small"
              text
              @click="handleDelete(slotProps.data.id)"
            />
          </div>
        </template>
      </Column>
    </DataTable>

    <CableFormModal
      :visible="showFormModal"
      :cableId="editingCableId"
      @update:visible="showFormModal = $event"
      @submit="handleFormSubmit"
      @cancel="showFormModal = false"
    />

    <ImportModal
      :visible="showImportModal"
      @update:visible="showImportModal = $event"
      @import="handleImport"
      @cancel="showImportModal = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useToast } from 'primevue/usetoast'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import CableFormModal from './CableFormModal.vue'
import ImportModal from './ImportModal.vue'
import type { DataTablePageEvent, DataTableSortEvent } from 'primevue/datatable'
import type {
  Cable,
  CableRequest,
  CableFilter,
  ImportMode,
  ExportFile,
  CableExport,
} from '../types'
import {
  getCables,
  createCable,
  updateCable,
  deleteCable,
  deleteCables,
  exportAllCables,
  exportSelectedCables,
  importCables,
} from '../api/catalog'

const toast = useToast()

const cables = ref<Cable[]>([])
const loading = ref(false)
const totalRecords = ref(0)
const pageSize = ref(50)
const currentPage = ref(0)

const filter = ref<CableFilter>({
  search: '',
})

const selectedCables = ref<Cable[]>([])

const showFormModal = ref(false)
const editingCableId = ref<number | undefined>()

const showImportModal = ref(false)

const sortField = ref<string>('fullName')
const sortOrder = ref<number>(1)

// Маппинг полей таблицы на пути свойств JPA-сущности
const SORT_FIELD_MAP: Record<string, string> = {
  cableTypeName: 'cableType.name',
  conductorMaterialName: 'cableType.conductorMaterial.name',
  nominalVoltage: 'nominalVoltage',
  conductorCount: 'conductorCount',
  crossSection: 'crossSection',
  maxCurrentEarth: 'maxCurrentEarth',
  maxCurrentAir: 'maxCurrentAir',
}

// fullName — вычисляемый, поэтому сортируем по его составляющим
function buildSortParams(): string[] {
  const dir = sortOrder.value === -1 ? 'desc' : 'asc'
  if (sortField.value === 'fullName') {
    return [`cableType.name,${dir}`, `conductorCount,${dir}`, `crossSection,${dir}`]
  }
  const backendField = SORT_FIELD_MAP[sortField.value] ?? sortField.value
  return [`${backendField},${dir}`]
}
onMounted(() => {
  loadCables()
})

async function loadCables() {
  loading.value = true
  try {
    const response = await getCables(
      filter.value,
      currentPage.value,
      pageSize.value,
      buildSortParams(),
    )
    cables.value = response.content
    totalRecords.value = response.totalElements
  } catch {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Не удалось загрузить каталог кабелей',
      life: 3000,
    })
  } finally {
    loading.value = false
  }
}

function onSort(event: DataTableSortEvent) {
  sortField.value = (event.sortField as string) || 'fullName'
  sortOrder.value = event.sortOrder ?? 1
  currentPage.value = 0 // при смене сортировки возвращаемся на первую страницу
  loadCables()
}

function onPage(event: DataTablePageEvent) {
  currentPage.value = event.page
  loadCables()
}

function openFormModal(cableId?: number) {
  editingCableId.value = cableId
  showFormModal.value = true
}

async function handleFormSubmit(request: CableRequest) {
  try {
    if (editingCableId.value) {
      await updateCable(editingCableId.value, request)
      toast.add({
        severity: 'success',
        summary: 'Кабель обновлён',
        life: 3000,
      })
    } else {
      await createCable(request)
      toast.add({
        severity: 'success',
        summary: 'Кабель создан',
        life: 3000,
      })
    }
    showFormModal.value = false
    editingCableId.value = undefined
    loadCables()
  } catch {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Не удалось сохранить кабель',
      life: 3000,
    })
  }
}

async function handleDelete(id: number) {
  if (!confirm('Удалить этот кабель?')) return

  try {
    await deleteCable(id)
    toast.add({
      severity: 'success',
      summary: 'Кабель удалён',
      life: 3000,
    })
    loadCables()
  } catch {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Не удалось удалить кабель',
      life: 3000,
    })
  }
}

async function handleBulkDelete() {
  if (!confirm(`Удалить ${selectedCables.value.length} кабелей?`)) return

  try {
    const ids = selectedCables.value.map((c) => c.id)
    await deleteCables(ids)
    toast.add({
      severity: 'success',
      summary: `Удалено кабелей: ${ids.length}`,
      life: 3000,
    })
    selectedCables.value = []
    loadCables()
  } catch {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Не удалось удалить кабели',
      life: 3000,
    })
  }
}

async function handleExportAll() {
  try {
    const data = await exportAllCables()
    downloadJson(data, 'cables_all.json')
    toast.add({
      severity: 'success',
      summary: 'Экспорт завершён',
      life: 3000,
    })
  } catch {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Не удалось экспортировать кабели',
      life: 3000,
    })
  }
}

async function handleExportSelected() {
  try {
    const ids = selectedCables.value.map((c) => c.id)
    const data = await exportSelectedCables(ids)
    downloadJson(data, 'cables_selected.json')
    toast.add({
      severity: 'success',
      summary: 'Экспорт завершён',
      life: 3000,
    })
  } catch {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Не удалось экспортировать кабели',
      life: 3000,
    })
  }
}

async function handleImport(file: File, mode: ImportMode) {
  try {
    const text = await file.text()
    const data = JSON.parse(text)
    const result = await importCables(data, mode)

    toast.add({
      severity: 'success',
      summary: 'Импорт завершён',
      detail: `Создано: ${result.created}, Обновлено: ${result.updated}, Пропущено: ${result.skipped}, Ошибок: ${result.failed}`,
      life: 5000,
    })

    if (result.errors.length > 0) {
      console.error('Ошибки импорта:', result.errors)
    }

    loadCables()
  } catch {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Не удалось импортировать кабели',
      life: 3000,
    })
  }
}

function downloadJson(data: ExportFile<CableExport>, filename: string) {
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.catalog-manager {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.search-bar {
  display: flex;
  gap: var(--spacing-sm);
  flex: 1;
  max-width: 400px;
}

.actions {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.cable-table {
  width: 100%;
}

.action-buttons {
  display: flex;
  gap: var(--spacing-xs);
  justify-content: center;
}

.w-full {
  width: 100%;
}
</style>
