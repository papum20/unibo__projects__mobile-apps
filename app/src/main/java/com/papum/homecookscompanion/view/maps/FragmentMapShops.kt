package com.papum.homecookscompanion.view.maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.OneFingerZoomOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


/**
 * Map view fragment to display shops.
 *
 * @author Marc Kurtz
 * @author Manuel Stahl
 */
class FragmentMapShops :
	FragmentMap(R.layout.fragment_map_shops, R.id.fragment_map_shops_mapview)
{

	private val viewModel: MapShopsViewModel by viewModels {
		MapShopsViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	private lateinit var overlayLocationPoints: ItemizedIconOverlay<OverlayItem>



	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)


		/* Points for shops on the map, which pop up a marker on click */
		overlayLocationPoints = ItemizedIconOverlay(
			requireContext(),
			mutableListOf<OverlayItem>(),
			object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem?> {

				override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
					return true
				}
				override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
					return false
				}

			}
		)


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
			overlayLocationPoints
		))


		viewModel.getAllShops().observe(viewLifecycleOwner) { shops ->

			if(shops != null) {
				// add points
				overlayLocationPoints.removeAllItems()
				overlayLocationPoints.addItems(
					shops.map { shop ->
						OverlayItem(
							shop.brand, shop.address,
							GeoPoint(shop.latitude, shop.longitude)
						) }
				)

				// add markers
				shops.forEach { shop ->
					// Create a new Marker at the clicked point
					val marker = Marker(mapView).apply {
						setPosition(GeoPoint(shop.latitude, shop.longitude))
						title = "${shop.brand} - ${shop.address}"
					}

					mapView.overlays.add(marker)
				}
			}
		}


	}




	companion object {

		private const val TAG = "MAP_SHOPS"

		fun newInstance(): FragmentMapShops {
			return FragmentMapShops()
		}
	}
}

