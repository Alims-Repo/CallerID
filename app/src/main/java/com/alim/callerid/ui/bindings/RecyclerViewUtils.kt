package com.alim.callerid.ui.bindings

import android.content.ContextWrapper
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.alim.callerid.model.ModelContacts
import com.alim.callerid.ui.adapter.ContactsAdapter

@BindingAdapter("contactList")
fun <T> setContactRecyclerViewProperties(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
    items?.let { listLiveData ->
        if (recyclerView.adapter == null) {
            recyclerView.adapter = ContactsAdapter()  // Set default adapter if not set
        }
        val adapter = recyclerView.adapter as? ContactsAdapter
        listLiveData.observe(recyclerView.findLifecycleOwner()!!, { list ->
            adapter?.submitList(list as? List<ModelContacts>)
        })
    }
}

fun RecyclerView.findLifecycleOwner(): LifecycleOwner? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is LifecycleOwner) {
            return context
        }
        context = context.baseContext
    }
    return null
}