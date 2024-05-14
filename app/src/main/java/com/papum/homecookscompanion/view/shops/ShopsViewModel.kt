package com.papum.homecookscompanion.view.shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProductAndInventory
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.EntityShops
import java.time.LocalDateTime

class ShopsViewModel(private val repository: Repository) : ViewModel() {

	/* query */

	fun getAllShops(): LiveData<List<EntityShops>> {
		return repository.getAllShops()
	}

	/* insert */

	fun addShop(shop: EntityShops) {
		repository.insertShop(shop)
	}

}


class ShopsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ShopsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ShopsViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}