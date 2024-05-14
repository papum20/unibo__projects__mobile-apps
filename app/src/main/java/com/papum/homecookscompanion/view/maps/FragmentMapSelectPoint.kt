package com.papum.homecookscompanion.view.maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.view.shops.FragmentDialogAddShop
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.OneFingerZoomOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


/**
 * Map view fragment which returns a location on click.
 *
 * @author Marc Kurtz
 * @author Manuel Stahl
 */
class FragmentMapSelectPoint : Fragment() {


	private lateinit var mapView: MapView
	private lateinit var eventsReceiver: MapEventsReceiver

	private lateinit var overlayCompass: CompassOverlay
	private lateinit var overlayCopyright: CopyrightOverlay
	private lateinit var overlayLocationMy: MyLocationNewOverlay
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
		return inflater.inflate(R.layout.fragment_map_shops, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		/* setup mapView and mapController, and initial values */
		mapView = view.findViewById<MapView>(R.id.fragment_map_shops_mapview).apply {
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
		overlayLocationMy = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
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


		/* list of items currently displayed */
		eventsReceiver = object : MapEventsReceiver {

			override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
				return false
			}

			override fun longPressHelper(p: GeoPoint): Boolean {

				FragmentDialogAddShop.newInstance(p.latitude, p.longitude)
					.show(parentFragmentManager, FragmentDialogAddShop.DIALOG_TAG)
				return true
			}
		}


		mapView.overlays.addAll( listOf(
			overlayCompass,
			overlayCopyright,
			overlayLocationMy,
			overlayOneFingerZoom,
			overlayRotationGesture,
			overlayScaleBar,
			MapEventsOverlay(eventsReceiver)
		))
	}


	override fun onPause() {
		mapView.onPause()
		super.onPause()
	}

	override fun onResume() {
		super.onResume()
		mapView.onResume()
	}



	companion object {

		private const val START_ZOOM = 5.0

		fun newInstance(): FragmentMapSelectPoint {
			return FragmentMapSelectPoint()
		}
	}
}

