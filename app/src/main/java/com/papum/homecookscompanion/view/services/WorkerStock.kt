package com.papum.homecookscompanion.view.services

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.papum.homecookscompanion.MainActivity
import com.papum.homecookscompanion.model.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
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

	private fun sendNotification(title: String, text: String) {

		val intent: Intent = Intent(applicationContext, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		val pendingIntent: PendingIntent = PendingIntent.getActivity(
			applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
		)

		val builder = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
			.setSmallIcon(android.R.drawable.ic_popup_reminder)
			.setContentTitle(title)
			.setContentText(text)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(pendingIntent)

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

		/* arguments keys */
		private const val KEY_PENDING_INTENT = "pendingIntent"

		/* worker params */
		private const val PRODUCTS_NAMES_IN_NOTIFICATION_COUNT = 3

		private const val PERIODIC_INTERVAL_HOURS: Long = 24

		// hour of day when worker stock should be executed periodically
		private const val REPEATING_HOUR: Int = 8

		fun createConstraints() = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.NOT_REQUIRED)
			.setRequiresCharging(false)
			.setRequiresBatteryNotLow(true)
			.build()

		fun createWorkRequest() : PeriodicWorkRequest {

			val delayTo_nextTime = LocalDateTime.now().let { now ->
				now.with(LocalTime.MIN)
					.withHour(REPEATING_HOUR)
					.plusDays(1)
					.toInstant(ZoneOffset.UTC)
					.toEpochMilli() - now.toInstant(ZoneOffset.UTC).toEpochMilli()
			}

			// Check stocks once a day, for that day
			return PeriodicWorkRequestBuilder<WorkerStock>(PERIODIC_INTERVAL_HOURS, TimeUnit.HOURS)
				.setConstraints(createConstraints())
				.setInitialDelay( delayTo_nextTime, TimeUnit.MILLISECONDS )
				.build()
		}

		/*
		// Do it every 1000s, for debug
		fun createWorkRequest() : PeriodicWorkRequest {

			// Check stocks once a day, for that day
			return PeriodicWorkRequestBuilder<WorkerStock>(1000, TimeUnit.SECONDS)
				.setConstraints(createConstraints())
				.build()
		}
		 */

	}

}