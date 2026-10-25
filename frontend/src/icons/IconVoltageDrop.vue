<template>
  <svg
    viewBox="0 0 24 24"
    width="25"
    height="30"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    style="margin-right: 8px"
  >
    <!-- Линия кабеля -->
    <line x1="0" y1="20" x2="25" y2="20" />
    <!-- Треугольник слева (вершиной внутрь, касается линии) -->
    <path d="M0 15 L8 20 L0 25 Z" fill="currentColor" stroke="none" />
    <!-- Треугольник справа (вершиной внутрь, касается линии) -->
    <path d="M25 15 L17 20 L25 25 Z" fill="currentColor" stroke="none" />
    <!-- ΔU над кабелем (крупнее) -->
    <text
      x="12"
      y="12"
      font-size="12"
      text-anchor="middle"
      fill="currentColor"
      stroke="none"
      font-family="sans-serif"
      font-weight="bold"
      style="letter-spacing: -0.3px"
    >
      ΔU
    </text>
  </svg>
</template>
