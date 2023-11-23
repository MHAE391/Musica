package com.m391.musica.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.m391.musica.R
import com.m391.musica.databinding.FragmentHomeBinding
import com.m391.musica.services.SongService
import com.m391.musica.ui.shared_view_models.SongsViewModel
import com.m391.musica.ui.shared_view_models.SongsViewModelFactory
import com.m391.musica.utils.setupLinearRecycler

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private val songsViewModel: SongsViewModel by activityViewModels {
        SongsViewModelFactory(requireActivity().application, SongService())
    }
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(songsViewModel.deviceSongs)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        val expandAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.expand_searchview)
        binding.searchView.setOnSearchClickListener {
            it.startAnimation(expandAnimation)
            binding.text.visibility = View.GONE
        }
        binding.searchView.setOnCloseListener {
            binding.text.visibility = View.VISIBLE
            false
        }
        setupRecycle()
    }

    private fun setupRecycle() {
        val adapter = SongAdapter {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToPlayerFragment(
                    it.position
                )
            )
        }
        binding.imagesRecyclerView.setupLinearRecycler(adapter)
    }

}