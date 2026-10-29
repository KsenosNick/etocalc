<template>
  <div class="workspace">
    <Tabs v-model:value="activeTab" class="ribbon-tabs">
      <TabList>
        <Tab value="home">
          <!-- Иконка: Однолинейная схема (как на странице "Построитель схем") -->
          <IconHome />
          <span>Главная</span>
        </Tab>

        <Tab value="voltage-drop">
          <!-- Иконка: Кабель с ΔU (крупнее) -->
          <IconVoltageDrop />
          <span>Падение напряжения</span>
        </Tab>

        <Tab value="short-circuit">
          <!-- Иконка: ТКЗ (молния, растянутая по высоте) -->
          <IconShortCircuite />
          <span>ТКЗ</span>
        </Tab>

        <Tab value="catalog">
          <!-- Иконка: Справочники (База данных) -->
          <IconCatalog />
          <span>База данных</span>
        </Tab>
      </TabList>

      <TabPanels class="ribbon-panels">
        <!-- Вкладка: Главная (будущий редактор схем) -->
        <TabPanel value="home">
          <div class="placeholder-panel">
            <i
              class="pi pi-sitemap"
              style="font-size: 4rem; margin-bottom: 1rem; color: var(--text-color); opacity: 0.5"
            ></i>
            <h3>Построитель однолинейных схем</h3>
            <p>Здесь будет визуальный редактор схем на базе vue-flow.</p>
            <p class="hint">Пока используйте вкладку "Падение напряжения" для точечных расчётов.</p>
          </div>
        </TabPanel>

        <!-- Вкладка: Падение напряжения (наш текущий калькулятор) -->
        <TabPanel value="voltage-drop">
          <CalculatorView />
        </TabPanel>

        <!-- Вкладка: ТКЗ (заглушка) -->
        <TabPanel value="short-circuit">
          <div class="placeholder-panel">
            <i
              class="pi pi-exclamation-triangle"
              style="font-size: 4rem; margin-bottom: 1rem; color: var(--text-color); opacity: 0.5"
            ></i>
            <h3>Расчёт токов короткого замыкания</h3>
            <p>Модуль находится в разработке.</p>
          </div>
        </TabPanel>

        <!-- Вкладка: Справочники (заглушка) -->
        <TabPanel value="catalog">
          <div class="placeholder-panel">
            <i
              class="pi pi-database"
              style="font-size: 4rem; margin-bottom: 1rem; color: var(--text-color); opacity: 0.5"
            ></i>
            <h3>Управление справочниками</h3>
            <p>Редактирование каталога кабелей, материалов и параметров сетей.</p>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Tabs from 'primevue/tabs'
import TabList from 'primevue/tablist'
import Tab from 'primevue/tab'
import TabPanels from 'primevue/tabpanels'
import TabPanel from 'primevue/tabpanel'
import CalculatorView from './CalculatorView.vue'
import IconShortCircuite from '@/icons/IconShortCircuite.vue'
import IconVoltageDrop from '@/icons/IconVoltageDrop.vue'
import IconHome from '@/icons/IconHome.vue'
import IconCatalog from '@/icons/IconCatalog.vue'

const activeTab = ref('voltage-drop') // По умолчанию открываем рабочий калькулятор
</script>

<style scoped>
.workspace {
  width: 100%;
  height: calc(100vh - var(--header-height));
  display: flex;
  flex-direction: column;
}

.ribbon-tabs {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.p-tablist) {
  background-color: var(--surface-card);
  border-bottom: 1px solid var(--border-color);
  padding: 0 var(--spacing-md);
}

:deep(.p-tab) {
  border: none;
  background: transparent;
  color: var(--text-color);
  opacity: 0.7;
  font-weight: 500;
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-md) var(--border-radius-md) 0 0;
  transition: all var(--transition-base);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

:deep(.p-tab:hover) {
  background-color: var(--surface-ground);
  opacity: 1;
}

:deep(.p-tab.p-tab-active) {
  background-color: var(--surface-ground);
  color: var(--text-color);
  opacity: 1;
  border-bottom: 2px solid var(--surface-ground);
  margin-bottom: -1px;
}

:deep(.p-tabpanels) {
  flex: 1;
  background-color: var(--surface-ground);
  padding: var(--spacing-lg);
  overflow-y: auto;
}

.placeholder-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: var(--text-color);
  opacity: 0.8;
}

.placeholder-panel h3 {
  margin: 0 0 var(--spacing-sm) 0;
  font-size: var(--text-2xl);
}

.placeholder-panel .hint {
  margin-top: var(--spacing-md);
  font-size: var(--text-sm);
  color: var(--text-color);
  opacity: 0.6;
  background-color: var(--surface-card);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-color);
}
</style>
