package com.haruu.notesome.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.haruu.notesome.adapter.MainPagerAdapter
import com.haruu.notesome.R
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

private const val REQ_PERMISSION: Int = 9
internal const val NUM_OF_PAGES: Int = 3
internal const val SHORT_TEXT_PAGE: Int = 0
internal const val AUDIO_PAGE: Int = 1
internal const val SETTINGS_PAGE: Int = 2

class MainActivity : AppCompatActivity() {
    private lateinit var mPagerAdapter: MainPagerAdapter
    private lateinit var mViewPager: ViewPager
    private lateinit var mNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPagerAdapter = MainPagerAdapter(supportFragmentManager)
        mViewPager = pager
        mViewPager.adapter = mPagerAdapter
        mViewPager.addOnPageChangeListener(
            object : ViewPager.SimpleOnPageChangeListener() {
                private var currentMenu: MenuItem? = null

                override fun onPageSelected(position: Int) {
                    currentMenu?.isChecked = false
                    currentMenu = mNavigationView.menu.getItem(position)
                    currentMenu?.isChecked = true
                }
            }
        )

        mNavigationView = navigation
        mNavigationView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_short_text -> {
                        mViewPager.currentItem = SHORT_TEXT_PAGE
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_audio -> {
                        mViewPager.currentItem = AUDIO_PAGE
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_settings -> {
                        mViewPager.currentItem = SETTINGS_PAGE
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        )
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun checkPermissions() {
        val permission: String = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (!EasyPermissions.hasPermissions(this, permission))
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_rationale),
                    REQ_PERMISSION,
                permission
            )
    }

}
