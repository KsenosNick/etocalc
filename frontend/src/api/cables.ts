import { apiClient } from './client'
import type { Cable } from '../types'

export async function getCables(): Promise<Cable[]> {
  const response = await axios.get<PageResponse<Cable>>('/api/catalog/cables', {
    params: { size: 1000 },
  })
  return response.data.content // ← разворачиваем PageResponse
}
