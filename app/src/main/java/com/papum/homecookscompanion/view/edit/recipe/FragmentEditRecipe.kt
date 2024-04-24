package com.papum.homecookscompanion.view.edit.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentEditRecipe.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentEditRecipe : Fragment(R.layout.fragment_edit_recipe) {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

		val viewModel: EditRecipeViewModel by viewModels {
			EditRecipeViewModelFactory(
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
            FragmentEditRecipe()
    }
}