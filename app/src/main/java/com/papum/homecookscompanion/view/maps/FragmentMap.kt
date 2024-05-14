package com.papum.homecookscompanion.view.maps

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.papum.homecookscompanion.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.OneFingerZoomOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Default map view activity.
 *
 * @author Marc Kurtz
 * @author Manuel Stahl
 */
class FragmentMap : Fragment() {


	private lateinit var mapView: MapView

	private lateinit var overlayCompass: CompassOverlay
	private lateinit var overlayCopyright: CopyrightOverlay
	private lateinit var overlayLocation: MyLocationNewOverlay
	private lateinit var overlayOneFingerZoom: OneFingerZoomOverlay
	private lateinit var overlayRotationGesture: RotationGestureOverlay
	private lateinit var overlayScaleBar: ScaleBarOverlay



	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_map, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		/* setup mapView and mapController, and initial values */
		mapView = view.findViewById<MapView>(R.id.fragment_map_mapview).apply {
			setTileSource(TileSourceFactory.MAPNIK)

			//needed for pinch zooms
			setMultiTouchControls(true)

			//scales tiles to the current screen's DPI, helps with readability of labels
			isTilesScaledToDpi = true

			// don't wrap around (repeated map)
			setHorizontalMapRepetitionEnabled(false)
			setVerticalMapRepetitionEnabled(false)
		}

		// start at your location
		val startPoint =  GeoPoint(40.0, 20.0)
		Log.d("LOC",GpsMyLocationProvider(context).lastKnownLocation?.toString() ?: "null" )
			/*GpsMyLocationProvider(context).lastKnownLocation.let {
			GeoPoint(it.latitude, it.longitude)
		}*/

		mapView.controller.apply {
			setZoom(START_ZOOM)
			setCenter(startPoint)
		}

		val dm = requireContext().resources.displayMetrics

		/* overlays */

		//My Location
		//note you have handle the permissions yourself, the overlay did not do it for you
		overlayLocation = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
			enableMyLocation()
		}

		//Copyright overlay
		overlayCopyright = CopyrightOverlay(context)

		//On screen compass
		overlayCompass = CompassOverlay(
			context, InternalCompassOrientationProvider(context), mapView
		).apply {
			enableCompass()
		}

		//map scale
		overlayScaleBar = ScaleBarOverlay(mapView).apply {
			setCentred(true)
			setScaleBarOffset(dm.widthPixels / 2, 10)
		}

		//support for map rotation
		overlayRotationGesture = RotationGestureOverlay(mapView).apply {
			isEnabled = true
		}

		//support for one finger zoom
		overlayOneFingerZoom = OneFingerZoomOverlay()


		/*
		//the rest of this is restoring the last map location the user looked at
		requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).let { prefs ->
			val zoomLevel = prefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1f)
			mapView.controller.setZoom(zoomLevel.toDouble())
			val orientation = prefs.getFloat(PREFS_ORIENTATION, 0f)
			mapView.setMapOrientation(orientation, false)
			val latitudeString = prefs.getString(PREFS_LATITUDE_STRING, "1.0")
			val longitudeString = prefs.getString(PREFS_LONGITUDE_STRING, "1.0")
			val latitude = latitudeString!!.toDouble()
			val longitude = longitudeString!!.toDouble()
			mapView.setExpectedCenter(GeoPoint(latitude, longitude))
		}
		*/
		/*
		//Mini map
		mMinimapOverlay = MinimapOverlay(context, mapView.tileRequestCompleteHandler)
		mMinimapOverlay!!.width = dm.widthPixels / 5
		mMinimapOverlay!!.height = dm.heightPixels / 5
		mapView.overlays.add(this.mMinimapOverlay)
		*/


		mapView.overlays.addAll( listOf(
			overlayCompass,
			overlayCopyright,
			overlayLocation,
			overlayOneFingerZoom,
			overlayRotationGesture,
			overlayScaleBar,
		))
	}


	override fun onPause() {
		/*
		//save the current location
		requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).let { prefs ->
			val edit = prefs.edit()
			edit.putString(PREFS_TILE_SOURCE, mapView.tileProvider.tileSource.name())
			edit.putFloat(PREFS_ORIENTATION,mapView.mapOrientation)
			edit.putString(PREFS_LATITUDE_STRING, mapView.mapCenter.latitude.toString())
			edit.putString(PREFS_LONGITUDE_STRING, mapView.mapCenter.longitude.toString())
			edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, mapView.zoomLevelDouble.toFloat())
			edit.commit()
		}
		*/

		mapView.onPause()
		super.onPause()
	}

	override fun onResume() {
		super.onResume()

		/*
		//manage saved data
		val tileSourceName = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(
			PREFS_TILE_SOURCE,
			TileSourceFactory.DEFAULT_TILE_SOURCE.name()
		)
		try {
			val tileSource = TileSourceFactory.getTileSource(tileSourceName)
			mapView.setTileSource(tileSource)
		} catch (e: IllegalArgumentException) {
			mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
		}
		 */

		mapView.onResume()
	}



	companion object {
		// ===========================================================
		// Constants
		// ===========================================================
		private const val PREFS_NAME = "org.andnav.osm.prefs"
		private const val PREFS_TILE_SOURCE = "tilesource"
		private const val PREFS_LATITUDE_STRING = "latitudeString"
		private const val PREFS_LONGITUDE_STRING = "longitudeString"
		private const val PREFS_ORIENTATION = "orientation"
		private const val PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble"

		private const val MENU_ABOUT = Menu.FIRST + 1
		private const val MENU_LAST_ID = MENU_ABOUT + 1 // Always set to last unused id

		private const val START_ZOOM = 5.0

		fun newInstance(): FragmentMap {
			return FragmentMap()
		}
	}
}

