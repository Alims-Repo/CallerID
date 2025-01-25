package com.alim.callerid.di

import com.alim.callerid.data.repository.interfaces.IContactsRepository

interface Base {

    val repository: IContactsRepository
}