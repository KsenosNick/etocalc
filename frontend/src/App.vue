<template>
  <div>
    <header class="app-header">
      <div class="header-left">
        <h1>⚡ Электротехнический калькулятор</h1>
      </div>
      <div class="header-right">
        <Button
          :icon="isDarkMode ? 'pi pi-sun' : 'pi pi-moon'"
          @click="toggleTheme"
          severity="secondary"
          rounded
          text
          v-tooltip="isDarkMode ? 'Светлая тема' : 'Тёмная тема'"
        />
      </div>
    </header>

    <main class="app-main">
      <RouterView />
    </main>

    <Toast />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterView } from 'vue-router'
import Button from 'primevue/button'
import Toast from 'primevue/toast'

const isDarkMode = ref(true)

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  isDarkMode.value = savedTheme ? savedTheme === 'dark' : true
  applyTheme()
})

function applyTheme() {
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark-mode')
  } else {
    document.documentElement.classList.remove('dark-mode')
  }
}

function toggleTheme() {
  isDarkMode.value = !isDarkMode.value
  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light')
  applyTheme()
}
</script>

<style scoped>
.app-header {
  background-color: var(--surface-card);
  border-bottom: 1px solid var(--border-color);
  padding: 0 var(--spacing-xl);
  height: var(--header-height);
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
}

.header-left h1 {
  margin: 0;
  font-size: var(--text-xl);
  font-weight: 600;
  color: var(--text-color);
}

.header-right {
  display: flex;
  align-items: center;
}

.app-main {
  max-width: 100%;
  margin: 0;
  padding: 0;
  height: calc(100vh - var(--header-height));
  overflow: hidden;
}
</style>
