package com.papum.homecookscompanion.model.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class Converters {

	@TypeConverter
	fun fromTimestamp(value: Long?): LocalDateTime? {
		return value?.let {
			LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
		}
	}

	@TypeConverter
	fun dateToTimestamp(date: LocalDateTime?): Long? {
		return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
	}
}