package com.papum.homecookscompanion.view.maps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.view.shops.FragmentDialogAddShop
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem


/**
 * Map view fragment which returns a location on click.
 *
 * @author Marc Kurtz
 * @author Manuel Stahl
 */
class FragmentMapShopsSelectPoint :
	FragmentMap(R.layout.fragment_map_select_point, R.id.fragment_map_point_mapview)
{

	private val viewModel: MapShopsSelectPointViewModel by viewModels {
		MapShopsSelectPointViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	private lateinit var overlayLocationPoints: ItemizedIconOverlay<OverlayItem>
	private lateinit var eventsReceiver: MapEventsReceiver


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


		/* press events */
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
			overlayLocationPoints,
			MapEventsOverlay(eventsReceiver)
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

		private const val TAG = "MAP_POINT"

		fun newInstance(): FragmentMapShopsSelectPoint {
			return FragmentMapShopsSelectPoint()
		}
	}
}

