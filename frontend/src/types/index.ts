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
