package com.alim.callerid.data.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alim.callerid.model.ModelContacts

@Dao
interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun blockContact(contact: ModelContacts)


    @Query("DELETE FROM contacts WHERE number = :number")
    fun unBlockContact(number: String)

    @Query("""
        WITH NormalizedInput AS (
            SELECT REPLACE(REPLACE(REPLACE(REPLACE(:number, '(', ''), ')', ''), '-', ''), ' ', '') AS normalizedNumber,
                   REPLACE(REPLACE(REPLACE(REPLACE('+1' || :number, '(', ''), ')', ''), '-', ''), ' ', '') AS normalizedNumberWithCode,
                   REPLACE(REPLACE(REPLACE(REPLACE(SUBSTR(:number, 2), '(', ''), ')', ''), '-', ''), ' ', '') AS normalizedNumberWithoutCode
        ),
        NormalizedContacts AS (
            SELECT *, 
                   REPLACE(REPLACE(REPLACE(REPLACE(number, '(', ''), ')', ''), '-', ''), ' ', '') AS normalizedContactNumber
            FROM contacts
        )
        SELECT * 
        FROM NormalizedContacts, NormalizedInput
        WHERE normalizedContactNumber = normalizedNumber
           OR normalizedContactNumber = normalizedNumberWithCode
           OR normalizedContactNumber = normalizedNumberWithoutCode
        ORDER BY name ASC
    """)
    fun getContactByQuery(number: String): List<ModelContacts>

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getBlockedContacts(): List<ModelContacts>

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getBlockedContactsLive(): LiveData<List<ModelContacts>>
}