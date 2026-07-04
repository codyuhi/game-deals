import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import DealTile from '../DealTile.vue'
import type { Deal, Store } from '../../types'

describe('DealTile.vue', () => {
  const mockDeal: Deal = {
    title: 'Portal 2',
    dealID: 'deal-portal',
    storeID: '1',
    gameID: '10',
    salePrice: '1.99',
    normalPrice: '9.99',
    savings: '80.08',
    dealRating: '9.5',
    releaseDate: 1303171200, // April 2011
    steamRatingText: 'Overwhelmingly Positive',
    steamRatingPercent: '98',
    steamRatingCount: '150000',
    metacriticScore: '95',
    thumb: 'https://example.com/portal2.jpg'
  }

  const mockStores: Store[] = [
    {
      storeID: '1',
      storeName: 'Steam Store',
      isActive: 1,
      images: { banner: '', logo: '', icon: '/img/steam.png' }
    }
  ]

  it('renders deal metadata, prices, and savings badge', () => {
    const wrapper = mount(DealTile, {
      props: {
        deal: mockDeal,
        storeData: mockStores
      },
      global: {
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          }
        }
      }
    })

    expect(wrapper.text()).toContain('Portal 2')
    expect(wrapper.text()).toContain('1.99')
    expect(wrapper.text()).toContain('9.99')
    expect(wrapper.text()).toContain('80%') // parseFloat(savings).toFixed(0)%
    expect(wrapper.text()).toContain('Steam Store')
  })

  it('emits favorite action when clicking favorite button', async () => {
    const wrapper = mount(DealTile, {
      props: {
        deal: mockDeal,
        storeData: mockStores
      },
      global: {
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          }
        }
      }
    })

    const favButton = wrapper.find('.fav-btn')
    expect(favButton.exists()).toBe(true)
    
    await favButton.trigger('click')
    
    expect(wrapper.emitted('favorite')).toBeTruthy()
    expect(wrapper.emitted('favorite')?.[0][0]).toEqual({
      name: 'Portal 2',
      id: '10'
    })
  })
})
