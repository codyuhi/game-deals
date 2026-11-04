import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import GameDetails from '../GameDetails.vue'
import type { GameDetailsData, Store } from '../../types'

vi.mock('vue-router', () => ({
  useRoute: () => ({
    params: { gameid: '101' }
  })
}))

const mockStores: Store[] = [
  {
    storeID: '1',
    storeName: 'Steam Store',
    isActive: 1,
    images: { banner: '', logo: '', icon: '/img/steam.png' }
  }
]

const mockGameWithSteam: GameDetailsData = {
  info: {
    title: 'Hades',
    steamAppID: '1145360',
    thumb: 'https://example.com/hades.jpg'
  },
  cheapestPriceEver: {
    price: '9.99',
    date: 1600000000
  },
  deals: [
    {
      storeID: '1',
      dealID: 'hades-deal',
      price: '12.49',
      retailPrice: '24.99',
      savings: '50'
    }
  ]
}

const mockGameWithoutSteam: GameDetailsData = {
  info: {
    title: 'GOG DRM-Free Gem',
    steamAppID: null,
    thumb: 'https://example.com/gem.jpg'
  },
  cheapestPriceEver: {
    price: '4.99',
    date: 1600000000
  },
  deals: []
}

const storage: Record<string, string> = {}
const mockLocalStorage: any = {
  getItem: (key: string) => storage[key] ?? null,
  setItem: (key: string, val: string) => { storage[key] = val },
  removeItem: (key: string) => { delete storage[key] },
  clear: () => { Object.keys(storage).forEach(k => delete storage[k]) }
}
Object.defineProperty(globalThis, 'localStorage', {
  value: mockLocalStorage,
  writable: true
})

describe('GameDetails.vue - DEAL-7', () => {
  beforeEach(() => {
    mockLocalStorage.clear()
    delete mockLocalStorage.storeData
  })

  it('renders Steam Deck & ProtonDB compatibility section and links to protondb.com/app/:steamAppId', async () => {
    vi.stubGlobal('fetch', vi.fn((url: string) => {
      if (url.includes('/stores')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockStores)
        })
      }
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockGameWithSteam)
      })
    }))

    const wrapper = mount(GameDetails, {
      global: {
        stubs: {
          TitleCard: {
            template: '<div class="stub-title-card">{{ title }}</div>',
            props: ['title']
          },
          FavoriteModal: {
            template: '<div class="stub-fav-modal"></div>'
          }
        }
      }
    })

    await flushPromises()

    const deckSection = wrapper.find('.deck-compat-card')
    expect(deckSection.exists()).toBe(true)
    expect(deckSection.text()).toContain('Steam Deck & ProtonDB Compatibility')
    expect(deckSection.text()).toContain('Hades')

    const link = deckSection.find('.deck-link-btn')
    expect(link.exists()).toBe(true)
    expect(link.attributes('href')).toBe('https://www.protondb.com/app/1145360')
  })

  it('gracefully omits Steam Deck section for non-Steam games without errors', async () => {
    vi.stubGlobal('fetch', vi.fn((url: string) => {
      if (url.includes('/stores')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockStores)
        })
      }
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockGameWithoutSteam)
      })
    }))

    const wrapper = mount(GameDetails, {
      global: {
        stubs: {
          TitleCard: {
            template: '<div class="stub-title-card">{{ title }}</div>',
            props: ['title']
          },
          FavoriteModal: {
            template: '<div class="stub-fav-modal"></div>'
          }
        }
      }
    })

    await flushPromises()

    expect(wrapper.find('.deck-compat-card').exists()).toBe(false)
  })
})
