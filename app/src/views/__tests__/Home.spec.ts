import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Home from '../Home.vue'
import type { Deal, Store } from '../../types'

const mockStores: Store[] = [
  {
    storeID: '1',
    storeName: 'Steam Store',
    isActive: 1,
    images: { banner: '', logo: '', icon: '/img/steam.png' }
  },
  {
    storeID: '2',
    storeName: 'GamersGate',
    isActive: 1,
    images: { banner: '', logo: '', icon: '/img/gamersgate.png' }
  }
]

const mockDeals: Deal[] = [
  {
    title: 'Free Game Giveaway',
    dealID: 'deal-free',
    storeID: '1',
    gameID: '101',
    salePrice: '0.00',
    normalPrice: '19.99',
    savings: '100',
    dealRating: '10.0',
    releaseDate: 1600000000,
    steamRatingText: 'Very Positive',
    steamRatingPercent: '90',
    steamRatingCount: '1000',
    metacriticScore: '85',
    steamAppID: '10101',
    thumb: 'https://example.com/free.jpg'
  },
  {
    title: 'Budget Indie Hero',
    dealID: 'deal-indie',
    storeID: '1',
    gameID: '102',
    salePrice: '4.50',
    normalPrice: '14.99',
    savings: '70',
    dealRating: '8.5',
    releaseDate: 1600000000,
    steamRatingText: 'Positive',
    steamRatingPercent: '80',
    steamRatingCount: '500',
    metacriticScore: '75',
    steamAppID: '10202',
    thumb: 'https://example.com/indie.jpg'
  },
  {
    title: 'Mid-Tier Adventure',
    dealID: 'deal-mid',
    storeID: '2',
    gameID: '103',
    salePrice: '8.99',
    normalPrice: '29.99',
    savings: '70',
    dealRating: '7.8',
    releaseDate: 1600000000,
    steamRatingText: null,
    steamRatingPercent: '0',
    steamRatingCount: '0',
    metacriticScore: '80',
    steamAppID: null, // Non-Steam store deal without Steam key
    thumb: 'https://example.com/mid.jpg'
  },
  {
    title: 'Premium RPG',
    dealID: 'deal-rpg',
    storeID: '1',
    gameID: '104',
    salePrice: '18.50',
    normalPrice: '59.99',
    savings: '69',
    dealRating: '9.0',
    releaseDate: 1600000000,
    steamRatingText: 'Overwhelmingly Positive',
    steamRatingPercent: '95',
    steamRatingCount: '50000',
    metacriticScore: '92',
    steamAppID: '10404',
    thumb: 'https://example.com/rpg.jpg'
  },
  {
    title: 'Full Price Deluxe',
    dealID: 'deal-deluxe',
    storeID: '2',
    gameID: '105',
    salePrice: '45.00',
    normalPrice: '69.99',
    savings: '35',
    dealRating: '6.0',
    releaseDate: 1600000000,
    steamRatingText: null,
    steamRatingPercent: '0',
    steamRatingCount: '0',
    metacriticScore: '70',
    steamAppID: null,
    thumb: 'https://example.com/deluxe.jpg'
  }
]

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

