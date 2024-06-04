package com.papum.homecookscompanion

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.osmdroid.config.Configuration.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.papum.homecookscompanion.utils.Const
import com.papum.homecookscompanion.utils.UtilServices
import com.papum.homecookscompanion.view.services.ServiceNotificationStock
import com.papum.homecookscompanion.view.services.WorkerStock

class MainActivity : AppCompatActivity() {

	private lateinit var navController: NavController
	private lateinit var appBarConfiguration: AppBarConfiguration



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
		val drawerLayout: DrawerLayout = findViewById(R.id.drawer)
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.inventory,
				R.id.list,
				R.id.meals,
				R.id.products,
				R.id.stats
			), drawerLayout
		)
		setupActionBarWithNavController(navController, appBarConfiguration)
		val navView: NavigationView = findViewById(R.id.nav_view)
		navView.setupWithNavController(navController)

		/* Notifications */
		UtilServices.createNotificationChannel(
			this,
			Const.ID_CHANNEL_STOCK,
			getString(R.string.channel_stock_name),
			getString(R.string.channel_stock_description),
			NotificationManager.IMPORTANCE_DEFAULT
		)

		UtilServices.createNotificationChannel(
			this,
			Const.ID_CHANNEL_GEOFENCES,
			getString(R.string.channel_geofences_name),
			getString(R.string.channel_geofences_description),
			NotificationManager.IMPORTANCE_DEFAULT
		)

		/* Services (workers) */

		// Enqueue the Stock work request to WorkManager
		val workManager = WorkManager.getInstance(this).apply {
			enqueueUniquePeriodicWork(
				WORKER_STOCK_NAME,
				ExistingPeriodicWorkPolicy.UPDATE,
				WorkerStock.createWorkRequest()
			)
		}
		workManager.getWorkInfosForUniqueWorkLiveData(WORKER_STOCK_NAME).observe(this) { info ->
			Log.d(TAG, "Works info for $WORKER_STOCK_NAME (size ${info.size}):\n${info.joinToString(separator = "\n") { it.toString() }}")
		}


		Log.d(TAG,"Added work request to work manager for stocks")

	}


	/* Navigation */

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(appBarConfiguration)
	}


	/* Permissions */

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

	private val broadcastReceiver_boot = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
				val serviceIntent = Intent(context, ServiceNotificationStock::class.java)
				context!!.startForegroundService(serviceIntent)
			}
		}
	}




	companion object {

		private const val TAG = "MainActivity"

		// notificationId is a unique int for each notification that you must define.
		const val NOTIFICATION_ID_STOCK			= 0
		const val NOTIFICATION_ID_GEOFENCE_SHOP	= 1

		const val WORKER_STOCK_NAME = "stock"

	}

}