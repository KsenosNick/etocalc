// Типы данных, соответствующие DTO бэкенда

export enum CalculationSourceType {
  CABLE = 'CABLE',
  BUSBAR = 'BUSBAR',
  TROLLEY = 'TROLLEY',
}

export interface CableType {
  id: number
  name: string
}

export interface Cable {
  id: number
  cableType: CableType
  nominalVoltage: number
  conductorCount: number
  crossSection: number
  rPhActive: number
  xPhReactive: number
  maxCurrentEarth: number
  maxCurrentAir: number
}

export interface VoltageDropRequest {
  sourceType: CalculationSourceType
  sourceId: number
  networkVoltage: number
  targetVoltage: number
  current: number
  power?: number
  length: number
  cosPhi: number
  ambientTemperature: number
  parallelCableCount: number
}

export interface CalculationStep {
  order: number
  key: string
  title: string
  formula: string
  expression: string
  result: string
  unit: string
}

export interface VoltageDropResponse {
  cableName: string
  deltaUVolts: number
  deltaUPercent: number
  endVoltage: number
  targetVoltage: number
  steps: CalculationStep[]
}

// === КАТАЛОГ КАБЕЛЕЙ ===

export interface CableType {
  id: number
  name: string
  conductorMaterialName: string
  insulationMaterialName: string
  sheathMaterialName: string
  hasArmor: boolean
}

export interface Cable {
  id: number
  cableTypeId: number
  cableTypeName: string
  conductorMaterialName: string
  nominalVoltage: number
  conductorCount: number
  crossSection: number
  rPhActive: number
  r0PhActive: number
  xPhReactive: number
  x0Reactive: number
  maxCurrentEarth: number
  maxCurrentAir: number
  fullName: string
}

export interface CableRequest {
  cableTypeId: number
  nominalVoltage: number
  conductorCount: number
  crossSection: number
  rPhActive: number
  r0PhActive: number
  xPhReactive: number
  x0Reactive: number
  maxCurrentEarth: number
  maxCurrentAir: number
}

export interface CableExport {
  cableTypeName: string
  nominalVoltage: number
  conductorCount: number
  crossSection: number
  rPhActive: number
  r0PhActive: number
  xPhReactive: number
  x0Reactive: number
  maxCurrentEarth: number
  maxCurrentAir: number
}

export interface ExportFile<T> {
  entityType: string
  formatVersion: number
  exportedAt: string
  items: T[]
}

export interface ImportResult {
  created: number
  updated: number
  skipped: number
  failed: number
  errors: string[]
}

export enum ImportMode {
  SKIP = 'SKIP',
  UPDATE = 'UPDATE',
  DUPLICATE = 'DUPLICATE',
}

export interface PageResponse<T> {
  content: T[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface CableFilter {
  search?: string
  conductorCount?: number
  minCrossSection?: number
  maxCrossSection?: number
}
