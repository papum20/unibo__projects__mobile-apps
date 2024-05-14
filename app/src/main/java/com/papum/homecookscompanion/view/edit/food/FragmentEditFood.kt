package com.papum.homecookscompanion.view.edit.food

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.view.maps.FragmentMap
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentEditFood.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentEditFood : Fragment(R.layout.fragment_edit_food) {

	private val args: FragmentEditFoodArgs by navArgs()

	private lateinit var navController: NavController

	private val requestMultiplePermissionsLauncher = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
			permissions.entries.forEach {
				Log.d(TAG, "Permission requested result: ${it.key}: ${it.value}")
			}
		}


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

		val foodId: String? = args.foodId

		val navController = findNavController()

		val viewModel: EditFoodViewModel by viewModels {
			EditFoodViewModelFactory(
				Repository(requireActivity().application)
			)
		}


		/* form fields */
		val etName			= view.findViewById<EditText>( R.id.fragment_edit_food_name)
		val etParent		= view.findViewById<EditText>( R.id.fragment_edit_food_parent)
		val etKcal			= view.findViewById<EditText>( R.id.fragment_edit_food_kcal)
		val etCarbohydrates	= view.findViewById<EditText>( R.id.fragment_edit_food_carbohydrates)
		val etFats			= view.findViewById<EditText>( R.id.fragment_edit_food_fats)
		val etProteins		= view.findViewById<EditText>( R.id.fragment_edit_food_proteins)


		/* if it's editing a food (and not creating), setup */
		if(foodId != null) {
			Log.d("PRODUCT_EDIT", "product to edit has id $foodId")
			viewModel.getProduct_fromId(foodId.toLong()).observe(viewLifecycleOwner) { food ->
				Log.d("PRODUCT_EDIT", "product to edit fetched, it's ${food.product.name}")
				etName.setText(				food.product.name)
				etParent.setText(			food.product.parent)
				etKcal.setText(				food.nutrients.kcal?.toString()				?: "")
				etCarbohydrates.setText(	food.nutrients.carbohydrates?.toString()	?: "")
				etFats.setText(				food.nutrients.fats?.toString()				?: "")
				etProteins.setText(			food.nutrients.proteins?.toString()			?: "")
			}
		}

		/* UI listeners */

		view.findViewById<Button>(R.id.fragment_edit_food_btn_confirm).setOnClickListener {
			val name			: String	= etName.text.toString()
			val parent			: String	= etParent.text.toString()
			val kcal			: Float?	= etKcal.text.toString().toFloatOrNull()
			val carbohydrates	: Float?	= etCarbohydrates.text.toString().toFloatOrNull()
			val fats			: Float?	= etFats.text.toString().toFloatOrNull()
			val proteins		: Float?	= etProteins.text.toString().toFloatOrNull()

			/* check valid fields */
			if(name == "") {
				Toast.makeText(activity, "ERROR: Missing name", Toast.LENGTH_LONG).show()
			}
			/* edit food */
			else {
				viewModel.createFoodWithNutrients(
					name, parent, kcal, carbohydrates, fats, proteins
				)
				Log.i("PRODUCT_EDIT", "added $name $parent")
				Toast.makeText(activity, "Added: $name !", Toast.LENGTH_SHORT).show()
				navController.navigateUp()
			}
		}

		view.findViewById<Button>(R.id.fragment_edit_food_btn_cancel).setOnClickListener {
			Log.i("PRODUCT_EDIT", "not added")
			navController.navigateUp()
		}

		view.findViewById<Button>(R.id.fragment_edit_food_btn_map).setOnClickListener {

			/* osm map */
			if (parentFragmentManager.findFragmentByTag(FRAGMENT_TAG_MAP) == null) {

				navController.navigate(
					FragmentEditFoodDirections.actionFragmentEditFoodToFragmentMap()
				)
			}
		}

    }


	override fun onResume() {
		super.onResume()
		//this will refresh the osmdroid configuration on resuming.
		//if you make changes to the configuration, use
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

		//map.onResume() //needed for compass, my location overlays, v6.0.0 and up
	}

	override fun onPause() {
		super.onPause()
		//this will refresh the osmdroid configuration on resuming.
		//if you make changes to the configuration, use
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//Configuration.getInstance().save(this, prefs);

		//map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
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

		private const val TAG = "FOOD"
		private const val FRAGMENT_TAG_MAP = "fragment_map_edit_food"

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