package com.papum.homecookscompanion.view.edit.scan

data class ScanModel(
	val recognizedProduct: String,
	val recognizedPrice: String,
	var product: String?,
	var price: String?
)

