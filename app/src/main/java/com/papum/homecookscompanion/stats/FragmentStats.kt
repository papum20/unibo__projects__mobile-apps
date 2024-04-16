package com.papum.homecookscompanion.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.papum.homecookscompanion.R


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentStats.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentStats : Fragment(R.layout.page_fragment_stats) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_stats, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentStats()
    }
}