package com.papum.homecookscompanion.view.maps

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.database.EntityProduct
import org.osmdroid.util.GeoPoint

class MapSelectPointViewModel() : ViewModel() {

	private var _selectedPoint = MutableLiveData<GeoPoint?>()
	val selectedPoint: LiveData<GeoPoint?>
		get() = _selectedPoint


	fun askSelectProduct() {
		_selectedPoint.value = null
	}

	fun selectPoint(point: GeoPoint) {
		_selectedPoint.value = point
	}

}


/**
 * Factory for constructing ProductResultViewModel, scoping it to the provided activity.
 */
class MapSelectPointViewModelFactory(private val activity: FragmentActivity) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(MapSelectPointViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ViewModelProvider(activity)[MapSelectPointViewModel::class.java] as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}