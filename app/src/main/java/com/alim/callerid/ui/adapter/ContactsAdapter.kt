package com.alim.callerid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alim.callerid.databinding.ItemContactBinding
import com.alim.callerid.di.Injector
import com.alim.callerid.model.ModelContacts

class ContactsAdapter() : ListAdapter<ModelContacts,
        ContactsAdapter.ContactViewHolder>(DiffCallback) {

    inner class ContactViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(modelIdentified: ModelContacts) {
            binding.contact = modelIdentified.also {
                it.blocked = Injector.repository.checkBlockedContact(it.number)
            }
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                val isBlocked = Injector.repository.checkBlockedContact(modelIdentified.number)
                showConfirmationDialog(
                    isBlocked, modelIdentified.name,
                    onConfirm = {
                        if (isBlocked) {
                            Injector.repository.unBlockContact(modelIdentified)
                        } else {
                            Injector.repository.blockContact(modelIdentified)
                        }
                        notifyItemChanged(adapterPosition)
                    }
                )
            }
        }

        private fun showConfirmationDialog(isBlocked: Boolean, name: String, onConfirm: () -> Unit) {
            val action = if (isBlocked) "unblock" else "block"
            val message = "Are you sure you want to $action $name?"
            val title = "${action.replaceFirstChar { it.uppercaseChar() }} Contact"

            AlertDialog.Builder(binding.root.context).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("Yes") { _, _ ->
                    onConfirm()
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                create().show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ModelContacts>() {

        override fun areItemsTheSame(
            oldItem: ModelContacts, newItem: ModelContacts
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ModelContacts, newItem: ModelContacts
        ) = oldItem == newItem
    }
}