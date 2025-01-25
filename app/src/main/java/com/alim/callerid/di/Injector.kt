package com.alim.callerid.di

import android.app.Application
import android.content.Context
import com.alim.callerid.data.repository.implementations.ContactsRepository
import com.alim.callerid.data.repository.interfaces.IContactsRepository
import com.alim.callerid.data.source.local.database.AppDatabase

object Injector : Base {

    private lateinit var appContext: Context

    override val repository: IContactsRepository by lazy {
        ContactsRepository(appContext, AppDatabase.getInstance(appContext).contactsDao())
    }

    fun init(context: Application) {
        appContext = context.applicationContext
    }
}