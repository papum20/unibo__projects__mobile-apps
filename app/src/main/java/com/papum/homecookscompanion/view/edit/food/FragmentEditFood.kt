package com.papum.homecookscompanion.view.edit.food

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.utils.Const


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentEditFood.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentEditFood : Fragment(R.layout.fragment_edit_food) {

	private val args: FragmentEditFoodArgs by navArgs()
	private val viewModel: EditFoodViewModel by viewModels {
		EditFoodViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	private lateinit var navController: NavController


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		navController = findNavController()

		/* permissions */
		requestPermissionsIfNecessary(PERMISSIONS_MAPS)

		/* args */
		val foodId: Long = args.foodId

		/* form fields */
		val checkEdible		= view.findViewById<CheckBox>( R.id.fragment_edit_food_edible_check)
		val etName			= view.findViewById<EditText>( R.id.fragment_edit_food_name)
		val etParent		= view.findViewById<EditText>( R.id.fragment_edit_food_parent)
		val etKcal			= view.findViewById<EditText>( R.id.nutrients_edit_kcal)
		val etCarbohydrates	= view.findViewById<EditText>( R.id.nutrients_edit_carb)
		val etFats			= view.findViewById<EditText>( R.id.nutrients_edit_fats)
		val etProteins		= view.findViewById<EditText>( R.id.nutrients_edit_prot)
		val layoutNutrients	= view.findViewById<ConstraintLayout>( R.id.fragment_edit_food_nutrients)
		val tvWeightDisplay	= view.findViewById<TextView>(R.id.nutrients_edit_quantity)

		// weight for displayed nutrients
		tvWeightDisplay?.text = viewModel.getDisplayWeight().toString()

		/* if it's editing a food (and not creating), setup */
		if(foodId != Const.ID_PRODUCT_NULL) {
			Log.d(TAG, "product to edit has id $foodId")
			viewModel.getProduct_fromId(foodId).observe(viewLifecycleOwner) { food ->
				Log.d(TAG, "product to edit fetched, it's ${food.product.name}")
				checkEdible.isChecked = food.product.isEdible
				etName.setText(				food.product.name)
				etParent.setText(			food.product.parent)
				etKcal.setText(				food.nutrients?.kcal?.toString()				?: "")
				etCarbohydrates.setText(	food.nutrients?.carbohydrates?.toString()	?: "")
				etFats.setText(				food.nutrients?.fats?.toString()				?: "")
				etProteins.setText(			food.nutrients?.proteins?.toString()			?: "")
			}
		} else {
			checkEdible.isChecked = DFLT_CHECKED
		}

		/* UI listeners */

		checkEdible.setOnCheckedChangeListener { buttonView, isChecked ->
			layoutNutrients.visibility = if(isChecked) View.VISIBLE else View.GONE
		}

		view.findViewById<ImageButton>(R.id.fragment_edit_food_btn_save).setOnClickListener {
			val name			: String	= etName.text.toString()
			val parent			: String	= etParent.text.toString()
			val kcal			: Float?	= etKcal.text.toString().toFloatOrNull()
			val carbohydrates	: Float?	= etCarbohydrates.text.toString().toFloatOrNull()
			val fats			: Float?	= etFats.text.toString().toFloatOrNull()
			val proteins		: Float?	= etProteins.text.toString().toFloatOrNull()

			/* check valid fields */
			if(name == "") {
				showErrorMissingName()
			}
			/* edit food */
			else {
				if(checkEdible.isChecked)
					viewModel.createFood(
						name, parent, kcal, carbohydrates, fats, proteins
					)
				else
					viewModel.createProductNonEdible(name, parent)
				showSuccessCreated(name, parent, checkEdible.isChecked)
				navController.navigateUp()
			}
		}

		view.findViewById<ImageButton>(R.id.fragment_edit_food_btn_cancel).setOnClickListener {
			Log.i(TAG, "not added")
			navController.navigateUp()
		}

		// map
		if(foodId != Const.ID_PRODUCT_NULL) {
			view.findViewById<ImageButton>(R.id.fragment_edit_food_btn_map).setOnClickListener {
				navController.navigate(
					FragmentEditFoodDirections.actionFragmentEditFoodToFragmentMap(foodId)
				)
			}
		} else {
			// if creating a new food, don't show map for shops
			view.findViewById<ImageButton>(R.id.fragment_edit_food_btn_map).let { btnMap ->
				btnMap.visibility	= View.GONE
				btnMap.isClickable	= false
			}
		}

    }


	override fun onResume() {
		super.onResume()
	}

	override fun onPause() {
		super.onPause()
	}


	/* display */

	private fun showErrorMissingName() {
		Log.e(TAG, "Missing name")
		Toast.makeText(activity, "ERROR: Missing name", Toast.LENGTH_LONG).show()
	}
	private fun showSuccessCreated(name: String, parent: String, checkedEdible: Boolean) {
		Log.i(TAG, "added $name $parent, edible: $checkedEdible")
		Toast.makeText(activity, "Added: $name, $parent !", Toast.LENGTH_SHORT).show()
	}


	/* Permissions */

	private val requestMultiplePermissionsLauncher = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		permissions.entries.forEach {
			Log.d(TAG, "Permission requested result: ${it.key}: ${it.value}")
		}
	}

	private fun requestPermissionsIfNecessary(permissions: Array<String>) {
		val permissionsToRequest = ArrayList<String>()
		permissions.forEach { permission ->
		if ( ContextCompat.checkSelfPermission(requireContext(), permission)
			!= PackageManager.PERMISSION_GRANTED) {
				permissionsToRequest.add(permission)
			}
		}
		if (permissionsToRequest.size > 0) {
			requestMultiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
		}
	}


	companion object {

		private const val TAG = "Food"
		private const val FRAGMENT_TAG_MAP = "fragment_map_edit_food"

		private const val DFLT_CHECKED = true

		private val PERMISSIONS_MAPS = arrayOf(
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
		)


		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 */
		@JvmStatic
		fun newInstance() =
			FragmentEditFood()

	}
}