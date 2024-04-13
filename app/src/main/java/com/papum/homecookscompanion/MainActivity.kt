package com.papum.homecookscompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papum.homecookscompanion.inventory.FragmentInventory
import com.papum.homecookscompanion.list.FragmentList
import com.papum.homecookscompanion.meals.FragmentMeals
import com.papum.homecookscompanion.settings.FragmentSettings
import com.papum.homecookscompanion.stats.FragmentStats

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment_meals      = FragmentMeals()
        val fragment_list       = FragmentList()
        val fragment_inventory  = FragmentInventory()
        val fragment_stats      = FragmentStats()
        val fragment_settings   = FragmentSettings()

        setCurrentFragment(fragment_list)
        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.bottom_mav_item_meals -> setCurrentFragment(fragment_meals)
                    R.id.bottom_mav_item_list -> setCurrentFragment(fragment_list)
                    R.id.bottom_mav_item_inventory -> setCurrentFragment(fragment_inventory)
                    R.id.bottom_mav_item_inventory -> setCurrentFragment(fragment_stats)
                    R.id.bottom_mav_item_inventory -> setCurrentFragment(fragment_settings)
                }
                true
            }
    }

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        setReorderingAllowed(true)  // suggested by docs
        //addToBackStack(null)  // doesn't update bottom_nav_bar too

        replace(R.id.fl_fragment_container, fragment)
        commit()
    }
}