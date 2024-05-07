package com.papum.homecookscompanion.view.maps

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.InputDevice
import android.view.LayoutInflater
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.View.OnGenericMotionListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.papum.homecookscompanion.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MinimapOverlay
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
	// ===========================================================
	// Fields
	// ===========================================================
	private var mMapView: MapView? = null
	private var mLocationOverlay: MyLocationNewOverlay? = null
	private var mCompassOverlay: CompassOverlay? = null
	private var mMinimapOverlay: MinimapOverlay? = null
	private var mScaleBarOverlay: ScaleBarOverlay? = null
	private var mRotationGestureOverlay: RotationGestureOverlay? = null
	private var mCopyrightOverlay: CopyrightOverlay? = null
	private var mOneFingerZoomOverlay: OneFingerZoomOverlay? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		//Note! we are programmatically construction the map view
		//be sure to handle application lifecycle correct (see note in on pause)

		mMapView = MapView(inflater.context)
		mMapView!!.setDestroyMode(false)
		mMapView!!.tag = "mapView" // needed for OpenStreetMapViewTest

		mMapView!!.setOnGenericMotionListener(OnGenericMotionListener { v, event ->

			/**
			 * mouse wheel zooming ftw
			 * http://stackoverflow.com/questions/11024809/how-can-my-view-respond-to-a-mousewheel
			 * @param v
			 * @param event
			 * @return
			 */
			/**
			 * mouse wheel zooming ftw
			 * http://stackoverflow.com/questions/11024809/how-can-my-view-respond-to-a-mousewheel
			 * @param v
			 * @param event
			 * @return
			 */
			if (0 != (event.source and InputDevice.SOURCE_CLASS_POINTER)) {
				when (event.action) {
					MotionEvent.ACTION_SCROLL -> {
						if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f) mMapView!!.controller.zoomOut()
						else {
							//this part just centers the map on the current mouse location before the zoom action occurs
							val iGeoPoint =
								mMapView!!.projection.fromPixels(event.x.toInt(), event.y.toInt())
							mMapView!!.controller.animateTo(iGeoPoint)
							mMapView!!.controller.zoomIn()
						}
						return@OnGenericMotionListener true
					}
				}
			}
			false
		})
		return mMapView
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		val context: Context? = this.activity
		val dm = context!!.resources.displayMetrics

		//My Location
		//note you have handle the permissions yourself, the overlay did not do it for you
		mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mMapView)
		mLocationOverlay!!.enableMyLocation()
		mMapView!!.overlays.add(this.mLocationOverlay)


		//Mini map
		mMinimapOverlay = MinimapOverlay(context, mMapView!!.tileRequestCompleteHandler)
		mMinimapOverlay!!.width = dm.widthPixels / 5
		mMinimapOverlay!!.height = dm.heightPixels / 5
		mMapView!!.overlays.add(this.mMinimapOverlay)


		//Copyright overlay
		mCopyrightOverlay = CopyrightOverlay(context)
		//i hate this very much, but it seems as if certain versions of android and/or
		//device types handle screen offsets differently
		mMapView!!.overlays.add(this.mCopyrightOverlay)


		//On screen compass
		mCompassOverlay = CompassOverlay(
			context, InternalCompassOrientationProvider(context),
			mMapView
		)
		mCompassOverlay!!.enableCompass()
		mMapView!!.overlays.add(this.mCompassOverlay)


		//map scale
		mScaleBarOverlay = ScaleBarOverlay(mMapView)
		mScaleBarOverlay!!.setCentred(true)
		mScaleBarOverlay!!.setScaleBarOffset(dm.widthPixels / 2, 10)
		mMapView!!.overlays.add(this.mScaleBarOverlay)


		//support for map rotation
		mRotationGestureOverlay = RotationGestureOverlay(mMapView)
		mRotationGestureOverlay!!.isEnabled = true
		mMapView!!.overlays.add(this.mRotationGestureOverlay)

		//support for one finger zoom
		mOneFingerZoomOverlay = OneFingerZoomOverlay()
		mMapView!!.overlays.add(this.mOneFingerZoomOverlay)

		//needed for pinch zooms
		mMapView!!.setMultiTouchControls(true)

		//scales tiles to the current screen's DPI, helps with readability of labels
		mMapView!!.isTilesScaledToDpi = true

		//the rest of this is restoring the last map location the user looked at
		context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).let { prefs ->
			val zoomLevel = prefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1f)
			mMapView!!.controller.setZoom(zoomLevel.toDouble())
			val orientation = prefs.getFloat(PREFS_ORIENTATION, 0f)
			mMapView!!.setMapOrientation(orientation, false)
			val latitudeString = prefs.getString(PREFS_LATITUDE_STRING, "1.0")
			val longitudeString = prefs.getString(PREFS_LONGITUDE_STRING, "1.0")
			val latitude = latitudeString!!.toDouble()
			val longitude = longitudeString!!.toDouble()
			mMapView!!.setExpectedCenter(GeoPoint(latitude, longitude))
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
/*
		val map = view.findViewById<View>(R.id.fragment_map_mapview)
		map.setTileSource(TileSourceFactory.MAPNIK)

		val mapController: `val` = map.controller
		mapController.setZoom(9.5)
		val startPoint: `val` = GeoPoint(48.8583, 2.2944)
		mapController.setCenter(startPoint)
*/
	}

	override fun onPause() {
		//save the current location
		requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).let { prefs ->
			val edit = prefs.edit()
			edit.putString(PREFS_TILE_SOURCE, mMapView!!.tileProvider.tileSource.name())
			edit.putFloat(PREFS_ORIENTATION, mMapView!!.mapOrientation)
			edit.putString(PREFS_LATITUDE_STRING, mMapView!!.mapCenter.latitude.toString())
			edit.putString(PREFS_LONGITUDE_STRING, mMapView!!.mapCenter.longitude.toString())
			edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, mMapView!!.zoomLevelDouble.toFloat())
			edit.commit()
		}

		mMapView!!.onPause()
		super.onPause()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		//this part terminates all of the overlays and background threads for osmdroid
		//only needed when you programmatically create the map
		mMapView!!.onDetach()
	}

	override fun onResume() {
		super.onResume()
		val tileSourceName = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(
			PREFS_TILE_SOURCE,
			TileSourceFactory.DEFAULT_TILE_SOURCE.name()
		)
		try {
			val tileSource = TileSourceFactory.getTileSource(tileSourceName)
			mMapView!!.setTileSource(tileSource)
		} catch (e: IllegalArgumentException) {
			mMapView!!.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
		}

		mMapView!!.onResume()
	}


	fun zoomIn() {
		mMapView!!.controller.zoomIn()
	}

	fun zoomOut() {
		mMapView!!.controller.zoomOut()
	}

	// @Override
	// public boolean onTrackballEvent(final MotionEvent event) {
	// return this.mMapView.onTrackballEvent(event);
	// }
	fun invalidateMapView() {
		mMapView!!.invalidate()
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

		fun newInstance(): FragmentMap {
			return FragmentMap()
		}
	}
}

