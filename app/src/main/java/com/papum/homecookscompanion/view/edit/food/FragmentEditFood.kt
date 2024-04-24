package com.papum.homecookscompanion.view.edit.food

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentEditFood.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentEditFood : Fragment(R.layout.fragment_edit_food) {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

		Log.d("AAA", "BBB")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val navController = findNavController()

		val viewModel: EditFoodViewModel by viewModels {
			EditFoodViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		/* UI listeners */

		Log.d("AAA", view.findViewById<Button>(R.id.fragment_edit_food_btn_confirm).toString())

		view.findViewById<Button>(R.id.fragment_edit_food_btn_confirm).setOnClickListener {
			Log.d("AAA", "AAA")
			val name			: String	= view.findViewById<EditText>( R.id.fragment_edit_food_name				).text.toString()
			val parent			: String	= view.findViewById<EditText>( R.id.fragment_edit_food_parent			).text.toString()
			val kcal			: Float?	= view.findViewById<EditText>( R.id.fragment_edit_food_kcal				).text.toString().toFloatOrNull()
			val carbohydrates	: Float?	= view.findViewById<EditText>( R.id.fragment_edit_food_carbohydrates	).text.toString().toFloatOrNull()
			val fats			: Float?	= view.findViewById<EditText>( R.id.fragment_edit_food_fats				).text.toString().toFloatOrNull()
			val proteins		: Float?	= view.findViewById<EditText>( R.id.fragment_edit_food_proteins			).text.toString().toFloatOrNull()

			/* check valid fields */
			if(name == "") {
				Toast.makeText(activity, "ERROR: Missing name", Toast.LENGTH_LONG).show()
			}
			/* edit food */
			else {
				viewModel.createFoodWithNutrients(
					name, parent, kcal, carbohydrates, fats, proteins
				)
				Log.i("EDIT_PRODUCT", "added $name $parent")
				Toast.makeText(activity, "Added: $name !", Toast.LENGTH_SHORT).show()
				navController.navigateUp()
			}
		}

		view.findViewById<Button>(R.id.fragment_edit_food_btn_cancel).setOnClickListener {
			Log.i("EDIT_PRODUCT", "not added")
			navController.navigateUp()
		}

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentEditFood()
    }
}