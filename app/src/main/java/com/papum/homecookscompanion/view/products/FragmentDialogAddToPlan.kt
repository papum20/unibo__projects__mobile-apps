package com.papum.homecookscompanion.view.products

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class FragmentDialogAddToPlan(
	private val listener: IListenerDialog
) : DialogFragment() {


	// Callbacks for dialog buttons
	interface IListenerDialog {
		fun onClickAddToPlan(dialog: DialogFragment, productId: Long, date: Date, quantity: Float)
		fun onClickAddToPlanCancel(dialog: DialogFragment)
	}


	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		val productId = arguments?.getLong(KEY_PRODUCT)

		val viewModel: ProductsViewModel by viewModels {
			ProductsViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		return activity?.let {

			val builder = AlertDialog.Builder(it)

			val inflater	= requireActivity().layoutInflater

			val dialogView	= inflater.inflate(R.layout.dialog_products_add_to_plan, null)
			val tvName	= dialogView.findViewById<TextView>(R.id.dialog_products_addToPlan_tv)

			Log.d("PRODUCTS_DIALOG", "product id: ${arguments?.getLong(KEY_PRODUCT).toString()}")
			productId?.let { productId ->
				viewModel.getProduct_fromId(productId).observe(this) { product ->
					Log.d("PRODUCTS_DIALOG", "listProducts: ${product.toString()}")
					if(product != null) {
						tvName.text = product.parent?.let { p ->
							"${product.name}, $p"
						} ?: product.name
					}
				}
			}

			builder
				.setView(dialogView)
				.setPositiveButton("Add") { dialog, id ->
					// add quantity of product to list
					val quantity	= dialogView.findViewById<EditText>(R.id.dialog_products_addToPlan_et)
						.text.toString().toFloatOrNull()
					val date		= dialogView.findViewById<EditText>(R.id.dialog_products_addToPlan_date)
						.text.toString().let { text ->
							Log.d("DATE", text)
							val format = SimpleDateFormat("dd/MM/yyyy")
							try {
								format.parse(text)
							} catch (e: ParseException) {
								Date(0)
							}
						}
					if(quantity != null)
						productId?.let { id ->
							listener.onClickAddToPlan(this, id, date, quantity)
						}
				}
				.setNegativeButton("Cancel") { dialog, id ->
					// User cancelled the dialog
					listener.onClickAddToPlanCancel(this)
				}

			// Create the AlertDialog object and return it.
			builder.create()
		} ?: throw IllegalStateException("Activity cannot be null")

	}


	/* companion */

	companion object {

		private const val KEY_PRODUCT: String = "product"

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 */
		@JvmStatic
		fun newInstance(listener: IListenerDialog, product: EntityProduct) =
			FragmentDialogAddToPlan(listener).apply {
				arguments = Bundle().apply {
					//putParcelable("product", product)
					putLong(KEY_PRODUCT, product.id)
				}
			}
	}

}
