package com.papum.homecookscompanion.view.plan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlan.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPlan : Fragment(R.layout.page_fragment_plan) {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		val viewModel: PlanViewModel by viewModels {
			PlanViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		val adapter = PlanAdapter(listOf())

        val recycler = view.findViewById<RecyclerView>(R.id.plan_recycler_view)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

		viewModel.getAllProducts().observe(viewLifecycleOwner) { newdata ->
			Log.d("MEALS_ACTIVITY_UPDATE", "products: ${newdata.map { plan -> "${plan.product.name} ${plan.planItem.date} ${plan.planItem.quantity};" } }")
			adapter.let { a ->
				a.items = newdata
				a.notifyDataSetChanged()
				//Log.d("MEALS_ACTIVITY_UPDATE", "${it.items}; ${it.itemCount}")
				Log.d("MEALS_ACTIVITY_UPDATE", "products: ${a.itemCount}")
			}
		}

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentPlan()
    }
}