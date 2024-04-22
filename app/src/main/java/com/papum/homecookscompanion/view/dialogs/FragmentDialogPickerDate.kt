package com.papum.homecookscompanion.view.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class FragmentDialogPickerDate(
	private val listener: DatePickerDialog.OnDateSetListener
) : DialogFragment(), DatePickerDialog.OnDateSetListener {


	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		// Use the current date as the default date in the picker, if not provided as arguments.
		val calendar	= Calendar.getInstance()

		val year		= arguments?.getInt(FragmentDialogPickerDate.KEY_DATE_YEAR)
			?: calendar.get(Calendar.YEAR)
		val month		= arguments?.getInt(FragmentDialogPickerDate.KEY_DATE_MONTH)
			?: calendar.get(Calendar.MONTH)
		val day			= arguments?.getInt(FragmentDialogPickerDate.KEY_DATE_DAY)
			?: calendar.get(Calendar.DAY_OF_MONTH)

		return DatePickerDialog(requireContext(), this, year, month, day)
	}


	/* DatePickerDialog.OnDateSetListener */

	override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
		listener.onDateSet(view, year, month, day)
	}


	/* companion */

	companion object {

		private const val KEY_DATE_YEAR:	String = "date_year"
		private const val KEY_DATE_MONTH:	String = "date_month"
		private const val KEY_DATE_DAY:		String = "date_day"

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 * Provide the default values here.
		 * Month starts from 0.
		 */
		@JvmStatic
		fun newInstance(
			listener: DatePickerDialog.OnDateSetListener,
			year: Int,
			month: Int,
			day: Int
		) =
			FragmentDialogPickerDate(listener).apply {
				arguments = Bundle().apply {
					//putParcelable("product", product)
					putInt(KEY_DATE_YEAR,	year)
					putInt(KEY_DATE_MONTH,	month)
					putInt(KEY_DATE_DAY,	day)
				}
			}
	}

}
