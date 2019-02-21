package com.haruu.notesome.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.haruu.notesome.R
import com.haruu.notesome.adapter.MainPagerAdapter
import com.haruu.notesome.dao.AppDatabase
import com.haruu.notesome.fragment.SettingsMainFragment
import com.haruu.notesome.fragment.ShortTextMainFragment
import com.haruu.notesome.fragment.SoundMainFragment
import com.haruu.notesome.presenter.ShortTextPresenter
import com.haruu.notesome.presenter.SoundPresenter
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

private const val REQ_PERMISSION: Int = 9

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var mViewPager: ViewPager
    private lateinit var mNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_rationale),
                REQ_PERMISSION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        else
            init()
    }

    private fun init() {
        setContentView(R.layout.activity_main)
        mViewPager = main_pager.apply {
            adapter = MainPagerAdapter(
                supportFragmentManager,
                arrayOf(
                    ShortTextMainFragment().apply {
                        presenter = ShortTextPresenter(
                            AppDatabase.getInstance(this@MainActivity).shortTextDao(),
                            this
                        )
                    },
                    SoundMainFragment().apply {
                        presenter = SoundPresenter(
                            AppDatabase.getInstance(this@MainActivity).soundDao(),
                            this
                        )
                    },
                    SettingsMainFragment()
                )
            )
            addOnPageChangeListener(
                object : ViewPager.SimpleOnPageChangeListener() {
                    private var currentMenu: MenuItem? = null

                    override fun onPageSelected(position: Int) {
                        currentMenu?.isChecked = false
                        currentMenu = mNavigationView.menu.getItem(position)
                        currentMenu?.isChecked = true
                    }
                })
        }

        mNavigationView = main_navigation.apply {
            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_short_text -> mViewPager.currentItem = 0
                    R.id.navigation_audio -> mViewPager.currentItem = 1
                    R.id.navigation_settings -> mViewPager.currentItem = 2
                }
                true
            }
        }
    }

    /* Permissions */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQ_PERMISSION && perms.size > 0)
            init()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }

}
