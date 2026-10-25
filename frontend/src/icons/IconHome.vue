<template>
  <svg
    viewBox="0 0 25 25"
    width="25"
    height="30"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    style="margin-right: 8px"
  >
    <!-- Источник питания -->
    <circle cx="12.5" cy="1.5" r="2.5" />
    <!-- Линия от источника -->
    <line x1="12.5" y1="3" x2="12.5" y2="12" />
    <!-- Горизонтальная шина -->
    <line x1="0" y1="12" x2="25" y2="12" />
    <!-- Вертикальный отвод к нагрузке -->
    <line x1="6.5" y1="12" x2="6.5" y2="21" />
    <line x1="18" y1="12" x2="18" y2="21" />
    <!-- Нагрузка (квадрат) -->
    <rect x="16" y="21" width="4" height="4" rx="0.5" />
    <rect x="4.5" y="21" width="4" height="4" rx="0.5" />
    <!-- Узлы соединения -->
    <circle cx="6.5" cy="12" r="1" fill="currentColor" />
    <circle cx="12.5" cy="12" r="1" fill="currentColor" />
    <circle cx="18" cy="12" r="1" fill="currentColor" />
  </svg>
</template>
