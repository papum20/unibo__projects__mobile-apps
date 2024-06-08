package com.papum.homecookscompanion.view.inventory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.utils.errors.BadQuantityException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.jvm.Throws

class InventoryViewModel(private val repository: Repository) : ViewModel() {

	private var _addRecipeResult = MutableLiveData<AddRecipeResult?>(null)
	val addRecipeResult: MutableLiveData<AddRecipeResult?>
			get() = _addRecipeResult


	fun getAllProductsInInventoryWithAlerts(): LiveData<List<EntityProductAndInventoryWithAlerts>> {
		return repository.getInventoryWithAlerts()
	}

	fun removeFromInventory(inventoryItem: EntityInventory) {
		repository.deleteInventoryItem(inventoryItem)
	}

	/* Insert */

	fun addAlert(id: Long, quantity: Float): EntityAlerts {
		return repository.addAlert(EntityAlerts(id, quantity))
	}
	fun addToInventory(id: Long, quantity: Float): EntityInventory {
		return repository.addInventoryItemQuantity(EntityInventory(id, quantity))
	}
	fun addToList(id: Long, quantity: Float) {
		repository.addListItem(EntityList(id, quantity))
	}

	@Throws(BadQuantityException::class)
	fun addToMealsFromInventory(
		inventoryItem: EntityInventory, date: LocalDateTime, quantity: Float
	) {

		try {
			repository.insertMealFromInventory(
				EntityMeals(0, inventoryItem.idProduct, date, quantity),
				inventoryItem
			)
		} catch (e: BadQuantityException) {
			Log.e(TAG, "Error: ${e.message}")
			throw BadQuantityException("insufficient quantity in inventory")
		}
	}

	suspend fun cookRecipe(recipeId: Long, quantity: Float) {

		withContext(Dispatchers.IO) {
			try {
				repository.insertRecipeCookedInInventory_value(recipeId, quantity)
				_addRecipeResult.postValue(AddRecipeResult.SUCCESS)
			} catch (e: BadQuantityException) {
				Log.e(TAG, "Error: ${e.message}")
				_addRecipeResult.postValue(AddRecipeResult.BAD_QUANTITY)
			}
		}
	}

	/* Update */

	fun updateInventory(inventoryItem: EntityInventory, newQuantity: Float): EntityInventory {
		val newInventoryItem = inventoryItem.apply {
			this.quantity = newQuantity
		}
		repository.updateInventoryItem(newInventoryItem)
		return newInventoryItem
	}
	
	fun updateAlert(alert: EntityAlerts, newQuantity: Float): EntityAlerts {
		val newAlert = alert.apply {
			this.quantity = newQuantity
		}
		repository.updateAlert(newAlert)
		return newAlert
	}


	/* setters/getters */

	fun resetAddRecipeResult() {
		_addRecipeResult.value = null
	}



	companion object {
		private const val TAG = "InventoryVM"

		enum class AddRecipeResult {
			BAD_QUANTITY,
			SUCCESS
		}
	}


}


class InventoryViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return InventoryViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}