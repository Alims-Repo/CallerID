package com.alim.callerid.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alim.callerid.config.Customize.SEARCH_DEBOUNCE_DELAY
import com.alim.callerid.data.repository.interfaces.IContactsRepository
import com.alim.callerid.di.Injector
import com.alim.callerid.model.ModelContacts
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: IContactsRepository = Injector.repository
) : ViewModel() {

    private var searchJob: Job? = null

    val contactMessage = MutableLiveData<String>("")
    val blockedMessage = MutableLiveData<String>("")

    private val _contacts = MutableLiveData<List<ModelContacts>>(emptyList())
    private val _blockedContacts = MediatorLiveData<List<ModelContacts>>().apply { value = emptyList() }

    private val _searchQueryContacts = MutableLiveData<String>("")
    private val _searchQueryBlocked = MutableLiveData<String>("")

    val filteredContacts: LiveData<List<ModelContacts>> = _searchQueryContacts.switchMap { query ->
        _contacts.map { list ->
            val filteredList = if (query.isEmpty()) list else list.filter {
                it.name.contains(query, ignoreCase = true) || it.number.contains(query)
            }
            contactMessage.postValue(
                if (_contacts.value.isNullOrEmpty())
                    "No contacts to display. Add contacts to your list, and they’ll appear here."
                else if (filteredList.isNullOrEmpty()) "No matching contacts found. Try searching for a different contact."
                else "Total ${filteredList.size} contacts found"
            )
            filteredList
        }
    }

    val filterBlocked: LiveData<List<ModelContacts>> = _searchQueryBlocked.switchMap { query ->
        _blockedContacts.map { list ->
            val filteredList = if (query.isEmpty()) list else list.filter {
                it.name.contains(query, ignoreCase = true) || it.number.contains(query)
            }
            blockedMessage.postValue(
                if (_blockedContacts.value.isNullOrEmpty())
                    "Blocked contacts will appear here. To add, simply block a contact from your contact list."
                else if (filteredList.isNullOrEmpty()) "No matching contacts found. Try searching for a different contact."
                else "Total ${filteredList.size} contacts found"
            )

            filteredList
        }
    }

    fun loadContacts(filter: String? = null, email: String? = null) {
        viewModelScope.launch {
            try {
                repository.getAllDeviceContacts(filter, email).let {
                    _contacts.value = it
                    if (it.isEmpty())
                        contactMessage.postValue("No contacts to display. Add contacts to your list, and they’ll appear here.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadBlockedContacts() {
        _blockedContacts.addSource(repository.getBlockedContacts()) {
            _blockedContacts.value = it
            if (it.isEmpty()) {
                blockedMessage.postValue("Blocked contacts will appear here. To add, simply block a contact from your contact list.")
            }
        }
    }

    fun loadAll() = viewModelScope.launch {
        loadContacts()
        loadBlockedContacts()
    }

    fun searchContacts(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            if (_searchQueryContacts.value != query)
                _searchQueryContacts.postValue(query)
        }
    }

    fun searchBlockedContacts(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            if (_searchQueryBlocked.value != query)
                _searchQueryBlocked.postValue(query)
        }
    }
}