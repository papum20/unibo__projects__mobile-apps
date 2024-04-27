package com.papum.homecookscompanion.view.products

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.view.dialogs.FragmentDialogPickerDate
import com.papum.homecookscompanion.view.dialogs.FragmentDialogPickerTime
import java.time.LocalDateTime

class FragmentDialogAddToMeals(
	private val listener: IListenerDialog
) :
	DialogFragment(),
	DatePickerDialog.OnDateSetListener,
	TimePickerDialog.OnTimeSetListener
{

	private var currentlySetDateTime	= LocalDateTime.of(0, 1, 1, 0, 0)
	private lateinit var viewDialog: View


	// Callbacks for dialog buttons
	interface IListenerDialog {
		fun onClickAddToMeals(dialog: DialogFragment, productId: Long, date: LocalDateTime, quantity: Float)
		fun onClickAddToMealsCancel(dialog: DialogFragment)
	}


	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		val productId = arguments?.getLong(KEY_PRODUCT)

		val viewModel: ProductsViewModel by viewModels {
			ProductsViewModelFactory(
				Repository(requireActivity().application)
			)
		}

		// use current date/time as default
		currentlySetDateTime = LocalDateTime.now()


		return activity?.let {

			val builder = AlertDialog.Builder(it)

			val inflater = requireActivity().layoutInflater

			viewDialog	= inflater.inflate(R.layout.dialog_products_add_to_meals, null)
			val tvName	= viewDialog.findViewById<TextView>(R.id.dialog_products_addToMeals_tv)

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

			// set default time/date
			onDateSet(null,
				currentlySetDateTime.year, currentlySetDateTime.monthValue - 1, currentlySetDateTime.dayOfMonth)
			onTimeSet(null,
				currentlySetDateTime.hour, currentlySetDateTime.minute)

			// add button listeners for opening date/time picker
			viewDialog.findViewById<Button>(R.id.dialog_products_addToMeals_date_btn).setOnClickListener {
				val newFragment = FragmentDialogPickerDate.newInstance(this,
					currentlySetDateTime.year, currentlySetDateTime.monthValue - 1, currentlySetDateTime.dayOfMonth)
				newFragment.show(parentFragmentManager, "pickerDate")
			}
			viewDialog.findViewById<Button>(R.id.dialog_products_addToMeals_time_btn).setOnClickListener {
				val newFragment = FragmentDialogPickerTime.newInstance(this,
					currentlySetDateTime.hour, currentlySetDateTime.minute)
				newFragment.show(parentFragmentManager, "pickerTime")
			}

			// set the view to build in the dialog
			builder
				.setView(viewDialog)
				.setPositiveButton("Add") { dialog, id ->

					// add quantity of product to list
					val quantity	= viewDialog.findViewById<EditText>(R.id.dialog_products_addToMeals_quantity_et)
						.text.toString().toFloatOrNull()

					if(quantity != null && productId != null)
						listener.onClickAddToMeals(this, productId, currentlySetDateTime, quantity)
				}
				.setNegativeButton("Cancel") { dialog, id ->
					// User cancelled the dialog
					listener.onClickAddToMealsCancel(this)
				}

			// Create the AlertDialog object and return it.
			builder.create()
		} ?: throw IllegalStateException("Activity cannot be null")

	}


	/* Date/Time interfaces */


	override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
		currentlySetDateTime = LocalDateTime.of(
			year,
			month + 1,		// DatePicker is zero-based
			dayOfMonth,
			currentlySetDateTime.hour,
			currentlySetDateTime.minute
		)

		viewDialog.findViewById<Button>(R.id.dialog_products_addToMeals_date_btn)?.text =
			getString(R.string.dialog_addTo_format_date,
				currentlySetDateTime.dayOfMonth, currentlySetDateTime.monthValue, currentlySetDateTime.year)
	}

	override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
		currentlySetDateTime = LocalDateTime.of(
			currentlySetDateTime.year,
			currentlySetDateTime.month,
			currentlySetDateTime.dayOfMonth,
			hourOfDay,
			minute
		)

		Log.d("DIALOG_DATE", "$hourOfDay, $minute")
		Log.d("DIALOG_DATE", currentlySetDateTime.toString())
		Log.d("DIALOG_DATE", this.view.toString())

		viewDialog.findViewById<Button>(R.id.dialog_products_addToMeals_time_btn)?.text =
			getString(R.string.dialog_addTo_format_time,
				currentlySetDateTime.hour, currentlySetDateTime.minute)
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
			FragmentDialogAddToMeals(listener).apply {
				arguments = Bundle().apply {
					//putParcelable("product", product)
					putLong(KEY_PRODUCT, product.id)
				}
			}
	}

}
