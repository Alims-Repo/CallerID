package com.alim.callerid.services

import android.content.Intent
import android.telecom.Call.Details
import android.telecom.CallScreeningService
import android.util.Log
import com.alim.callerid.di.Injector

class CallService : CallScreeningService() {

    override fun onScreenCall(callDetails: Details) {
        isBlocked(callDetails).let { isBlocked ->
            respondToCall(
                callDetails, CallResponse.Builder()
                    .setDisallowCall(isBlocked)
                    .setDisallowCall(isBlocked)
                    .setSkipCallLog(isBlocked)
                    .setSkipNotification(isBlocked).build()
            )

            if (!isBlocked)
                applicationContext.sendBroadcast(
                    Intent(applicationContext, IdentityBroadcast::class.java).apply {
                        action = "com.alim.callerid.SHOW_OVERLAY"
                        putExtra("phone_number", callDetails.handle?.schemeSpecificPart.toString())
                    }
                )
        }
    }

    private fun isBlocked(callDetails: Details): Boolean {
        Log.e("CallService", "onScreenCall: ${callDetails.handle?.schemeSpecificPart}")
        return Injector.repository.checkBlockedContact(
            callDetails.handle?.schemeSpecificPart.toString()
        )
    }
}