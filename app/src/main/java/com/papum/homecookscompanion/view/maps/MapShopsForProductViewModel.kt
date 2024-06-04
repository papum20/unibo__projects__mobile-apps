package com.papum.homecookscompanion.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityShopsWithPurchases
import org.osmdroid.util.GeoPoint
import kotlin.jvm.Throws
import kotlin.math.roundToInt

class MapShopsForProductViewModel(private val repository: Repository) : ViewModel() {

	private var myPosition: MutableLiveData<GeoPoint?> = MutableLiveData(null)
	private var shopsRadius: MutableLiveData<Float> = MutableLiveData(RADIUS_DFLT)


	/* Query */

	fun getProductFromId(productId: Long): LiveData<EntityProduct?> =
		repository.getProduct(productId)

	/**
	 * @return a (liveData of a) list of shops, with the purchase, for the given product,
	 * with the lowest price (for that shop), all filtered in the selected radius
	 */
	fun getShopsForProduct_inRadius(productId: Long): LiveData<List<EntityShopsWithPurchases>> =
		repository.getPurchases_forProduct(productId)
			.switchMap { shopsWithPurchases ->
				Log.d("VIEWMODEL", "shopswithpurchases switchmap ${shopsRadius.value}")
				getShopsRadius().switchMap { radius ->
					Log.d("VIEWMODEL", "radius switchmap ${shopsRadius.value}")
					myPosition.switchMap { myPosition ->
						MutableLiveData(
							shopsWithPurchases.map { shopWithPurchases ->
								EntityShopsWithPurchases(
									shopWithPurchases.shop,
									// filter shops in radius from current position (or all if not set)
									shopWithPurchases.purchases.filter {
										myPosition?.let { position ->
											val positionShop = GeoPoint(
												shopWithPurchases.shop.latitude,
												shopWithPurchases.shop.longitude
											)
											Log.d("VIEWMODEL", "filter ${position.distanceToAsDouble(positionShop) }")
											position.distanceToAsDouble(positionShop) <= 1000 * radius
										} ?: true
									}.reduceOrNull { acc, purchase ->
										Log.d("VIEWMODEL", "reduce ${acc.price} ${purchase.price}")
										// get the lowest price for each shop
										if (purchase.price == null)
											acc
										else if (acc.price == null || purchase.price!! < acc.price!!)
											purchase
										else
											acc
									}?.let {
										bestPurchase -> listOf(bestPurchase)
									} ?: emptyList()
								)
							}.filter { shopWithPurchases ->
								// don't return shops without related purchases
								shopWithPurchases.purchases.isNotEmpty()

							}
						)
			} } }

	/**
	 * @return (a liveData of) the best purchase for the given product, with the lowest price
	 */
	fun getShopWithBestPurchase(productId: Long): LiveData<EntityShopsWithPurchases?> =
		getShopsForProduct_inRadius(productId)
			.switchMap { shopsWithPurchases ->
				Log.d("VIEWMODEL", "best switchmap ${shopsRadius.value}")
				MutableLiveData(
					shopsWithPurchases.reduceOrNull { acc, shopWithPurchases ->
						if( acc.purchases.isEmpty() || acc.purchases[0].price == null )
							shopWithPurchases
						else if(shopWithPurchases.purchases.isEmpty()
							|| shopWithPurchases.purchases[0].price == null
							|| acc.purchases[0].price!! < shopWithPurchases.purchases[0].price!!
						)
							acc
						else
							shopWithPurchases
					}
				)
			}


	/* Getters */

	fun getShopsRadius(): MutableLiveData<Float> = shopsRadius
	fun getInitialSliderValue(): Float = INITIAL_RADIUS_VAL.toFloat()


	/* Setters */

	fun setMyPosition(position: GeoPoint) {
		myPosition.value = position
	}

	fun setShopsRadius(sliderValue: Float) {
		shopsRadius.value = DISTANCE[ (sliderValue / 10).roundToInt()]
	}


	companion object {

		// DISTANCE[sliderVal], for sliderVal from 0 to 10
		private val DISTANCE = arrayOf(
			1.0f,
			1.5f,
			2.0f,
			2.5f,
			3.5f,
			5.0f,
			7.5f,
			10.0f,
			20.0f,
			100.0f,
			50000.0f	// more than circumference or any other non-repeating distance on earth
		)

		private const val INITIAL_RADIUS_VAL = 50
		private val RADIUS_DFLT	= DISTANCE[INITIAL_RADIUS_VAL / 10]

		private const val SLIDER_MIN = 0f
		private const val SLIDER_MAX = 100f
		private const val SLIDER_STEP = 10f


	}

}


/**
 * Factory for constructing ProductResultViewModel, scoping it to the provided activity.
 */
class MapShopsForProductViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(MapShopsForProductViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return MapShopsForProductViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}