package com.alim.callerid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alim.callerid.databinding.FragmentBlockNumbersBinding
import com.alim.callerid.ui.viewmodel.ContactsViewModel
import kotlin.getValue

class BlockNumbersFragment : Fragment() {

    private val viewModel by activityViewModels<ContactsViewModel>()

    private lateinit var binding: FragmentBlockNumbersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlockNumbersBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.search.doAfterTextChanged {
            viewModel.searchBlockedContacts(it.toString())
        }

        return binding.root
    }
}