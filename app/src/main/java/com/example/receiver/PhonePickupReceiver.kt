package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Listens for system screen wake / unlock broadcasts (USER_PRESENT, SCREEN_ON)
 * to detect when the user picks up or unlocks their phone.
 * If an auto-adhkar session was triggered but left unread/unattended,
 * this receiver immediately surfaces an unread reminder notification at the top.
 */
class PhonePickupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(MandatoryAdhkarManager.TAG, "PhonePickupReceiver: Received action=$action")

        if (action == Intent.ACTION_USER_PRESENT || action == Intent.ACTION_SCREEN_ON) {
            try {
                MandatoryAdhkarManager.checkAndNotifyIfUnreadOnPickup(context)
            } catch (e: Exception) {
                Log.e(MandatoryAdhkarManager.TAG, "PhonePickupReceiver error: ${e.message}", e)
            }
        }
    }
}
