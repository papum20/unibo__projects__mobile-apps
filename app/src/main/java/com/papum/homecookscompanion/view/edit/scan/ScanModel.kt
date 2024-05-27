package com.papum.homecookscompanion.view.edit.scan

import androidx.room.ColumnInfo

data class ScanModel(
	val recognizedProduct:	String,
	val recognizedPrice:	Float,
	var product:		String?,	// displayed product name
	var price:			Float?,
	var quantity:		Float?,

	val pieces:			Int?	= null,
	val price_kg:		Float?	= null,
	val price_piece:	Float?	= null,
	val weight_kg:		Float?	= null,

	var productId:		Long?	= null
) {

	override fun toString(): String {
		return "r: $recognizedProduct; p: $product; ${quantity} g, ${price} e"
	}
}

