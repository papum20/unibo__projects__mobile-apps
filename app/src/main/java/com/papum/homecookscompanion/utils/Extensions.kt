package com.papum.homecookscompanion.utils

class Extensions {

	companion object {

		fun Float.round(decimals: Int): Float {
			var multiplier = 1.0f
			repeat(decimals) { multiplier *= 10 }
			return kotlin.math.round(this * multiplier) / multiplier
		}

	}

}