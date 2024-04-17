package com.papum.homecookscompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papum.homecookscompanion.view.inventory.FragmentInventory
import com.papum.homecookscompanion.view.list.FragmentList
import com.papum.homecookscompanion.view.meals.FragmentMeals
import com.papum.homecookscompanion.view.products.FragmentProducts
import com.papum.homecookscompanion.view.settings.FragmentSettings
import com.papum.homecookscompanion.view.stats.FragmentStats

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment_meals      = FragmentMeals()
        val fragment_list       = FragmentList()
        val fragment_inventory  = FragmentInventory()
        val fragment_products	= FragmentProducts()
        val fragment_stats      = FragmentStats()
        val fragment_settings   = FragmentSettings()

		val bottom_navbar		= findViewById<BottomNavigationView>(R.id.bottom_nav)

		// set navbar onclick listeners for entries
		bottom_navbar.setOnItemSelectedListener {
			when (it.itemId) {
				R.id.bottom_nav_item_meals -> setCurrentFragment(fragment_meals)
				R.id.bottom_nav_item_list -> setCurrentFragment(fragment_list)
				R.id.bottom_nav_item_inventory -> setCurrentFragment(fragment_inventory)
				R.id.bottom_nav_item_products -> setCurrentFragment(fragment_products)
				R.id.bottom_nav_item_stats -> setCurrentFragment(fragment_stats)
			}
			true
		}

		// set default app entry
		bottom_navbar.selectedItemId = R.id.bottom_nav_item_products
		//setCurrentFragment(fragment_products)
	}

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        setReorderingAllowed(true)  // suggested by docs
        //addToBackStack(null)  // doesn't update bottom_nav_bar too

        replace(R.id.fl_fragment_container, fragment)
        commit()
    }
}