package com.alim.callerid.config

import android.telephony.PhoneNumberUtils.normalizeNumber

object Dummey {

    val predefinedIdentities = mapOf<String, String>(
        normalizeNumber("(650) 555-1212") to "Emulated Contact",
        normalizeNumber("+8801881632597") to "Alim Bhai",
    )
}