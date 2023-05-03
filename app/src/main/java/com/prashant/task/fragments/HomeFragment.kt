package com.prashant.task.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.prashant.task.databinding.HomeFragmentBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            cardImages.setOnClickListener {
                view.findNavController()
                    .navigate(HomeFragmentDirections.actionFirstFragmentToImage())
            }
            cardVideo.setOnClickListener {
                view.findNavController()
                    .navigate(HomeFragmentDirections.actionFirstFragmentToVideoFragment())
            }
            cardInput.setOnClickListener {
                view.findNavController()
                    .navigate(HomeFragmentDirections.actionFirstFragmentToUserInput())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}