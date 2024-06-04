package com.papum.homecookscompanion.view.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndIngredientOf
import com.papum.homecookscompanion.model.database.EntityProductAndMealsWithNutrients
import com.papum.homecookscompanion.utils.UtilProducts
import kotlin.jvm.Throws

class StatsViewModel(
	private val repository: Repository
) : ViewModel() {


	private val lastDay		= MutableLiveData<Long>()
	private val duration	 = MutableLiveData<Long>()


	private fun getNutrients(): LiveData<List<EntityProductAndMealsWithNutrients>> =
		lastDay.switchMap { lastDay ->
			duration.switchMap { duration ->
				repository.getMeals()
		}




	companion object {

		private const val TAG = "STATSvm"

	}

}


class StatsViewModelFactory(
	private val repository: Repository,
	private val recipeId: Long
) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(StatsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return StatsViewModel(repository, recipeId) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}