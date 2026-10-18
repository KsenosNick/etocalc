import { apiClient } from './client'
import type { Cable } from '../types'

export async function fetchCables(): Promise<Cable[]> {
  const response = await apiClient.get<Cable[]>('/catalog/cables')
  return response.data
}
