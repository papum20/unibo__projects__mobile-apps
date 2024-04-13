package com.papum.homecookscompanion.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.papum.homecookscompanion.R


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentInventory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentInventory : Fragment(R.layout.page_fragment_inventory) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_inventory, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentInventory()
    }
}