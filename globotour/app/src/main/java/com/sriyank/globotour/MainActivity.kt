package com.sriyank.globotour

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar        : MaterialToolbar
    private lateinit var navController  : NavController
    //private lateinit var drawer  : DrawerLayout
    //private lateinit var navView  : NavigationView
    private lateinit var bottomNavView  : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        toolbar         = findViewById(R.id.activity_main_toolbar)
        //drawer         = findViewById(R.id.drawer_layout)
        //navView        = findViewById(R.id.navigation_view)
        bottomNavView   = findViewById(R.id.bottom_nav_view)

        // Get NavHostFragment and NavController
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_frag) as NavHostFragment
        navController   = navHostFrag.navController

        //val appBarConfiguration = AppBarConfiguration(navController.graph, drawer)
        val topLevelDest = setOf(R.id.fragmentCityList, R.id.fragmentFavoriteList)
        val appBarConfiguration = AppBarConfiguration(topLevelDest)

        toolbar.setupWithNavController(navController, appBarConfiguration)

        //navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
    }

    /*override fun onBackPressed() {
        if(drawer.isOpen) {
            drawer.close()
        } else {
            super.onBackPressed()
        }
    }*/
}