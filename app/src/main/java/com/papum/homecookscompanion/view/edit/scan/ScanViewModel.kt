package com.papum.homecookscompanion.view.edit.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventory
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.EntityProductAndList
import java.time.LocalDateTime

class ScanViewModel(private val repository: Repository) : ViewModel() {


	/* Query */



	/* Insert */



}


class ScanViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ScanViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ScanViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}