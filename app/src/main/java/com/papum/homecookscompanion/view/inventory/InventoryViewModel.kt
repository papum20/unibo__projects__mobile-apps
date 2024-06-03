package com.papum.homecookscompanion.view.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import java.time.LocalDateTime
import kotlin.jvm.Throws

class InventoryViewModel(private val repository: Repository) : ViewModel() {


	fun getAllProductsInInventoryWithAlerts(): LiveData<List<EntityProductAndInventoryWithAlerts>> {
		return repository.getAllInventoryWithAlerts_value()
	}

	fun removeFromInventory(inventoryItem: EntityInventory) {
		repository.deleteInventoryItem(inventoryItem)
	}

	/* Insert */

	fun addAlert(id: Long, quantity: Float): EntityAlerts {
		return repository.addAlert(EntityAlerts(id, quantity))
	}

	fun addToInventory(id: Long, quantity: Float): EntityInventory {
		return repository.addInventoryItem(EntityInventory(id, quantity))
	}

	fun addToList(id: Long, quantity: Float) {
		repository.addListItem(EntityList(id, quantity))
	}

	fun addToMealsFromInventory(inventoryItem: EntityInventory, date: LocalDateTime, quantity: Float) {
		repository.insertMealFromInventory(
			EntityMeals(0, inventoryItem.idProduct, date, quantity),
			inventoryItem
		)
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