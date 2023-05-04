package com.prashant.task.fragments.inputs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.prashant.task.databinding.UserInputFragmentBinding


class UserInput : Fragment() {

    private var _binding: UserInputFragmentBinding? = null
    private val binding get() = _binding!!
    private val userInputVM by viewModels<UserInputVM>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = UserInputFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = userInputVM
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}