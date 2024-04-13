package com.papum.homecookscompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment1 = Fragment1()
        val fragment2 = Fragment2()
        val fragment3 = Fragment3()

        setCurrentFragment(fragment1)
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.bottom_mav_item_meals -> setCurrentFragment(fragment1)
                    R.id.bottom_mav_item_list -> setCurrentFragment(fragment2)
                    R.id.bottom_mav_item_inventory -> setCurrentFragment(fragment3)

                }
                true
            }
    }

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.flFragment, fragment)
        commit()
    }
}