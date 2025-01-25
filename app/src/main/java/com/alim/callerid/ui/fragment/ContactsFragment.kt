package com.alim.callerid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alim.callerid.R
import com.alim.callerid.databinding.FragmentContactsBinding
import com.alim.callerid.ui.viewmodel.ContactsViewModel

class ContactsFragment : Fragment() {

    private val viewModel by activityViewModels<ContactsViewModel>()

    private lateinit var binding: FragmentContactsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactsBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupToolbar()

        binding.search.doAfterTextChanged {
            binding.viewModel?.searchContacts(it.toString())
        }

        return binding.root
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar
        toolbar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.filter_all -> {
                viewModel.loadContacts()
                true
            }
            R.id.filter_google -> {
                viewModel.loadContacts("google")
                true
            }
            R.id.filter_device -> {
                viewModel.loadContacts("device")
                true
            }
            R.id.filter_sim -> {
                viewModel.loadContacts("sim")
                true
            }
            else -> false
        }
    }
}