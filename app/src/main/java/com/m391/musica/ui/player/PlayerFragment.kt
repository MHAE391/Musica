package com.m391.musica.ui.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        PlayerViewModelFactory(
            requireActivity().application,
            args.songPosition,
            songsViewModel.deviceSongs
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.next.setOnClickListener {
            viewModel.onNextPreviousButtonClicked(1, binding.playPause)
        }
        binding.previous.setOnClickListener {
            viewModel.onNextPreviousButtonClicked(-1, binding.playPause)
        }
        binding.playPause.setOnClickListener {
            if (it.tag == getString(R.string.play)) {
                viewModel.setOnPlayButtonClicked(binding.playPause)
            } else {
                viewModel.setOnPauseButtonClicked(binding.playPause)
            }
        }
        viewModel.setProgressListener(binding.durationSeekBar)
    }

}