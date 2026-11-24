
import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { searchListings } from './api'
import type { ListingSearchResponse } from './types'

type SearchFilters = {
  minPrice: string
  maxPrice: string
  minBedrooms: string
  city: string
  keyword: string
  targetBudget: string
}

const initialFilters: SearchFilters = {
  minPrice: '',
  maxPrice: '',
  minBedrooms: '',
  city: '',
  keyword: '',
  targetBudget: '',
}

const emptyResult: ListingSearchResponse = {
  results: [],
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
}

const money = (value: number) =>
  new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0,
  }).format(value)

const date = (value: string) =>
  new Date(`${value}T00:00:00`).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })

export default function App() {
  const [draft, setDraft] = useState<SearchFilters>(initialFilters)
  const [filters, setFilters] = useState<SearchFilters>(initialFilters)
  const [page, setPage] = useState(0)
  const [data, setData] = useState<ListingSearchResponse>(emptyResult)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refresh, setRefresh] = useState(0)

  const params = useMemo(() => {
    const query = new URLSearchParams({
      page: String(page),
      size: '10',
    })

    if (filters.minPrice) query.set('minPrice', filters.minPrice)
    if (filters.maxPrice) query.set('maxPrice', filters.maxPrice)
    if (filters.minBedrooms) query.set('minBedrooms', filters.minBedrooms)
    if (filters.city.trim()) query.set('city', filters.city.trim())
    if (filters.keyword.trim()) query.set('keyword', filters.keyword.trim())
    if (filters.targetBudget) query.set('targetBudget', filters.targetBudget)

    return query
  }, [filters, page])

  useEffect(() => {
    const controller = new AbortController()

    setLoading(true)
    setError('')

    searchListings(params, controller.signal)
      .then(setData)
      .catch((reason: unknown) => {
        if (!(reason instanceof DOMException && reason.name === 'AbortError')) {
          setError(
            reason instanceof Error
              ? reason.message
              : 'Unable to load listings. Please try again.',
          )
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false)
      })

    return () => controller.abort()
  }, [params, refresh])

  function updateDraft(field: keyof SearchFilters, value: string) {
    setDraft(current => ({ ...current, [field]: value }))
  }

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setPage(0)
    setFilters({ ...draft })
  }

  function reset() {
    setDraft(initialFilters)
    setFilters(initialFilters)
    setPage(0)
  }

  const firstResult = data.totalElements === 0 ? 0 : page * data.size + 1
  const lastResult = Math.min((page + 1) * data.size, data.totalElements)

  return (
    <div className="app-shell">
      <header className="site-header">
        <a className="brand" href="/" aria-label="ListingSearch home">
          <span className="brand-symbol">L</span>
          <span className="brand-name">Listing<span>Search</span></span>
        </a>

        <div className="header-context">
          <span className="header-dot" />
          PROPERTY SEARCH
        </div>
      </header>

      <main className="page-container">
        <section className="intro">
          <div className="eyebrow">A BETTER WAY TO FIND YOUR NEXT HOME</div>
          <h1>Find a home that<br /><span>fits your life.</span></h1>
          <p>
            Explore properties, narrow your search, and discover listings
            ranked around your budget.
          </p>
        </section>

        <section className="search-panel" aria-labelledby="search-title">
          <div className="panel-heading">
            <div>
              <div className="eyebrow">YOUR SEARCH</div>
              <h2 id="search-title">Property criteria</h2>
            </div>
            <button className="clear-button" type="button" onClick={reset}>
              Clear filters
            </button>
          </div>

          <form className="search-form" onSubmit={submit}>
            <label className="field">
              <span>City</span>
              <input
                value={draft.city}
                onChange={event => updateDraft('city', event.target.value)}
                placeholder="Any city"
              />
            </label>

            <label className="field">
              <span>Keyword</span>
              <input
                value={draft.keyword}
                onChange={event => updateDraft('keyword', event.target.value)}
                placeholder="Try renovated, garden..."
              />
            </label>

            <label className="field">
              <span>Minimum price</span>
              <input
                type="number"
                min="0"
                value={draft.minPrice}
                onChange={event => updateDraft('minPrice', event.target.value)}
                placeholder="$ No minimum"
              />
            </label>

            <label className="field">
              <span>Maximum price</span>
              <input
                type="number"
                min="0"
                value={draft.maxPrice}
                onChange={event => updateDraft('maxPrice', event.target.value)}
                placeholder="$ No maximum"
              />
            </label>

            <label className="field">
              <span>Bedrooms</span>
              <select
                value={draft.minBedrooms}
                onChange={event => updateDraft('minBedrooms', event.target.value)}
              >
                <option value="">Any number</option>
                {[1, 2, 3, 4, 5].map(number => (
                  <option key={number} value={number}>{number}+</option>
                ))}
              </select>
            </label>

            <label className="field">
              <span>Target budget</span>
              <input
                type="number"
                min="0"
                value={draft.targetBudget}
                onChange={event => updateDraft('targetBudget', event.target.value)}
                placeholder="$ Your ideal price"
              />
            </label>

            <div className="form-actions">
              <button className="search-button" type="submit">
                Search properties
                <span aria-hidden="true">→</span>
              </button>
              <span className="form-hint">Your filters are applied when you search.</span>
            </div>
          </form>
        </section>

        <section className="results-section" aria-labelledby="results-title" aria-busy={loading}>
          <div className="results-heading">
            <div>
              <div className="eyebrow">PROPERTY COLLECTION</div>
              <h2 id="results-title">Available listings</h2>
              <p className="results-summary">
                {loading
                  ? 'Updating your search...'
                  : `${data.totalElements} ${data.totalElements === 1 ? 'property' : 'properties'} found`}
              </p>
            </div>

            <div className="result-count">
              <span>RESULTS</span>
              <strong>{data.totalElements}</strong>
            </div>
          </div>

          {error && (
            <div className="state-panel error-panel" role="alert">
              <strong>We couldn't complete your search.</strong>
              <p>{error}</p>
              <button type="button" onClick={() => setRefresh(value => value + 1)}>
                Try again
              </button>
            </div>
          )}

          {loading && !error && (
            <div className="loading-panel" role="status">
              <span className="loading-indicator" />
              Searching properties
            </div>
          )}

          {!loading && !error && data.results.length === 0 && (
            <div className="state-panel empty-panel">
              <div className="empty-symbol">⌕</div>
              <strong>No properties match your search</strong>
              <p>Try changing your criteria or clearing the filters to see more listings.</p>
              <button type="button" onClick={reset}>Clear all filters</button>
            </div>
          )}

          {!error && data.results.length > 0 && (
            <>
              <div className="listing-grid">
                {data.results.map(({ listing, relevanceScore }) => (
                  <article
                    className="listing-card"
                    key={`${listing.source}-${listing.id}`}
                  >
                    <div className="property-visual">
                      <div className="visual-building building-one" />
                      <div className="visual-building building-two" />
                      <div className="visual-ground" />
                      <div className="visual-caption">
                        <span>{listing.city}, {listing.state}</span>
                        <strong>{listing.zip}</strong>
                      </div>
                      <span className="status-label">{listing.status}</span>
                    </div>

                    <div className="listing-content">
                      <div className="listing-price-row">
                        <strong className="listing-price">{money(listing.price)}</strong>
                        <span className="match-score">
                          {(relevanceScore * 100).toFixed(1)}% match
                        </span>
                      </div>

                      <h3>{listing.address}</h3>
                      <p className="listing-location">
                        {listing.city}, {listing.state} {listing.zip}
                      </p>

                      <div className="property-facts">
                        <span><strong>{listing.bedrooms}</strong> beds</span>
                        <span><strong>{listing.bathrooms}</strong> baths</span>
                        <span><strong>{listing.sqft.toLocaleString()}</strong> sqft</span>
                      </div>

                      <p className="listing-description">{listing.description}</p>

                      <div className="listing-meta">
                        <span>Listed {date(listing.listedDate)}</span>
                        <span>Ref. {listing.source}-{listing.id}</span>
                      </div>
                    </div>
                  </article>
                ))}
              </div>

              {data.totalPages > 1 && (
                <nav className="pagination" aria-label="Listing pages">
                  <button
                    type="button"
                    disabled={page === 0 || loading}
                    onClick={() => setPage(current => current - 1)}
                  >
                    ← Previous
                  </button>
                  <span>
                    <strong>{data.page + 1}</strong> of {data.totalPages}
                    <small>{firstResult}–{lastResult} of {data.totalElements}</small>
                  </span>
                  <button
                    type="button"
                    disabled={page + 1 >= data.totalPages || loading}
                    onClick={() => setPage(current => current + 1)}
                  >
                    Next →
                  </button>
                </nav>
              )}
            </>
          )}
        </section>
      </main>

      <footer className="site-footer">
        <span>LISTINGSEARCH</span>
        <span>JAVA · SPRING BOOT · REACT · TYPESCRIPT</span>
      </footer>
    </div>
  )
}
