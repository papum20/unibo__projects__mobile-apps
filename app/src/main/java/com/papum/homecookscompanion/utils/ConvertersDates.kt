package com.papum.homecookscompanion.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ConvertersDates {


	companion object {
		@JvmStatic
		fun localDateTimeToDate(localeDate: LocalDateTime): Date {
			val instant = localeDate.atZone(ZoneId.systemDefault()).toInstant()

			return Date.from(instant)
		}
	}

}