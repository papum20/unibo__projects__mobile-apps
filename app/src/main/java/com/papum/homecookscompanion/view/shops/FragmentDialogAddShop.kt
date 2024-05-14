package com.papum.homecookscompanion.view.shops

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityShops
import com.papum.homecookscompanion.view.products.FragmentDialogAddToInventory

class FragmentDialogAddShop : DialogFragment() {

	private lateinit var navController: NavController
	private val viewModel: ShopsViewModel by viewModels {
		ShopsViewModelFactory(
			Repository(requireActivity().application)
		)
	}


	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		val latitude	= requireArguments().getDouble(KEY_LATITUDE)
		val longitude	= requireArguments().getDouble(KEY_LONGITUDE)

		navController = findNavController()


		val builder		= AlertDialog.Builder(requireActivity())
		val inflater	= requireActivity().layoutInflater
		val dialogView	= inflater.inflate(R.layout.dialog_products_add_shop, null)

		val etAddress	= dialogView.findViewById<EditText>(R.id.dialog_addShop_address_et)
		val etBrand		= dialogView.findViewById<EditText>(R.id.dialog_addShop_brand_et)
		val etCity		= dialogView.findViewById<EditText>(R.id.dialog_addShop_city_et)
		val etState		= dialogView.findViewById<EditText>(R.id.dialog_addShop_state_et)
		dialogView.findViewById<TextView>(R.id.dialog_addShop_latlng)
			.setText( requireContext().getString(R.string.shop_format_latlng, latitude, longitude) )

		builder
			.setView(dialogView)
			.setPositiveButton(requireContext().getString(R.string.btn_add)) { dialog, id ->
				viewModel.addShop(
					EntityShops(
						0,
						etAddress.text.toString(),
						etBrand.text.toString(),
						etCity.text.toString(),
						etState.text.toString(),
						latitude,
						longitude
					)
				)
				Toast.makeText(requireContext(),
					"Shop ${etBrand.text} added!", Toast.LENGTH_SHORT)
					.show()
				Log.d(TAG, "Shop ${etBrand.text} added!")

				navController.navigateUp()
			}
			.setNegativeButton(requireContext().getString(R.string.btn_cancel)) { dialog, id ->
				// User cancelled the dialog
			}

		// Create the AlertDialog object and return it.
		return builder.create()

	}


	/* companion */

	companion object {

		private const val TAG: String = "DIALOG_ADD_SHOP"

		private const val KEY_LATITUDE: String	= "latitude"
		private const val KEY_LONGITUDE: String	= "longitude"

		const val DIALOG_TAG: String = "dialog_add_shop"


		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 */
		@JvmStatic
		fun newInstance(latitude: Double, longitude: Double) =
			FragmentDialogAddShop().apply {
				arguments = Bundle().apply {
					//putParcelable("product", product)
					putDouble(KEY_LATITUDE,		latitude)
					putDouble(KEY_LONGITUDE,	longitude)
				}
			}
	}

}
