package com.papum.homecookscompanion.view.meals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProductAndMealsWithNutrients
import com.papum.homecookscompanion.utils.errors.BadQuantityException
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.jvm.Throws

class MealsViewModel(private val repository: Repository) : ViewModel() {

	val currentlySetDate: MutableLiveData<LocalDateTime> = MutableLiveData(LocalDateTime.now()) 	// default to today


	/* queries */

	/**
	 * `month` from 1.
	 */
	private fun _getAllMealsWithNutrients(date: LocalDateTime): LiveData<List<EntityProductAndMealsWithNutrients>> {
		return repository.getMealsAndNutrients(
			date.with(LocalTime.MIN), date.with(LocalTime.MAX)
		)
	}

	/**
	 * Get all products for date `this.currentlySetDate`
	 */
	fun getAllMealsWithNutrients(): LiveData<List<EntityProductAndMealsWithNutrients>> =
		currentlySetDate.switchMap { date ->
			_getAllMealsWithNutrients(date)
		}


	/* insert */

	@Throws(BadQuantityException::class)
	fun updateQuantity(meal: EntityMeals, newQuantity: Float) {

		if(newQuantity < 0) {
			throw BadQuantityException("Quantity cannot be negative")
		}
		repository.insertMealBackToInventory(meal, newQuantity)
	}


}


class MealsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(MealsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return MealsViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}