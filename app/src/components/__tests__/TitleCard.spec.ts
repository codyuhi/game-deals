import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import TitleCard from '../TitleCard.vue'

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn()
  })
}))

describe('TitleCard.vue', () => {
  it('renders title and descriptions correctly', () => {
    const wrapper = mount(TitleCard, {
      props: {
        title: 'Super PC Deals',
        descriptions: ['Description line one', 'Description line two'],
        showSearchButton: false
      }
    })

    expect(wrapper.text()).toContain('Super PC Deals')
    expect(wrapper.text()).toContain('Description line one')
    expect(wrapper.text()).toContain('Description line two')
    expect(wrapper.find('form').exists()).toBe(false)
  })

  it('renders search input form when showSearchButton is true', () => {
    const wrapper = mount(TitleCard, {
      props: {
        title: 'Game Search',
        descriptions: [],
        showSearchButton: true
      }
    })

    expect(wrapper.find('form').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="Search PC games..."]').exists()).toBe(true)
  })
})
