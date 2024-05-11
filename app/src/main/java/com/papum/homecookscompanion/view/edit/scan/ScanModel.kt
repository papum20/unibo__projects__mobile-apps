package com.papum.homecookscompanion.view.edit.scan

data class ScanModel(
	val recognizedProduct:	String,
	val recognizedPrice:	Float,
	var product:		String?,
	var price:			Float?,

	val pieces:			Int?,
	val price_kg:		Float?,
	val price_piece:	Float?,
	val weight_kg:		Float?
)

