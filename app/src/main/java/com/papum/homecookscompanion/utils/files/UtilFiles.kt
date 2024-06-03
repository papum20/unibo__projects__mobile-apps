package com.papum.homecookscompanion.utils.files

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.jvm.Throws

object UtilFiles {

	@Throws(IOException::class)
	fun readTextFromUri(resolver: ContentResolver, uri: Uri): String {

		val stringBuilder = StringBuilder()

		resolver.openInputStream(uri)?.use { stream ->
			val reader = BufferedReader(InputStreamReader(stream))

			var line = reader.readLine()
			while (line != null) {
				stringBuilder.append(line).append('\n')
				line = reader.readLine()
			}
		}

		return stringBuilder.toString()
	}

}