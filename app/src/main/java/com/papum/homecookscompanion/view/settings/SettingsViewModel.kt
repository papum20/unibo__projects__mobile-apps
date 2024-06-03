package com.papum.homecookscompanion.view.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Geofence
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityShops
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.jvm.Throws

class SettingsViewModel(private val repository: Repository) : ViewModel() {

	var shops: List<EntityShops>? = null


	fun loadShops() {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				shops = repository.getAllShops_value()
				Log.d(TAG, "loaded shops: ${shops!!.size}")
			}
		}
	}


	fun getGeofencesList() : List<Geofence>? {
		return shops?.map { shop ->
			Geofence.Builder()
				.setRequestId(getGeofenceId(shop))
				.setCircularRegion(
					shop.latitude,
					shop.longitude,
					GEOFENCE_RADIUS_METERS
				)
				.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
				.build()
		}
	}

	fun getGeofenceId(shop: EntityShops) : String =
		"SHOP_${shop.id}"



	companion object {

		private const val TAG = "SETTINGSvm"

		private const val GEOFENCE_RADIUS_METERS	= 150f
		private const val GEOFENCE_DURATION_MILLIS	= 1000000L

	}


}


class SettingsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return SettingsViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}