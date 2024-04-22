package com.papum.homecookscompanion.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ConvertersDates {


	companion object {
		@JvmStatic
		fun localDateTimeToDate(localeDate: LocalDateTime): Date {
			// local -> instant -> date
			return Date.from(localeDate.atZone(ZoneId.systemDefault()).toInstant())
		}

		@JvmStatic
		fun dateToLocalDateTime(date: Date): LocalDateTime {
			// date -> instant -> local
			return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
		}
	}

}