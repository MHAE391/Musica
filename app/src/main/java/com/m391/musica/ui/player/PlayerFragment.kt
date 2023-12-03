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
import com.m391.musica.utils.setFavoriteImage
import com.m391.musica.utils.setNotFavoriteImage
import kotlinx.coroutines.launch

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
            if (args.songDestination == getString(R.string.home)) songsViewModel.deviceSongs else songsViewModel.favouriteSongs,
            songsViewModel.checkFavourite,
            songsViewModel.setSongFavourite,
            songsViewModel.setSongNotFavourite
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
            viewModel.nextPrevious(1)
        }
        binding.previous.setOnClickListener {
            viewModel.nextPrevious(-1)
        }
        binding.playPause.setOnClickListener {
            if (it.tag == getString(R.string.play)) {
                viewModel.play()
            } else {
                viewModel.pause()
            }
        }
        binding.favorite.setOnClickListener {
            if (it.tag == getString(R.string.favourite)) {
                setNotFavorite(binding.favorite)
            } else {
                setFavorite(binding.favorite)
            }
        }
        viewModel.setProgressListener(binding.durationSeekBar)
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            viewModel.startCheckFavourite(viewLifecycleOwner)
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) setPauseImage(binding.playPause)
            else setPlayImage(binding.playPause)
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            viewModel.stopCheckFavourite(viewLifecycleOwner)
            viewModel.isPlaying.removeObservers(viewLifecycleOwner)
            viewModel.isFavourite.removeObservers(viewLifecycleOwner)
        }
    }

    private fun setFavorite(imageView: ImageView) {
        setFavoriteImage(imageView)
        lifecycleScope.launch {
            viewModel.setFavourite()
        }
    }

    private fun setNotFavorite(imageView: ImageView) {
        setNotFavoriteImage(imageView)
        lifecycleScope.launch {
            viewModel.setNotFavourite()
        }
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