package com.papum.homecookscompanion.utils

import android.app.NotificationChannel
import android.content.Context
import androidx.core.app.NotificationManagerCompat

object UtilServices {

	fun createNotificationChannel(
		context: Context,
		id: String,
		name: String,
		descriptionText: String,
		importance: Int
	){
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) -- app already requires api >= 30
		val channel	= NotificationChannel(id, name, importance).apply {
			description = descriptionText
		}
		val notificationManager = NotificationManagerCompat.from(context)
		notificationManager.createNotificationChannel(channel)
	}

}