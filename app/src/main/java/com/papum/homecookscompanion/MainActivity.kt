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

        val fragmentMeals      	= FragmentMeals()
        val fragmentList       	= FragmentList()
        val fragmentInventory 	= FragmentInventory()
        val fragmentProducts	= FragmentProducts()
        val fragmentStats      	= FragmentStats()
        val fragmentSettings   	= FragmentSettings()

		val bottomNavbar		= findViewById<BottomNavigationView>(R.id.bottom_nav)

		// set navbar onclick listeners for entries
		bottomNavbar.setOnItemSelectedListener {
			when (it.itemId) {
				R.id.bottom_nav_item_meals -> setCurrentFragment(fragmentMeals)
				R.id.bottom_nav_item_list -> setCurrentFragment(fragmentList)
				R.id.bottom_nav_item_inventory -> setCurrentFragment(fragmentInventory)
				R.id.bottom_nav_item_products -> setCurrentFragment(fragmentProducts)
				R.id.bottom_nav_item_stats -> setCurrentFragment(fragmentStats)
			}
			true
		}

		// set default app entry
		bottomNavbar.selectedItemId = R.id.bottom_nav_item_products
		//setCurrentFragment(fragment_products)
	}

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        setReorderingAllowed(true)  // suggested by docs
        //addToBackStack(null)  // doesn't update bottom_nav_bar too

        replace(R.id.fl_fragment_container, fragment)
        commit()
    }
}