package com.papum.homecookscompanion.view.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class BroadcastReceiverGeofence : BroadcastReceiver() {

	override fun onReceive(context: Context?, intent: Intent) {

		Log.d(TAG, "Received something")

		val geofencingEvent = GeofencingEvent.fromIntent(intent)
		if (geofencingEvent == null || geofencingEvent.hasError()) {
			val errorMessage = geofencingEvent?.let {
				GeofenceStatusCodes
					.getStatusCodeString(it.errorCode)
			} ?: "No geofencing event found!"
			Log.e(TAG, errorMessage)
			return
		}

		// Get the transition type.
		val geofenceTransition = geofencingEvent.geofenceTransition
		var messageToDisplay = "Something weird happened with the transition types"

		// Test that the reported transition was of interest.
		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
			geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

			// Get the geofences that were triggered. A single event can trigger multiple geofences.
			val triggeringGeofences = geofencingEvent.triggeringGeofences

			// Get the transition details as a String.
			messageToDisplay = triggeringGeofences.toString()
			
		} else {
			// Log the error.
			Log.e(TAG, geofenceTransition.toString())
		}

		Toast.makeText(context, messageToDisplay, Toast.LENGTH_LONG).show()
	}


	companion object {
		private const val TAG = "GEOFENCE"
	}


}