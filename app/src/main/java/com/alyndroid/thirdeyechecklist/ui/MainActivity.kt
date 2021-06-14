package com.alyndroid.thirdeyechecklist.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.base.BaseActivity
import com.alyndroid.thirdeyechecklist.ui.checklists.NavHostAddCheckListFragment
import com.alyndroid.thirdeyechecklist.ui.inbox.NavHostInboxFragment
import com.alyndroid.thirdeyechecklist.ui.login.LoginActivity
import com.alyndroid.thirdeyechecklist.ui.notification.NavHostNotificationFragment
import com.alyndroid.thirdeyechecklist.ui.reports.NavHostReportsFragment
import com.alyndroid.thirdeyechecklist.ui.settings.NavHostSettingsFragment
import com.alyndroid.thirdeyechecklist.util.SharedPreference
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

        navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_nav_full_name).text =
            SharedPreference(this).getValueString("name")

        navigationView.menu.findItem(R.id.nav_version).title = "version : " +
                this.packageManager.getPackageInfo(this.packageName, 0).versionName

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

            R.id.logout -> {
                SharedPreference(this).logout()

                //goto login
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                try {
                    ActivityCompat.finishAffinity(this)
                } catch (e: IllegalStateException) {
                    finish()
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}
