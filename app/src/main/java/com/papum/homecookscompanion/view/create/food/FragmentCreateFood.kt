package com.papum.homecookscompanion.view.create.food

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
 * Use the [FragmentCreateFood.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentCreateFood : Fragment(R.layout.fragment_create_food) {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		val viewModel: CreateFoodViewModel by viewModels {
			CreateFoodViewModelFactory(
				Repository(requireActivity().application)
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
            FragmentCreateFood()
    }
}