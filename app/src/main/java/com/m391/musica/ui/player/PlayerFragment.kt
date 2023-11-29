package com.m391.musica.ui.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.m391.musica.R
import com.m391.musica.databinding.FragmentPlayerBinding
import com.m391.musica.ui.shared_view_models.SongsViewModel
import com.m391.musica.utils.toDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerFragment : Fragment() {
    private val binding: FragmentPlayerBinding by lazy {
        FragmentPlayerBinding.inflate(layoutInflater)
    }
    private val args: PlayerFragmentArgs by navArgs()
    private val songsViewModel: SongsViewModel by activityViewModels()

    private val viewModel: PlayerViewModel by viewModels {
        if (args.songDestination == getString(R.string.home))
            PlayerViewModelFactory(
                requireActivity().application,
                args.songPosition,
                songsViewModel.deviceSongs,
                songsViewModel.checkFavourite
            )
        else PlayerViewModelFactory(
            requireActivity().application,
            args.songPosition,
            songsViewModel.favouriteSongs,
            songsViewModel.checkFavourite
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
                viewModel.setOnPlayButtonClicked()
            } else {
                viewModel.setOnPauseButtonClicked()
            }
        }
        viewModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) {
                setPauseImage(binding.playPause)
            } else setPlayImage(binding.playPause)
        }
        viewModel.setProgressListener(binding.durationSeekBar)
        viewModel.isFavourite.observe(viewLifecycleOwner) {
            if (it == true) {
                setFavoriteImage(binding.favorite)
            } else {
                setNotFavoriteImage(binding.favorite)
            }
        }
        binding.favorite.setOnClickListener {
            if (it.tag == getString(R.string.favourite)) {
                setNotFavoriteImage(binding.favorite)
                songsViewModel.setSongNotFavourite(viewModel.currentPlayingSong.value!!.toDatabaseModel())
            } else {
                setFavoriteImage(binding.favorite)
                songsViewModel.setSongFavorite(viewModel.currentPlayingSong.value!!.toDatabaseModel())
            }
        }
    }

    private fun setFavoriteImage(imageView: ImageView) {
        imageView.tag = getString(R.string.favourite)
        imageView.setImageResource(R.drawable.baseline_favorite_24)
    }

    private fun setNotFavoriteImage(imageView: ImageView) {
        imageView.tag = getString(R.string.not_favourite)
        imageView.setImageResource(R.drawable.baseline_favorite_border_24)
    }

    private fun setPlayImage(imageView: ImageView) {
        imageView.tag = getString(R.string.play)
        imageView.setImageResource(R.drawable.baseline_play_arrow_24)
    }

    private fun setPauseImage(imageView: ImageView) {
        imageView.tag = getString(R.string.pause)
        imageView.setImageResource(R.drawable.baseline_pause_24)
    }

}