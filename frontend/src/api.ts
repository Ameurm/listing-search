import type { ListingSearchResponse } from './types'

const baseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export async function searchListings(params: URLSearchParams, signal?: AbortSignal): Promise<ListingSearchResponse> {
  const response = await fetch(`${baseUrl}/api/listings?${params.toString()}`, { signal })
  if (!response.ok) {
    const problem = await response.json().catch(() => null)
    throw new Error(problem?.detail ?? problem?.errors ?? `Request failed (${response.status})`)
  }
  return response.json() as Promise<ListingSearchResponse>
}
