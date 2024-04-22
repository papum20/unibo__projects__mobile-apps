package com.papum.homecookscompanion.view.dialogs

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import java.util.Calendar

class FragmentDialogPickerTime(
	private val listener: TimePickerDialog.OnTimeSetListener
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {


	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		// Use the current date as the default date in the picker, if not provided as arguments.
		val calendar	= Calendar.getInstance()

		val hour		= arguments?.getInt(FragmentDialogPickerTime.KEY_TIME_HOUR)
			?: calendar.get(Calendar.HOUR)
		val minute		= arguments?.getInt(FragmentDialogPickerTime.KEY_TIME_MINUTE)
			?: calendar.get(Calendar.MINUTE)

		return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
	}


	/* TimePickerDialog.OnTimeSetListener */

	override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
		listener.onTimeSet(view, hourOfDay, minute)
	}


	/* companion */

	companion object {

		private const val KEY_TIME_HOUR:	String = "time_hour"
		private const val KEY_TIME_MINUTE:	String = "time_minute"

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 * Provide the default values here.
		 */
		@JvmStatic
		fun newInstance(
			listener: TimePickerDialog.OnTimeSetListener,
			hour: Int,
			minute: Int
		) =
			FragmentDialogPickerTime(listener).apply {
				arguments = Bundle().apply {
					//putParcelable("product", product)
					putInt(FragmentDialogPickerTime.KEY_TIME_HOUR,		hour)
					putInt(FragmentDialogPickerTime.KEY_TIME_MINUTE,	minute)
				}
			}
	}

}
