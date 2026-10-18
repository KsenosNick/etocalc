import { apiClient } from './client'
import type { VoltageDropRequest, VoltageDropResponse } from '../types'

export async function calculateVoltageDrop(
  request: VoltageDropRequest,
): Promise<VoltageDropResponse> {
  const response = await apiClient.post<VoltageDropResponse>(
    '/calculation/voltage-drop',
    request,
  )
  return response.data
}
