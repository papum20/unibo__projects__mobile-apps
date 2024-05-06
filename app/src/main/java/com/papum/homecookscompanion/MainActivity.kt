package com.papum.homecookscompanion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.osmdroid.config.Configuration.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papum.homecookscompanion.view.services.ServiceNotificationStock
import com.papum.homecookscompanion.view.services.WorkerStock

class MainActivity : AppCompatActivity() {

	private lateinit var navController: NavController
	private lateinit var appBarConfiguration: AppBarConfiguration

	/* Permission launchers */
	private val notificationsPermissionAndNotifyLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (isGranted) {
			_fireNotification()
		}
	}

	private val bootPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (isGranted) {
			registerReceiver(broadcastReceiver_boot, IntentFilter(
				"android.intent.action.BOOT_COMPLETED"
			))
		}
	}

	/* Service connections */
	private lateinit var serviceNotificationStock: ServiceNotificationStock
	private val serviceConnection: ServiceConnection = object : ServiceConnection {

		override fun onServiceConnected(className: ComponentName, service: IBinder) {
			serviceNotificationStock = (service as ServiceNotificationStock.BinderStock).getService()
		}
		override fun onServiceDisconnected(name: ComponentName?) {
			/* Whatever */
		}
	}

	private val broadcastReceiver_boot = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
				val serviceIntent = Intent(context, ServiceNotificationStock::class.java)
				context!!.startForegroundService(serviceIntent)
			}
		}
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		/* osm */
		//handle permissions first, before map is created. not depicted here

		//load/initialize the osmdroid configuration, this can be done
		// This won't work unless you have imported this: org.osmdroid.config.Configuration.*
		getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

		// nav host fragment
		val navHostFragment = supportFragmentManager.findFragmentById(
			R.id.fragment_nav_container
		) as NavHostFragment
		navController = navHostFragment.navController


		// setup toolbar and navbar
		setSupportActionBar(findViewById(R.id.toolbar))
		findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)

		// setup actionbar with top level destinations
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.inventory,
				R.id.list,
				R.id.meals,
				R.id.products,
				R.id.stats
			)
		)
		setupActionBarWithNavController(navController, appBarConfiguration)


		/* Notifications */
		createNotificationChannel(
			CHANNEL_ID,
			getString(R.string.channel_name),
			getString(R.string.channel_description),
			NotificationManager.IMPORTANCE_DEFAULT
		)



		/* THREAD */
		/*
		val threadButton = findViewById<Button>(R.id.buttonThread)
		val labelCounter = findViewById<TextView>(R.id.labelCounter)

		threadButton.setOnClickListener{
			Thread(
				Runnable {
					var counter = 1000
					while (counter > 0) {
						Thread.sleep(10)
						counter--
						runOnUiThread{
							labelCounter.text = counter.toString()
						}
					}
				}).start()
		}

		/* COROUTINE */
		suspend fun semaphore(){
			withContext(Dispatchers.IO) {
				Thread.sleep(8000)
				Log.i("Semaphore", "Go on!")
			}
		}
		val coroutineButton = findViewById<Button>(R.id.buttonCoroutine)
		val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
		coroutineButton.setOnClickListener{
			val job: Job = scope.launch {
				val deferred: Deferred<Unit> = async{ semaphore() }
				var counter = 1000
				while (counter > 0) {
					delay(10)
					counter--
					if (counter < 500) {
						deferred.await()
					}
					labelCounter.text = counter.toString()
				}
			}
		}
		 */

		/* SERVICE */
		//startService(Intent(this, ServiceNotificationStock::class.java))

		/* BOUND SERVICE */

		/*
		bindService(
			Intent(this, ServiceNotificationStock::class.java),
			serviceConnection,
			BIND_AUTO_CREATE
		)
		 */
		//serviceNotificationStock.sendNotification()

		/* Services (workers) */

		// Create a periodic work request to send notifications
		val notificationWorkRequest = OneTimeWorkRequestBuilder<WorkerStock>().build()

		// Enqueue the work request to WorkManager
		WorkManager.getInstance(this).enqueueUniquePeriodicWork(
			WORKER_STOCK_NAME,
			ExistingPeriodicWorkPolicy.UPDATE,
			WorkerStock.createWorkRequest()
		)

		Log.d("WORK","MIN_PERIODIC_INTERVAL_MILLIS: ${PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS / 1000}")

	}


	/* Navigation */

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(appBarConfiguration)
	}


	/* Services */



	private fun createNotificationChannel(
		id: String,
		name: String,
		descriptionText: String,
		importance: Int
	){
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) -- app already requires api >= 30
		val channel	= NotificationChannel(id, name, importance).apply {
			description = descriptionText
		}
		val notificationManager = NotificationManagerCompat.from(this)
		notificationManager.createNotificationChannel(channel)
	}

	/**
	 * fire a notification;
	 * doesn't check for permission, use wrapper fireNotificationAndCheck()
	 */
	@SuppressLint("MissingPermission")
	fun _fireNotification() {
		val intent: Intent = Intent(this, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
			putExtra("caller", "notification")
		}
		val pendingIntent: PendingIntent = PendingIntent.getActivity(
			this, 0, intent, PendingIntent.FLAG_IMMUTABLE
		)

		val builder = NotificationCompat.Builder(this, CHANNEL_ID)
			.setSmallIcon(androidx.core.R.drawable.notification_bg)
			.setContentTitle("Remember that you will die!")
			.setContentText("Let me explain a number of reasons why this is the case, blah, blah, blah...")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
		builder.addAction(androidx.core.R.drawable.notification_action_background, "PRESS ME", pendingIntent)

		NotificationManagerCompat.from(this)
			.notify(NOTIFICATION_ID_STOCK, builder.build())
	}

	fun fireNotificationAndCheck() {

		with(NotificationManagerCompat.from(this)) {
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
				ActivityCompat.checkSelfPermission(
					this@MainActivity,
					Manifest.permission.POST_NOTIFICATIONS
				) != PackageManager.PERMISSION_GRANTED) {
				// POST_NOTIFICATIONS permission exists and is needed from api 33 TIRAMISU
				notificationsPermissionAndNotifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
				return@with
			} else {
				_fireNotification()

			}
		}
	}


	companion object {
		const val CHANNEL_ID = "Channel_HomecooksCompanion"

		// notificationId is a unique int for each notification that you must define.
		const val NOTIFICATION_ID_STOCK = 0

		const val WORKER_STOCK_NAME = "stock"

	}

}