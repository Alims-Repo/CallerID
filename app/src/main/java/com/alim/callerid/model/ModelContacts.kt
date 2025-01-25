package com.alim.callerid.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ModelContacts(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val number: String,
    val image: String
) {
    @Ignore
    var blocked: Boolean = true
}