package com.m391.musica.ui.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.m391.musica.R
import com.m391.musica.database.AppDatabase
import com.m391.musica.database.MusicDAO
import com.m391.musica.databinding.FragmentFavouriteBinding
import com.m391.musica.ui.home.SongAdapter
import com.m391.musica.ui.shared_view_models.SongsViewModel
import com.m391.musica.utils.setupLinearRecycler
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {
    private val binding: FragmentFavouriteBinding by lazy {
        FragmentFavouriteBinding.inflate(layoutInflater)
    }
    private val songsViewModel: SongsViewModel by activityViewModels()
    private val viewModel: FavouriteViewModel by viewModels {
        FavouriteViewModelFactory(requireActivity().application, songsViewModel.favouriteSongs)
    }
    
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.refreshSongs(viewLifecycleOwner)
        }
    }

    override fun onStart() {
        super.onStart()
        setupRecycle()
        lifecycleScope.launch {
            songsViewModel.refreshFavouriteSongs()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun setupRecycle() {
        val adapter = SongAdapter {
            findNavController().navigate(
                FavouriteFragmentDirections.actionFavouriteFragmentToPlayerFragment(
                    it.position,
                    getString(R.string.favourite)
                )
            )
        }
        binding.imagesRecyclerView.setupLinearRecycler(adapter)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopRefresh(viewLifecycleOwner)
    }
}