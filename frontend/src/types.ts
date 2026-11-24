export interface Listing {
  id: string
  source: string
  address: string
  city: string
  state: string
  zip: string
  price: number
  bedrooms: number
  bathrooms: number
  sqft: number
  listedDate: string
  status: string
  description: string
}
export interface ListingSearchResult { listing: Listing; relevanceScore: number }
export interface ListingSearchResponse {
  results: ListingSearchResult[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}
