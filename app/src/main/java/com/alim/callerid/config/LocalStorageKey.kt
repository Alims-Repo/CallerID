package com.alim.callerid.config

object LocalStorageKey {

    val localAccountTypes = arrayOf(
        null,                          // Truly local contacts (NULL ACCOUNT_TYPE)
        "vnd.sec.contact.phone",       // Samsung
        "com.android.local",           // Generic local
        "local_contact_account",       // Some custom devices
        "com.huawei.local.contacts",   // Huawei
        "com.miui.local.contacts",     // Xiaomi
        "com.oppo.local.contacts",     // Oppo
        "com.vivo.local.contacts",     // Vivo
        "com.oneplus.local.contacts",  // OnePlus
        "com.realme.local.contacts",   // Realme
        "phone"                        // Generic fallback for many devices
    )
}