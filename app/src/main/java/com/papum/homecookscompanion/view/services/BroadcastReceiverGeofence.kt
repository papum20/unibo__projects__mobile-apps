package com.papum.homecookscompanion.view.services

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.papum.homecookscompanion.MainActivity

class BroadcastReceiverGeofence : BroadcastReceiver() {

	override fun onReceive(context: Context?, intent: Intent) {

		Log.d(TAG_GEOFENCE, "Received something")

		val geofencingEvent = GeofencingEvent.fromIntent(intent)
		if (geofencingEvent == null || geofencingEvent.hasError()) {
			val errorMessage = geofencingEvent?.let {
				GeofenceStatusCodes
					.getStatusCodeString(it.errorCode)
			} ?: "No geofencing event found!"
			Log.e(TAG_GEOFENCE, errorMessage)
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

			// Send notification and log the transition details.
			sendNotification(context, "Location detected", "you entered a shop. Tap to open the app. $messageToDisplay")
			Log.i(TAG_GEOFENCE, messageToDisplay)

		} else {
			// Log the error.
			Log.e(TAG_GEOFENCE, geofenceTransition.toString())
		}

		Toast.makeText(context, messageToDisplay, Toast.LENGTH_LONG).show()
	}


	private fun sendNotification(context: Context?, title: String, text: String) {

		if(context == null) {
			Log.e(TAG_NOTIFICATIONS, "Context is null")
			return
		}

		val intent: Intent = Intent(context, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		val pendingIntent: PendingIntent = PendingIntent.getActivity(
			context, 0, intent, PendingIntent.FLAG_IMMUTABLE
		)

		val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
			.setSmallIcon(android.R.drawable.ic_menu_mylocation)
			.setContentTitle(title)
			.setContentText(text)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(pendingIntent)

		val notificationManager = NotificationManagerCompat.from(context)
		if (ActivityCompat.checkSelfPermission(
				context,
				Manifest.permission.POST_NOTIFICATIONS
			) == PackageManager.PERMISSION_GRANTED
		) {
			notificationManager.notify(MainActivity.NOTIFICATION_ID_GEOFENCE_SHOP, builder.build())
		}
	}



	companion object {
		private const val TAG_GEOFENCE		= "GEOFENCE"
		private const val TAG_NOTIFICATIONS	= "GEOFENCE"
	}


}