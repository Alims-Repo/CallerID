package com.alim.callerid.utils

object NumberProcessing {

    fun normalizeNumber(number: String, defaultCountryCode: String = "+880"): String {
        val cleanedNumber = number.replace(Regex("[^+\\d]"), "")
        return if (cleanedNumber.startsWith("+")) {
            cleanedNumber
        } else {
            defaultCountryCode + cleanedNumber
        }
    }
}