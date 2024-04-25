package com.papum.homecookscompanion.services

import android.Manifest
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.papum.homecookscompanion.MainActivity
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit


/**
 * Worker (service) to check and send notification for low stocks of products.
 */
class WorkerStock(appContext: Context, workerParams: WorkerParameters)
	: Worker(appContext, workerParams) {


	override fun doWork(): Result {

		val repository = Repository(applicationContext)

		val products_lowStock = repository.getAllProductsWithInventoryAndAlerts_lowStocks()
		val names = products_lowStock.subList(0, PRODUCTS_NAMES_IN_NOTIFICATION_COUNT)
			.map { product -> product.product.name }
			.joinToString(",",
				postfix = (
						if(products_lowStock.size > PRODUCTS_NAMES_IN_NOTIFICATION_COUNT) "..."
						else ""
						)
			)

		sendNotification("${products_lowStock.size} products in low stock!", names)

		// Indicate whether the work finished successfully with the Result
		return Result.success()
	}

	private fun sendNotification(text: String, title: String) {
		val builder = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_dialog_info)
			.setContentTitle(title)
			.setContentText(text)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)

		val notificationManager = NotificationManagerCompat.from(applicationContext)
		if (ActivityCompat.checkSelfPermission(
				applicationContext,
				Manifest.permission.POST_NOTIFICATIONS
			) == PackageManager.PERMISSION_GRANTED
		) {
			notificationManager.notify(MainActivity.NOTIFICATION_ID_STOCK, builder.build())
		}
	}


	companion object {

		private const val PRODUCTS_NAMES_IN_NOTIFICATION_COUNT = 3

		private const val PERIODIC_INTERVAL_HOURS: Long = 24

		// hour of day when worker stock should be executed periodically
		private const val REPEATING_HOUR: Int = 8

		fun createConstraints() = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.NOT_REQUIRED)
			.setRequiresCharging(false)
			.setRequiresBatteryNotLow(true)
			.build()

		/*
		fun createWOrkRequest() : PeriodicWorkRequest {
			val stockWork_nextTime = LocalDateTime.now()
				.with(LocalTime.MIN)
				.withHour(REPEATING_HOUR)

			// Check stocks once a day, for that day
			return PeriodicWorkRequestBuilder<WorkerStock>(PERIODIC_INTERVAL_HOURS, TimeUnit.HOURS)
				.setConstraints(createConstraints())
				.setInitialDelay( Duration.between(LocalDateTime.now(), stockWork_nextTime) )
				.build()
		}
		*/

		fun createWOrkRequest() : PeriodicWorkRequest {

			// Check stocks once a day, for that day
			return PeriodicWorkRequestBuilder<WorkerStock>(100000, TimeUnit.SECONDS)
				.setConstraints(createConstraints())
				.build()
		}
	}

}