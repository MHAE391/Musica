package com.m391.musica.ui.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.m391.musica.R
import com.m391.musica.databinding.FragmentPlayerBinding
import com.m391.musica.ui.shared_view_models.SongsViewModel

class PlayerFragment : Fragment() {
    private val binding: FragmentPlayerBinding by lazy {
        FragmentPlayerBinding.inflate(layoutInflater)
    }
    private val args: PlayerFragmentArgs by navArgs()
    private val songsViewModel: SongsViewModel by activityViewModels()
    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(args.songPosition, songsViewModel.deviceSongs)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.currentPlayingSong.observe(viewLifecycleOwner) {
            binding.text.text = it.toString()
        }
        return binding.root
    }
}