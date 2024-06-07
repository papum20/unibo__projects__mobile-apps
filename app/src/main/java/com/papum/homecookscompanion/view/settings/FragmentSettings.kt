package com.papum.homecookscompanion.view.settings

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.papum.homecookscompanion.MainActivity
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.utils.Const
import com.papum.homecookscompanion.view.services.BroadcastReceiverGeofence


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAbout.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSettings : Fragment(R.layout.page_fragment_settings) {


	private val viewModel: SettingsViewModel by viewModels {
		SettingsViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	/* geolocation */
	private lateinit var geofencingClient: GeofencingClient


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_fragment_settings, container, false)
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel.loadShops()

		/* geolocation */
		geofencingClient = LocationServices.getGeofencingClient(requireActivity())

		/* UI listeners */
		view.findViewById<Button>(R.id.settings_btn_geofence).setOnClickListener {
			check_and_showDialogAskCoarseLocation()
		}

	}


	/* dialogs */

	/**
	 * Ask for coarse location, or go to next step (fine).
	 */
	private fun check_and_showDialogAskCoarseLocation() {

		if( ActivityCompat.checkSelfPermission(
			requireContext(),
			Manifest.permission.ACCESS_COARSE_LOCATION
		) != PackageManager.PERMISSION_GRANTED
		) {

			val builder: AlertDialog.Builder = AlertDialog.Builder(context)
			builder
				.setMessage("We need coarse location before asking for background location.")
				.setTitle("Grant access to coarse location")
				.setPositiveButton(getString(R.string.btn_enable)) { dialog, which ->
					locationCoarsePermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
				}
				.setNegativeButton(getString(R.string.btn_cancel)) { dialog, which ->
					showErrorPermissionAborted()
				}


			val dialog: AlertDialog = builder.create()
			dialog.show()
		} else {
			check_and_showDialogAskFineLocation()
		}

	}

	/**
	 * Ask for fine location, or go to next step (background).
	 */
	private fun check_and_showDialogAskFineLocation() {

		if( ActivityCompat.checkSelfPermission(
			requireContext(),
			Manifest.permission.ACCESS_FINE_LOCATION
		) != PackageManager.PERMISSION_GRANTED
		) {
			val builder: AlertDialog.Builder = AlertDialog.Builder(context)
			builder
				.setMessage("We need fine location before asking for background location.")
				.setTitle("Grant access to and fine location")
				.setPositiveButton(getString(R.string.btn_enable)) { dialog, which ->
					locationFinePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
				}
				.setNegativeButton(getString(R.string.btn_cancel)) { dialog, which ->
					showErrorPermissionAborted()
				}


			val dialog: AlertDialog = builder.create()
			dialog.show()
		} else {
			check_and_showDialogAskBackgroundLocation()
		}

	}

	/**
	 * Ask for background location, or go to next step (register geofences).
	 */
	private fun check_and_showDialogAskBackgroundLocation() {

		if( ActivityCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.ACCESS_BACKGROUND_LOCATION
			) != PackageManager.PERMISSION_GRANTED
		) {
			val builder: AlertDialog.Builder = AlertDialog.Builder(context)
			builder
				.setMessage("We need background location to send you notifications when you are in a shop.")
				.setTitle("Grant access to background location.")
				.setPositiveButton(getString(R.string.btn_enable)) { dialog, which ->
					locationBackgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
				}
				.setNegativeButton(getString(R.string.btn_cancel)) { dialog, which ->
					showErrorPermissionAborted()
				}


			val dialog: AlertDialog = builder.create()
			dialog.show()
		} else {
			registerGeofences()
		}

	}

	/**
	 * TODO: check or remove this
	 */
	private fun check_and_showDialogAskNotifications() {
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
			ActivityCompat.checkSelfPermission(
				requireActivity(),
				Manifest.permission.POST_NOTIFICATIONS
			) != PackageManager.PERMISSION_GRANTED) {
			// POST_NOTIFICATIONS permission exists and is needed from api 33 TIRAMISU
			notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			return
		}


		val intent: Intent = Intent(requireActivity(), MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
			putExtra("caller", "notification")
		}
		val pendingIntent: PendingIntent = PendingIntent.getActivity(
			requireActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE
		)

		val builder = NotificationCompat.Builder(requireActivity(), Const.ID_CHANNEL_STOCK)
			.setSmallIcon(androidx.core.R.drawable.notification_bg)
			.setContentTitle("Remember that you will die!")
			.setContentText("Let me explain a number of reasons why this is the case, blah, blah, blah...")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
		builder.addAction(androidx.core.R.drawable.notification_action_background, "PRESS ME", pendingIntent)

		NotificationManagerCompat.from(requireActivity())
			.notify(MainActivity.NOTIFICATION_ID_STOCK, builder.build())
	}


	/* geolocation */

	/**
	 * Register geofences.
	 */
	private fun registerGeofences() {

		val geofencesList = viewModel.getGeofencesList()
		if(geofencesList == null) {
			showErrorMissingShops()
			return
		}

		val pendingIntent: PendingIntent by lazy {
			val intent = Intent(requireContext(), BroadcastReceiverGeofence::class.java)
			// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
			// addGeofences() and removeGeofences().
			PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
		}

		val geofencingRequest = GeofencingRequest.Builder().apply {
			setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
			addGeofences(geofencesList)
		}.build()

		if (ActivityCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.ACCESS_BACKGROUND_LOCATION
			) != PackageManager.PERMISSION_GRANTED
		) {
			showErrorPermissionBackground()
			return
		}

		geofencingClient.addGeofences(geofencingRequest, pendingIntent).run {
			addOnSuccessListener {
				/* IF THIS FAILS GO TO Settings | Security & location | Location | Mode and set "high accuracy" */
				showSuccessGeofencesAdded(geofencesList.size)
			}
			addOnFailureListener {
				showErrorAddingGeofences()
			}
		}

		/*
		geofencingClient.removeGeofences(pendingIntent).run {
			addOnSuccessListener {
				// Geofences removed
				// ...
				Log.d(TAG_GEOFENCES, "Geofences were removed")
				Log.d(TAG_GEOFENCES, "geofences: ${geofencingRequest.geofences}")
			}
			addOnFailureListener {
				// Failed to remove geofences
				// ...
				Log.e(TAG_GEOFENCES, "Failed to remove geofences")
				Log.d(TAG_GEOFENCES, "geofences: ${geofencingRequest.geofences}")
			}
		}
		 */
	}


	/* Permissions launchers */

	private val locationBackgroundPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted ->
		if (isGranted) {
			Log.d(TAG,"granted background location")
			registerGeofences()
		} else {
			Log.d(TAG,"not granted background location")
		}
	}

	private val locationCoarsePermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted ->
		if (isGranted) {
			Log.d(TAG,"granted coarse location")
			check_and_showDialogAskFineLocation()
		} else {
			Log.d(TAG,"not granted coarse location")
		}
	}

	private val locationFinePermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted ->
		if (isGranted) {
			Log.d(TAG,"granted fine location")
			check_and_showDialogAskBackgroundLocation()
		} else {
			Log.d(TAG,"not granted fine location")
		}
	}

	private val notificationsPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (isGranted) {
			Log.d(TAG,"granted notifications")
			check_and_showDialogAskNotifications()
		} else {
			Log.d(TAG,"not granted notifications")
		}
	}


	/* display */



	private fun showErrorAddingGeofences() {
		Log.e(TAG, "Failed to add geofences")
		Toast.makeText(context, "Aborted: Failed to add geofences", Toast.LENGTH_LONG)
			.show()
	}
	private fun showErrorMissingShops() {
		Log.d(TAG, "viewModel.shops is null")
		Toast.makeText(context, "ERROR: please retry later", Toast.LENGTH_LONG)
			.show()
	}
	private fun showErrorPermissionAborted() {
		Log.d(TAG, "Aborted: Permission not granted")
		Toast.makeText(context, "Aborted: Permission not granted", Toast.LENGTH_LONG)
			.show()
	}
	private fun showErrorPermissionBackground() {
		Log.e(TAG, "Missing background permission")
		Toast.makeText(context, "Aborted: Missing background permission", Toast.LENGTH_LONG)
			.show()
	}
	private fun showSuccessGeofencesAdded(addedNumber: Int) {
		Log.d(TAG, "Geofences added for $addedNumber shops")
		Toast.makeText(context, "Added geofences for $addedNumber shops!", Toast.LENGTH_SHORT)
			.show()
	}





	companion object {

		private const val TAG = "GEOFENCES"

		/**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentSettings()
    }
}