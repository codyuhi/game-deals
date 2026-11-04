import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SearchTile from '../SearchTile.vue'
import type { SearchResult } from '../../types'

describe('SearchTile.vue', () => {
  const mockGame: SearchResult = {
    gameID: '12345',
    steamAppID: '98765',
    cheapest: '14.99',
    cheapestDealID: 'deal-xyz',
    external: 'Dave the Diver',
    thumb: 'https://example.com/dave.jpg'
  }

  it('renders game external name and cheapest price', () => {
    const wrapper = mount(SearchTile, {
      props: {
        game: mockGame
      },
      global: {
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          }
        }
      }
    })

    expect(wrapper.text()).toContain('Dave the Diver')
    expect(wrapper.text()).toContain('14.99')
    expect(wrapper.find('img').attributes('src')).toBe('https://example.com/dave.jpg')
    const deckBadge = wrapper.find('.badge-deck')
    expect(deckBadge.exists()).toBe(true)
    expect(deckBadge.attributes('href')).toBe('https://www.protondb.com/app/98765')
  })

  it('omits ProtonDB badge when game has no steamAppID', () => {
    const wrapper = mount(SearchTile, {
      props: {
        game: {
          ...mockGame,
          steamAppID: null
        }
      },
      global: {
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          }
        }
      }
    })

    expect(wrapper.find('.badge-deck').exists()).toBe(false)
  })
})
