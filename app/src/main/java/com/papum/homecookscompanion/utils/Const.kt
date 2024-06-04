package com.papum.homecookscompanion.utils

object Const {

	const val ID_PRODUCT_NULL = -1L

	const val DFLT_MEASURE			= "g"
	const val MEASURE_QUANTITY		= 100.0F	// grams
	const val DFLT_NUTRIENT_VALUE	= 0.0F

	const val DFLT_NUTRIENT_VALUE_DISPLAYED = ""

	/* Notifications */
	const val ID_CHANNEL_STOCK		= "HomecooksCompanion_stock"
	const val ID_CHANNEL_GEOFENCES	= "HomecooksCompanion_geofences"



	fun getQuantityString(quantity: Float): String =
		"${quantity}${DFLT_MEASURE}"

}