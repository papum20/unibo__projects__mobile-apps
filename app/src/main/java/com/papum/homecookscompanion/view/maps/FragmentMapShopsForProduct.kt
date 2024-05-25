package com.papum.homecookscompanion.view.maps

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.slider.Slider
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.view.edit.food.FragmentEditFoodArgs
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem


/**
 * Map view fragment to display shops.
 *
 * @author Marc Kurtz
 * @author Manuel Stahl
 */
class FragmentMapShopsForProduct :
	FragmentMap(R.layout.fragment_map_shops_product, R.id.fragment_map_product_mapview)
{

	private val args: FragmentEditFoodArgs by navArgs()
	private val viewModel: MapShopsForProductViewModel by viewModels {
		MapShopsForProductViewModelFactory(
			Repository(requireActivity().application)
		)
	}

	private lateinit var overlayLocationPoints: ItemizedIconOverlay<OverlayItem>



	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)


		/* args */
		val productId: Long = args.foodId

		/* view */
		val tvProduct	= view.findViewById<TextView>(R.id.fragment_map_product_info_product)
		val tvPrice		= view.findViewById<TextView>(R.id.fragment_map_product_best_price)
		val tvRadius	= view.findViewById<TextView>(R.id.fragment_map_product_radius)
		val tvShop		= view.findViewById<TextView>(R.id.fragment_map_product_best_shop)
		view.findViewById<Slider>(R.id.fragment_map_product_slider).value =
			viewModel.getInitialSliderValue()

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


		/* observers */

		viewModel.getProductFromId(productId).observe(viewLifecycleOwner) { product ->
			tvProduct.text = product.toString()
		}

		// all shops in radius
		viewModel.getShopsForProduct_inRadius(productId).observe(viewLifecycleOwner) { shopsWithPurchases ->

			Log.d(TAG, "shopsWithPurchases: ${shopsWithPurchases.joinToString {
				"${it.shop.brand} ${it.purchases.joinToString { p -> "${p.idProduct} ${p.price}"}}"
			}}")

			if(shopsWithPurchases != null) {
				// add points
				overlayLocationPoints.removeAllItems()
				overlayLocationPoints.addItems(
					shopsWithPurchases.map { shopWithPurchase ->
						OverlayItem(
							shopWithPurchase.shop.brand, shopWithPurchase.shop.address,
							GeoPoint(shopWithPurchase.shop.latitude, shopWithPurchase.shop.longitude)
						) }
				)

				// add markers
				mapView.overlays.removeAll { it is Marker }
				shopsWithPurchases.forEach { shopWithPurchase ->
					shopWithPurchase.purchases.getOrNull(0)?.let { purchase ->
						// Create a new Marker at the clicked point
						val marker = Marker(mapView).apply {
							setPosition(
								GeoPoint(shopWithPurchase.shop.latitude, shopWithPurchase.shop.longitude)
							)
							title = "${shopWithPurchase.shop.brand} - ${shopWithPurchase.shop.address}"
							snippet = "${purchase.price} €"
						}
						mapView.overlays.add(marker)
					}
				}

			}

		}

		// radius
		viewModel.getShopsRadius().observe(viewLifecycleOwner) { radius ->
			tvRadius.text = getString(R.string.map_product_radius, radius)
		}

		// best price and shop
		viewModel.getShopWithBestPurchase(productId).observe(viewLifecycleOwner) { shopWithPurchases ->
			shopWithPurchases?.purchases?.getOrNull(0)?.let { bestPurchase ->
				tvPrice.text = getString(R.string.map_product_best_price,
					bestPurchase.price ?: NULL_PRICE)
				tvShop.text = getString(R.string.map_product_best_shop,
					shopWithPurchases.shop.let {"${it.brand}, ${it.address}"} )
			}
		}

		// notify my position to view model
		overlayLocationMy.runOnFirstFix {
			overlayLocationMy.myLocation?.let { location ->
				requireActivity().runOnUiThread {
					viewModel.setMyPosition(location)
				}
			}
		}


		/* UI Listeners */

		view.findViewById<Slider>(R.id.fragment_map_product_slider)
			.addOnChangeListener { slider, value, fromUser ->
				viewModel.setShopsRadius(value)
		}


	}




	companion object {

		private const val TAG = "MAP_SHOPS"

		private const val NULL_PRICE = 99999.99f

		fun newInstance(): FragmentMapShopsForProduct {
			return FragmentMapShopsForProduct()
		}
	}
}

