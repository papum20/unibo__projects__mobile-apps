package com.papum.homecookscompanion.view.maps

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityShops
import org.osmdroid.util.GeoPoint
import kotlin.jvm.Throws

class MapShopsSelectPointViewModel(private val repository: Repository) : ViewModel() {


	fun getAllShops(): LiveData<List<EntityShops>> {
		return repository.getAllShops()
	}


}


/**
 * Factory for constructing ProductResultViewModel, scoping it to the provided activity.
 */
class MapShopsSelectPointViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(MapShopsSelectPointViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return MapShopsSelectPointViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}