package com.papum.homecookscompanion.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadcastReceiverBoot : BroadcastReceiver() {

	override fun onReceive(context: Context?, intent: Intent?) {
		if (intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			val serviceIntent = Intent(context, ServiceNotificationStock::class.java)
			context!!.startForegroundService(serviceIntent)
		}
	}

}