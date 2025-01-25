package com.alim.callerid.data.repository.interfaces

import androidx.lifecycle.LiveData
import com.alim.callerid.model.ModelContacts

interface IContactsRepository {

    fun getAllDeviceContacts(filter: String? = null, email: String? = null): List<ModelContacts>

    fun checkBlockedContact(number: String): Boolean

    fun blockContact(contact: ModelContacts)

    fun unBlockContact(contact: ModelContacts)

    fun getBlockedContacts() : LiveData<List<ModelContacts>>
}