describe('Home.vue - DEAL-6 & DEAL-7', () => {
  beforeEach(() => {
    mockLocalStorage.clear()
    delete mockLocalStorage.storeData
    vi.stubGlobal('fetch', vi.fn((url: string) => {
      if (url.includes('/stores')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockStores)
        })
      }
      if (url.includes('/deals')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockDeals)
        })
      }
      return Promise.reject(new Error('Unknown URL'))
    }))
  })

  const mountHome = async () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          },
          TitleCard: {
            template: '<div class="stub-title-card">{{ title }}</div>',
            props: ['title']
          },
          FavoriteModal: {
            template: '<div class="stub-fav-modal"></div>',
            methods: { open: vi.fn() }
          }
        }
      }
    })
    await flushPromises()
    return wrapper
  }

  it('renders budget price tier chips and all deals by default (DEAL-6)', async () => {
    const wrapper = await mountHome()

    const budgetPills = wrapper.findAll('.budget-section .filter-pill-btn')
    expect(budgetPills.length).toBe(5)
    expect(budgetPills[0].text()).toContain('All Deals')
    expect(budgetPills[1].text()).toContain('Free (100% Off)')
    expect(budgetPills[2].text()).toContain('Under $5')
    expect(budgetPills[3].text()).toContain('Under $10')
    expect(budgetPills[4].text()).toContain('Under $20')

    // Initial state: all 5 mock deals visible
    const dealCards = wrapper.findAll('.deal-card')
    expect(dealCards.length).toBe(5)
  })

  it('filters active deals by Free threshold (DEAL-6)', async () => {
    const wrapper = await mountHome()
    const freeButton = wrapper.findAll('.budget-section .filter-pill-btn')[1]
    
    await freeButton.trigger('click')
    await wrapper.vm.$nextTick()

    const dealCards = wrapper.findAll('.deal-card')
    expect(dealCards.length).toBe(1)
    expect(dealCards[0].text()).toContain('Free Game Giveaway')
  })

  it('filters active deals by Under $5 threshold (DEAL-6)', async () => {
    const wrapper = await mountHome()
    const under5Button = wrapper.findAll('.budget-section .filter-pill-btn')[2]
    
    await under5Button.trigger('click')
    await wrapper.vm.$nextTick()

    const dealCards = wrapper.findAll('.deal-card')
    // Free ($0.00) and Budget Indie Hero ($4.50)
    expect(dealCards.length).toBe(2)
  })

  it('filters active deals by Under $10 threshold (DEAL-6)', async () => {
    const wrapper = await mountHome()
    const under10Button = wrapper.findAll('.budget-section .filter-pill-btn')[3]
    
    await under10Button.trigger('click')
    await wrapper.vm.$nextTick()

    const dealCards = wrapper.findAll('.deal-card')
    // Free ($0.00), Indie ($4.50), Mid-Tier ($8.99)
    expect(dealCards.length).toBe(3)
  })

  it('filters active deals by Under $20 threshold (DEAL-6)', async () => {
    const wrapper = await mountHome()
    const under20Button = wrapper.findAll('.budget-section .filter-pill-btn')[4]
    
    await under20Button.trigger('click')
    await wrapper.vm.$nextTick()

    const dealCards = wrapper.findAll('.deal-card')
    // Free, Indie, Mid-Tier, RPG ($18.50) => 4 deals (excludes $45.00)
    expect(dealCards.length).toBe(4)
  })

  it('combines price threshold with store filter and search (DEAL-6)', async () => {
    const wrapper = await mountHome()
    
    // Select Under $10
    const under10Button = wrapper.findAll('.budget-section .filter-pill-btn')[3]
    await under10Button.trigger('click')

    // Filter by storeID = 2 (GamersGate)
    const storeButtons = wrapper.findAll('.pills-section')[0].findAll('.filter-pill-btn')
    const gamersGateBtn = storeButtons.find(b => b.text().includes('GamersGate'))
    expect(gamersGateBtn).toBeTruthy()
    await gamersGateBtn!.trigger('click')

    await wrapper.vm.$nextTick()
    // Under $10 on GamersGate is only Mid-Tier Adventure ($8.99)
    let dealCards = wrapper.findAll('.deal-card')
    expect(dealCards.length).toBe(1)
    expect(dealCards[0].text()).toContain('Mid-Tier Adventure')

    // Add search query that does not match
    const searchInput = wrapper.find('.quick-filter-input')
    await searchInput.setValue('nonexistent')
    await wrapper.vm.$nextTick()

    expect(wrapper.findAll('.deal-card').length).toBe(0)
    expect(wrapper.find('.empty-filter').exists()).toBe(true)
    expect(wrapper.text()).toContain('No deals match your filter criteria')
  })

  it('resets all filters when clicking Reset All Filters button (DEAL-6)', async () => {
    const wrapper = await mountHome()

    // Apply multiple filters
    await wrapper.findAll('.budget-section .filter-pill-btn')[1].trigger('click') // Free
    const searchInput = wrapper.find('.quick-filter-input')
    await searchInput.setValue('Nonexistent Game')
    await wrapper.vm.$nextTick()

    expect(wrapper.find('.empty-filter').exists()).toBe(true)

    const resetBtn = wrapper.find('.reset-btn')
    expect(resetBtn.exists()).toBe(true)
    await resetBtn.trigger('click')
    await wrapper.vm.$nextTick()

    expect(wrapper.findAll('.deal-card').length).toBe(5)
  })

  it('filters deals by Steam-redeemable keys (DEAL-7)', async () => {
    const wrapper = await mountHome()

    const steamPill = wrapper.find('.steam-pill-btn')
    expect(steamPill.exists()).toBe(true)
    await steamPill.trigger('click')
    await wrapper.vm.$nextTick()

    // 3 deals have steamAppID (Free, Indie, RPG)
    const dealCards = wrapper.findAll('.deal-card')
    expect(dealCards.length).toBe(3)
    dealCards.forEach(card => {
      expect(card.find('.badge-deck').exists()).toBe(true)
    })
  })
})
