package com.papum.homecookscompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papum.homecookscompanion.view.inventory.FragmentInventory
import com.papum.homecookscompanion.view.list.FragmentList
import com.papum.homecookscompanion.view.meals.FragmentMeals
import com.papum.homecookscompanion.view.products.FragmentProducts
import com.papum.homecookscompanion.view.settings.FragmentSettings
import com.papum.homecookscompanion.view.stats.FragmentStats

class MainActivity : AppCompatActivity() {

	private lateinit var navController: NavController
	private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val navHostFragment = supportFragmentManager.findFragmentById(
			R.id.fragment_nav_container
		) as NavHostFragment
		navController = navHostFragment.navController


		// setup toolbar
		setSupportActionBar(findViewById(R.id.toolbar))

		// setup navbar
		findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)

		// set default app entry
		//bottomNavbar.selectedItemId = R.id.fragmentInventory
		//setCurrentFragment(fragment_products)


		// setup actionbar with top level destinations
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.inventory,
				R.id.list,
				R.id.meals,
				R.id.products,
				R.id.stats
			)
		)
		setupActionBarWithNavController(navController, appBarConfiguration)
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(appBarConfiguration)
	}

}