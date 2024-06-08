package com.papum.homecookscompanion.view.stats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProductAndMealsWithNutrients
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.jvm.Throws

class StatsViewModel(
	private val repository: Repository
) : ViewModel() {

	/* last day of period and days of duration */
	private val lastDay	= MutableLiveData(LocalDate.now())
	private val days	= MutableLiveData(1L)


	/* queries */

	fun getMealsWithNutrients(): LiveData<List<EntityProductAndMealsWithNutrients>> =
		lastDay.switchMap { lastDay ->
			days.switchMap { days ->
				LocalDateTime.MIN.with(lastDay).plusDays(1).minusSeconds(1)
					.let { lastDayTime ->
						repository.getMeals(
							lastDayTime.with(LocalTime.MIN).minusDays(days - 1),
							lastDayTime
						)
					}
			}
		}

	/**
	 * Get the average of nutrients on the last `days`.
	 */
	fun getNutrientsAverage(): LiveData<EntityNutrients> =
		getMealsWithNutrients().switchMap { mealsWithNutrients ->
			MutableLiveData(
				mealsWithNutrients.map { it.nutrients }
					.reduceOrNull { acc, entityNutrients -> EntityNutrients(
							0,
							acc.kcal?.let			{ entityNutrients.kcal?.plus(it) },
							acc.carbohydrates?.let	{ entityNutrients.carbohydrates?.plus(it) },
							acc.fats?.let			{ entityNutrients.fats?.plus(it) },
							acc.proteins?.let		{ entityNutrients.proteins?.plus(it) },
						)
					}?.apply {
						days.value?.let { days ->
							kcal = kcal?.div(days)
							carbohydrates = carbohydrates?.div(days)
							fats = fats?.div(days)
							proteins = proteins?.div(days)
						}
					} ?: EntityNutrients(
					0,
					null,
					null,
					null,
					null
					)
			)
		}

	/* getters / setters */

	fun setDurationDays(days: Long) {
		this.days.value = days
		Log.d(TAG, "set duration days to $days")
	}



	companion object {

		private const val TAG = "STATSvm"

	}

}


class StatsViewModelFactory(
	private val repository: Repository
) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(StatsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return StatsViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}