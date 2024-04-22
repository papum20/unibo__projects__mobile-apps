package com.papum.homecookscompanion.view.meals

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import java.time.LocalDateTime


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMeals.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMeals : Fragment(R.layout.page_fragment_meals) {

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

		val vm: MealsViewModel by viewModels {
			MealsViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		/* recycler view */
		val adapter = MealsAdapter(listOf())

        val recycler = view.findViewById<RecyclerView>(R.id.meals_recycler_view)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

		/* selected day and buttons to change it */
		vm.currentlySetDate.value = LocalDateTime.now()
		viewDate = view.findViewById(R.id.meals_date_text)
		updateDisplayedDate(vm.currentlySetDate.value)

		view.findViewById<Button>(R.id.meals_date_btn_left).setOnClickListener {
			vm.currentlySetDate.value = vm.currentlySetDate.value?.minusDays(1)
			updateDisplayedDate(vm.currentlySetDate.value)
		}
		view.findViewById<Button>(R.id.meals_date_btn_right).setOnClickListener {
			vm.currentlySetDate.value = vm.currentlySetDate.value?.plusDays(1)
			updateDisplayedDate(vm.currentlySetDate.value)
		}

		/* observe meals/products for the selected day */
		vm.getAllProducts_fromDateSet()
			.observe(viewLifecycleOwner) { newData ->
				Log.d("MEALS_ACTIVITY_UPDATE", "products: ${newData.map { meal -> "${meal.product.name} ${meal.mealsItem.date} ${meal.mealsItem.quantity};" } }")
				adapter.let { a ->
					a.items = newData
					a.notifyDataSetChanged()
					//Log.d("MEALS_ACTIVITY_UPDATE", "${it.items}; ${it.itemCount}")
					Log.d("MEALS_ACTIVITY_UPDATE", "products: ${a.itemCount}")
				}
			}

    }

	/**
	 * Update date displayed in textView with currentlySetDate.
	 */
	private fun updateDisplayedDate(date: LocalDateTime?) {
		date?.let { d ->
			viewDate.text = getString(
				R.string.meals_date_placeholder,
				d.dayOfMonth, d.monthValue, d.year
			)
		}
	}


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentMeals()
    }
}