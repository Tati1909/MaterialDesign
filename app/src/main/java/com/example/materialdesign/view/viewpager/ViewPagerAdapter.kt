package com.example.materialdesign.view.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

private const val EARTH_FRAGMENT = 0
private const val MARS_FRAGMENT = 1
private const val WEATHER_FRAGMENT = 2

class ViewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = arrayOf(
        EarthFragment(),
        MarsFragment(),
        WeatherFragment()
    )

    //возвращает тот фрагмент, который нужно отобразить
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> fragments[EARTH_FRAGMENT]
            1 -> fragments[MARS_FRAGMENT]
            2 -> fragments[WEATHER_FRAGMENT]
            else -> throw IllegalStateException("Перешли на $position, но фрагментов всего ${fragments.size}")
        }
    }

    //возвращает количество фрагментов(3)
    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }

   /* override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Earth"
            1 -> "Mars"
            2 -> "Weather"
            else -> "Earth"
        }
    } */
}