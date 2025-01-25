package com.alim.callerid.data.repository.implementations

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import com.alim.callerid.config.Customize.HIDE_DUPLICATE_CONTACT
import com.alim.callerid.config.LocalStorageKey.localAccountTypes
import com.alim.callerid.data.repository.interfaces.IContactsRepository
import com.alim.callerid.data.source.local.dao.ContactsDao
import com.alim.callerid.model.ModelContacts
import com.alim.callerid.utils.NumberProcessing.normalizeNumber

class ContactsRepository(
    private val context: Context,
    private val contactsDao: ContactsDao
) : IContactsRepository {

    override fun getAllDeviceContacts(filter: String?, email: String?): List<ModelContacts> {
        val blockedSet = contactsDao.getBlockedContacts().map { it.number }.toSet()
        val contactsList = mutableListOf<ModelContacts>()

        val (selection, selectionArgs) = if (email == null) {
            when (filter) {
                "google" -> "${ContactsContract.RawContacts.ACCOUNT_TYPE} = ?"
                "device" -> "${ContactsContract.RawContacts.ACCOUNT_TYPE} = ?"
                "sim" -> "${ContactsContract.RawContacts.DATA_SET} = ?"
                else -> null
            } to when (filter) {
                "google" -> arrayOf("com.google")
                "device" -> localAccountTypes
                "sim" -> arrayOf("sim")
                else -> null
            }
        } else {
            "${ContactsContract.RawContacts.ACCOUNT_NAME} = ? AND " +
                    "${ContactsContract.RawContacts.ACCOUNT_TYPE} = ?" to
                    arrayOf(email, "com.google")
        }

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            ), selection, selectionArgs,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoUriIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            val list = ArrayList<String>()
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)
                val photoUri = cursor.getString(photoUriIndex)

                contactsList.add(
                    ModelContacts(
                        0, name, number, photoUri ?: ""
                    ).also {
                        it.blocked = blockedSet.contains(number)
                    }
                )
            }
        }

        return if (HIDE_DUPLICATE_CONTACT) {
            contactsList.distinctBy { contact ->
                normalizeNumber(contact.number)
            }
        } else {
            contactsList
        }
    }

    override fun checkBlockedContact(number: String): Boolean {
        return contactsDao.getContactByQuery(number).isNotEmpty()
    }

    override fun blockContact(contact: ModelContacts) {
        contactsDao.blockContact(contact)
    }

    override fun unBlockContact(contact: ModelContacts) {
        contactsDao.unBlockContact(contact.number)
    }

    override fun getBlockedContacts(): LiveData<List<ModelContacts>> {
        return contactsDao.getBlockedContactsLive()
    }
}