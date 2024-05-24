package com.papum.homecookscompanion.utils

import com.papum.homecookscompanion.utils.Extensions.Companion.round

class Formatters {

	companion object {

		private const val QUANTITY_DIGITS_DECIMAL = 2


		fun formatQuantity(quantity: Float) : Float {
			return quantity.round(QUANTITY_DIGITS_DECIMAL)
		}

	}

}