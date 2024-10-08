package com.papum.homecookscompanion.view.dialogs

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
import com.papum.homecookscompanion.view.products.ProductsViewModel
import com.papum.homecookscompanion.view.products.ProductsViewModelFactory
import kotlin.jvm.Throws

class FragmentDialogAddToInventory(
	private val listener: IListenerDialog
) : DialogFragment() {


	// Callbacks for dialog buttons
	interface IListenerDialog {
		fun onClickAddToInventory(dialog: DialogFragment, productId: Long, quantity: Float)
		fun onClickAddToInventoryCancel(dialog: DialogFragment)
	}


	@Throws(IllegalStateException::class)
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

			val dialogView	= inflater.inflate(R.layout.dialog_products_add_to_inventory, null)
			val tvName	= dialogView.findViewById<TextView>(R.id.dialog_products_addToInventory_tv)

			Log.d("PRODUCTS_DIALOG", "product id: ${arguments?.getLong(KEY_PRODUCT).toString()}")
			productId?.let { productId ->
				viewModel.getProduct(productId).observe(this) { product ->
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
					val quantity = dialogView.findViewById<EditText>(R.id.dialog_products_addToInventory_et)
						.text.toString().toFloatOrNull()
					if(quantity != null)
						productId?.let { id ->
							listener.onClickAddToInventory(this, id, quantity)
						}
				}
				.setNegativeButton("Cancel") { dialog, id ->
					// User cancelled the dialog
					listener.onClickAddToInventoryCancel(this)
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
			FragmentDialogAddToInventory(listener).apply {
				arguments = Bundle().apply {
					//putParcelable("product", product)
					putLong(KEY_PRODUCT, product.id)
				}
			}
	}

}
