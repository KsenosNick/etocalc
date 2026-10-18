<template>
  <div>
    <header class="app-header">
      <div class="header-content">
        <h1>⚡ Электротехнический калькулятор</h1>
        <Button
          :icon="isDarkMode ? 'pi pi-sun' : 'pi pi-moon'"
          @click="toggleTheme"
          severity="secondary"
          rounded
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
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 1rem 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
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
  max-width: 1400px;
  margin: 2rem auto;
  padding: 0 2rem;
}
</style>
