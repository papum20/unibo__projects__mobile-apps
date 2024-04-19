package com.papum.homecookscompanion.view.products

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityProduct

class FragmentDialogAddToList(
	private val listener: IListenerDialog
) : DialogFragment() {


	// Callbacks for dialog buttons
	interface IListenerDialog {
		fun onClickAdd(dialog: DialogFragment, quantity: Float)
		fun onClickCancel(dialog: DialogFragment)
	}


	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		val productId	= arguments?.getLong(getString(R.string.bundle_FragmentDialogAddToList_product))
		//val product		=  ProductsViewModel.getAllProductsWithList_withId(productId)

		return activity?.let {

			val builder = AlertDialog.Builder(it)

			val inflater	= requireActivity().layoutInflater
			val dialogView	= inflater.inflate(R.layout.dialog_products_add_to_list, null)

			builder
				.setView(dialogView)
				.setPositiveButton("Add") { dialog, id ->
					// add quantity of product to list
					val quantity = dialogView.findViewById<EditText>(R.id.dialog_products_addToList_et)
						.text.toString().toFloatOrNull()
					if(quantity != null)
						listener.onClickAdd(this, quantity)
				}
				.setNegativeButton("Cancel") { dialog, id ->
					// User cancelled the dialog
					listener.onClickCancel(this)
				}

			// Create the AlertDialog object and return it.
			builder.create()
		} ?: throw IllegalStateException("Activity cannot be null")

	}

	/* companion */

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 */
		@JvmStatic
		fun newInstance(listener: IListenerDialog, product: EntityProduct) =
			FragmentDialogAddToList(listener).apply {
				arguments = Bundle().apply {
					//putParcelable("product", product)
					putLong(getString(R.string.bundle_FragmentDialogAddToList_product), product.id)
				}
			}
	}

}
