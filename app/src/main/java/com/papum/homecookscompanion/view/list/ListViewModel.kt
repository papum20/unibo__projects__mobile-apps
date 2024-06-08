package com.papum.homecookscompanion.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityProductAndList
import kotlin.jvm.Throws

class ListViewModel(private val repository: Repository) : ViewModel() {

	fun getAllProductsInList(): LiveData<List<EntityProductAndList>> {
		return repository.getList()
	}

	fun addToList(id: Long, quantity: Float): EntityList {
		return repository.addListItem(EntityList(id, quantity))
	}

	fun removeProductFromList(listItem: EntityList) {
		repository.deleteListItem(listItem)
	}

	/**
	 * @return the updated list item
	 */
	fun updateListItem(listItem: EntityList, newQuantity: Float): EntityList {
		val newListItem = listItem.apply {
			this.quantity = newQuantity
		}
		repository.updateListItem(newListItem)
		return newListItem
	}

}


class ListViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ListViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ListViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}