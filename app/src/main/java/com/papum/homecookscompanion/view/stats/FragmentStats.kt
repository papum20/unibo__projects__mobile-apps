package com.papum.homecookscompanion.view.stats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository


class FragmentStats :
	Fragment(R.layout.page_fragment_stats),
	AdapterView.OnItemSelectedListener
{

	private val viewModel: StatsViewModel by viewModels {
		StatsViewModelFactory(
			Repository(requireActivity().application)
		)
	}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_stats, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)


		/* view */

		view.findViewById<TextView>(R.id.nutrients_quantity).text	= AVERAGE_QUANTITY.toString()
		view.findViewById<TextView>(R.id.nutrients_measure).text	= AVERAGE_MEASURE

		with(view.findViewById<Spinner>(R.id.stats_duration_spinner)) {

			adapter = ArrayAdapter(
				requireContext(),
				android.R.layout.simple_spinner_item,
				OPTIONS_DURATION
			).apply {
				// Specify the layout to use when the list of choices appears.
				setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			}

			onItemSelectedListener = this@FragmentStats
		}


		/* observers */

		viewModel.getNutrientsAverage().observe(viewLifecycleOwner) { nutrients ->
			Log.d(TAG, "Nutrients: $nutrients")
			view.findViewById<TextView>(R.id.nutrients_kcal).text = nutrients.kcal?.let {
				getString(R.string.nutrients_format_value, it)
			} ?: getString(R.string.quantity_unavailable)
			view.findViewById<TextView>(R.id.nutrients_carbohydrates).text = nutrients.carbohydrates?.let {
				getString(R.string.nutrients_format_value, it)
			} ?: getString(R.string.quantity_unavailable)
			view.findViewById<TextView>(R.id.nutrients_fats).text = nutrients.fats?.let {
				getString(R.string.nutrients_format_value, it)
			} ?: getString(R.string.quantity_unavailable)
			view.findViewById<TextView>(R.id.nutrients_proteins).text = nutrients.proteins?.let {
				getString(R.string.nutrients_format_value, it)
			} ?: getString(R.string.quantity_unavailable)
		}


	}


		override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
			viewModel.setDurationDays(OPTIONS_DURATION_VALUES[pos])
		}

		override fun onNothingSelected(parent: AdapterView<*>) {

		}



	companion object {

		private const val TAG = "FragmentStats"

		private val OPTIONS_DURATION = arrayOf(
			"today",
			"last week",
			"last month (30 days)",
			"last year (365 days)"
		)
		private val OPTIONS_DURATION_VALUES = arrayOf(
			1L,
			7L,
			30L,
			365L
		)

		private const val AVERAGE_QUANTITY	= 1
		private const val AVERAGE_MEASURE	= "day(s)"

	}


}