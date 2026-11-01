import axios from 'axios'
import type {
  Cable,
  CableRequest,
  CableType,
  PageResponse,
  CableFilter,
  ExportFile,
  CableExport,
  ImportResult,
  ImportMode,
} from '../types'

const API_BASE = '/api/catalog/cables'

export async function getCables(
  filter: CableFilter,
  page: number = 0,
  size: number = 50,
  sort: string | string[] = 'fullName,asc',
): Promise<PageResponse<Cable>> {
  const searchParams = new URLSearchParams()
  searchParams.set('page', String(page))
  searchParams.set('size', String(size))

  // Повторяющиеся параметры sort=...&sort=... — именно так ждёт Spring
  for (const s of Array.isArray(sort) ? sort : [sort]) {
    searchParams.append('sort', s)
  }

  if (filter.cableTypeName) searchParams.set('cableTypeName', filter.cableTypeName)
  if (filter.conductorCount !== undefined) {
    searchParams.set('conductorCount', String(filter.conductorCount))
  }
  if (filter.minCrossSection !== undefined) {
    searchParams.set('minCrossSection', String(filter.minCrossSection))
  }
  if (filter.maxCrossSection !== undefined) {
    searchParams.set('maxCrossSection', String(filter.maxCrossSection))
  }

  // Передаём готовую query-строку — axios ничего не сериализует сам
  const response = await axios.get<PageResponse<Cable>>(`${API_BASE}?${searchParams.toString()}`)
  return response.data
}

export async function getCableById(id: number): Promise<Cable> {
  const response = await axios.get<Cable>(`${API_BASE}/${id}`)
  return response.data
}

export async function createCable(request: CableRequest): Promise<Cable> {
  const response = await axios.post<Cable>(API_BASE, request)
  return response.data
}

export async function updateCable(id: number, request: CableRequest): Promise<Cable> {
  const response = await axios.put<Cable>(`${API_BASE}/${id}`, request)
  return response.data
}

export async function deleteCable(id: number): Promise<void> {
  await axios.delete(`${API_BASE}/${id}`)
}

export async function deleteCables(ids: number[]): Promise<void> {
  await axios.delete(`${API_BASE}/bulk`, { data: ids })
}

export async function getCableTypes(): Promise<CableType[]> {
  const response = await axios.get<CableType[]>(`${API_BASE}/types`)
  return response.data
}

export async function exportAllCables(): Promise<ExportFile<CableExport>> {
  const response = await axios.get<ExportFile<CableExport>>(`${API_BASE}/export`)
  return response.data
}

export async function exportSelectedCables(ids: number[]): Promise<ExportFile<CableExport>> {
  const response = await axios.post<ExportFile<CableExport>>(`${API_BASE}/export`, ids)
  return response.data
}

export async function importCables(
  file: ExportFile<CableExport>,
  mode: ImportMode,
): Promise<ImportResult> {
  const response = await axios.post<ImportResult>(`${API_BASE}/import`, file, { params: { mode } })
  return response.data
}
