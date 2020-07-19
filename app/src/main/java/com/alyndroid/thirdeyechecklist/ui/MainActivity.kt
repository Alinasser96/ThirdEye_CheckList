package com.alyndroid.thirdeyechecklist.ui

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.base.BaseActivity
import com.alyndroid.thirdeyechecklist.ui.inbox.NavHostInboxFragment
import com.alyndroid.thirdeyechecklist.ui.checklists.NavHostAddCheckListFragment
import com.alyndroid.thirdeyechecklist.ui.notification.NavHostNotificationFragment
import com.alyndroid.thirdeyechecklist.ui.reports.NavHostReportsFragment
import com.alyndroid.thirdeyechecklist.ui.settings.NavHostSettingsFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_layout.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDrawerLayout()
        controlBottomNav()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun controlBottomNav() {
        bottomNavigation.add(
            MeowBottomNavigation.Model(
                1,
                R.drawable.ic_notification
            )
        )
        bottomNavigation.add(
            MeowBottomNavigation.Model(
                2,
                R.drawable.ic_add
            )
        )
        bottomNavigation.add(
            MeowBottomNavigation.Model(
                3,
                R.drawable.ic_inbox
            )
        )
        bottomNavigation.add(
            MeowBottomNavigation.Model(
                4,
                R.drawable.ic_report
            )
        )
        bottomNavigation.add(
            MeowBottomNavigation.Model(
                5,
                R.drawable.ic_gear
            )
        )

        bottomNavigation.show(3)
        loadFragment(NavHostInboxFragment())

        bottomNavigation.setOnClickMenuListener {
            val fragment = when (it.id.toString()) {
                "1" -> {
                    NavHostNotificationFragment()
                }
                "2" -> {
                    NavHostAddCheckListFragment()
                }
                "3" -> {
                    NavHostInboxFragment()
                }
                "4" -> {
                    NavHostReportsFragment()
                }
                else -> {
                    NavHostSettingsFragment()
                }
            }
            loadFragment(fragment)
        }
    }

    private fun initDrawerLayout() {
        toolbar = main_toolbar
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        drawerLayout = drawer_layout
        navigationView = nav_view

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle!!)
        toggle!!.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.change_language -> {
                change()
                forceRTLIfSupported()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }




}
