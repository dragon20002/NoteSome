package com.haruu.notesome.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.haruu.notesome.activity.AUDIO_PAGE
import com.haruu.notesome.activity.NUM_OF_PAGES
import com.haruu.notesome.activity.SETTINGS_PAGE
import com.haruu.notesome.activity.SHORT_TEXT_PAGE
import com.haruu.notesome.fragment.AudioMainFragment
import com.haruu.notesome.fragment.SettingsMainFragment
import com.haruu.notesome.fragment.ShortTextMainFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = NUM_OF_PAGES

    override fun getItem(i: Int): Fragment {
        return when (i) {
            SHORT_TEXT_PAGE -> ShortTextMainFragment()
            AUDIO_PAGE -> AudioMainFragment()
            SETTINGS_PAGE -> SettingsMainFragment()
            else -> ShortTextMainFragment()
        }
    }

}