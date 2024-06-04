package com.papum.homecookscompanion.view.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.papum.homecookscompanion.R


class FragmentStats :
	Fragment(R.layout.page_fragment_stats),
	AdapterView.OnItemSelectedListener
{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_stats, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val spinner: Spinner = view.findViewById(R.id.stats_duration_spinner)
// Create an ArrayAdapter using the string array and a default spinner layout.
		ArrayAdapter.createFromResource(
			this,
			R.array.planets_array,
			android.R.layout.simple_spinner_item
		).also { adapter ->
			// Specify the layout to use when the list of choices appears.
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			// Apply the adapter to the spinner.
			spinner.adapter = adapter
		}

		spinner.onItemSelectedListener = this
	}


		override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
			// An item is selected. You can retrieve the selected item using
			// parent.getItemAtPosition(pos).
		}

		override fun onNothingSelected(parent: AdapterView<*>) {
			// Another interface callback.
		}


	}


}