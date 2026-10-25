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

<style>
:root {
  --surface-ground: #f8f9fa;
  --surface-card: #ffffff;
  --text-color: #495057;
  --border-color: #dee2e6;
}

/* Применяем тёмную тему к корневому элементу <html> */
html.dark-mode {
  --surface-ground: #121212;
  --surface-card: #1e1e1e;
  --text-color: #ffffffde;
  --border-color: #333333;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: var(--surface-ground);
  color: var(--text-color);
  transition:
    background-color 0.3s,
    color 0.3s;
}

.app-header {
  background-color: var(--surface-card);
  border-bottom: 1px solid var(--border-color);
  padding: 0 2rem;
  height: 60px;
  display: flex;
  justify-content: space-between; /* ← РАЗНОСИМ ЭЛЕМЕНТЫ ПО КРАЯМ */
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left h1 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
}
.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  margin: 0;
  font-size: 1.5rem;
}

.app-main {
  max-width: 100%;
  margin: 0;
  padding: 0;
  height: calc(100vh - 60px);
  overflow: hidden;
}
</style>
