package com.prashant.task.fragments.table

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.prashant.task.databinding.TableFragmentBinding

class Table :Fragment() {
    private var _binding: TableFragmentBinding? = null
    private val binding get() = _binding!!
    private val tableVM by viewModels<TableVM>()
    private val args by navArgs<TableArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = TableFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = tableVM

        Log.e(TAG, "onCreateView: ${args.inputData}")
        return binding.root
    }

    companion object {
        private const val TAG = "Table"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}