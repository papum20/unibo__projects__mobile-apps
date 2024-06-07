package com.papum.homecookscompanion.view.meals

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProductAndMealsWithNutrients
import java.time.LocalDateTime


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMeals.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMeals :
	Fragment(R.layout.page_fragment_meals),
	MealsAdapter.IListenerMealsItem
{

	val viewModel: MealsViewModel by viewModels {
		MealsViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	private lateinit var viewDate: TextView			// displays viewModel.currentlySetDate



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_meals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		/* recycler view */
		val adapter = MealsAdapter(listOf(), this, context)

        val recycler = view.findViewById<RecyclerView>(R.id.meals_recycler_view)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

		/* selected day and buttons to change it */
		viewModel.currentlySetDate.value = LocalDateTime.now()
		viewDate = view.findViewById(R.id.meals_date_text)
		updateDisplayedDate(viewModel.currentlySetDate.value)

		view.findViewById<ImageButton>(R.id.meals_date_btn_left).setOnClickListener {
			viewModel.currentlySetDate.value = viewModel.currentlySetDate.value?.minusDays(1)
			updateDisplayedDate(viewModel.currentlySetDate.value)
		}
		view.findViewById<ImageButton>(R.id.meals_date_btn_right).setOnClickListener {
			viewModel.currentlySetDate.value = viewModel.currentlySetDate.value?.plusDays(1)
			updateDisplayedDate(viewModel.currentlySetDate.value)
		}

		/* observe meals/products for the selected day */
		viewModel.getAllMealsWithNutrients()
			.observe(viewLifecycleOwner) { mealsWithNutrients ->
				Log.d(TAG, "new meals: ${mealsWithNutrients.size}")
				adapter.updateAll(mealsWithNutrients)
			}

    }

	/**
	 * Update date displayed in textView with currentlySetDate.
	 */
	private fun updateDisplayedDate(date: LocalDateTime?) {
		date?.let { d ->
			viewDate.text = getString(
				R.string.product_format_date,
				d.dayOfMonth, d.monthValue, d.year
			)
		}
	}


	/* MealsAdapter.IListenerMealsItem */

	override fun onSetQuantity(item: EntityProductAndMealsWithNutrients, quantity: Float) {
		viewModel.updateQuantity(item.meal, quantity)
	}


    companion object {

		private const val TAG = "Meals"

    }


}