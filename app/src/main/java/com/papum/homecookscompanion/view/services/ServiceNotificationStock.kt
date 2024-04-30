package com.papum.homecookscompanion.view.services

import android.Manifest
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.papum.homecookscompanion.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Service to send notification for low stocks of products.
 */
class ServiceNotificationStock : Service() {

	private val INTERVAL = 8000 // 8 seconds

	/*
	private val binder = BinderStock()
	private var notificationJob: Job? = null
	 */

	inner class BinderStock: Binder() {
		fun getService(): ServiceNotificationStock {

			return this@ServiceNotificationStock
		}
	}



	override fun onBind(intent: Intent?): IBinder? {
		//return binder
		return null
	}

	/*
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		// Start the coroutine to send notifications repeatedly
		notificationJob = CoroutineScope(Dispatchers.Default).launch {
			sendNotifications()
		}
        return START_STICKY
    }
	 */

	/*
	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

		val input = intent.getStringExtra("inputExtra")
		val notificationIntent = Intent(this, MainActivity::class.java)
		val pendingIntent = PendingIntent.getActivity(
			this,
			0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
		)
		val notification: Notification = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
			.setContentTitle("Auto Start Service")
			.setContentText(input)
			.setSmallIcon(androidx.core.R.drawable.notification_bg)
			.setContentIntent(pendingIntent)
			.build()

		val notificationManager = NotificationManagerCompat.from(this)
		val channel = NotificationChannel(
			MainActivity.CHANNEL_ID,
			MainActivity.CHANNEL_NAME,
			NotificationManager.IMPORTANCE_DEFAULT
		)
		notificationManager.createNotificationChannel(channel)
		NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)

		startForeground(1, notification)
		return START_NOT_STICKY
	}
	 */

	/*
	override fun onDestroy() {
		// Cancel the coroutine job when the service is destroyed
		notificationJob?.cancel()
		super.onDestroy()
	}
	 */

	/*
	private suspend fun sendNotifications() {
		while (notificationJob?.isActive == true) {
			sendNotification()
			// Delay for the specified interval before sending the next notification
			delay(INTERVAL.toLong())
		}
	}
	*/

	fun sendNotification() {
		val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
			.setSmallIcon(androidx.core.R.drawable.notification_bg)
			.setContentTitle("Fired from a Service!")
			.setContentText("content text")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)

		val notificationManager = NotificationManagerCompat.from(this)
		if (ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.POST_NOTIFICATIONS
			) == PackageManager.PERMISSION_GRANTED
		) {
            notificationManager.notify(MainActivity.NOTIFICATION_ID_STOCK, builder.build())
		}
	}

}