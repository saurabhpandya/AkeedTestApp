package com.akeedapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.akeedapp.MainActivity
import com.akeedapp.R
import com.akeedapp.data.ContentModel
import com.akeedapp.data.main.MainNetworkDataProvider
import com.akeedapp.databinding.FragmentDetailsBinding
import com.akeedapp.networking.RetrofitClient
import com.akeedapp.ui.ViewModelFactory
import com.akeedapp.ui.main.MainViewModel
import com.bumptech.glide.Glide


class DetailFragment : Fragment() {

    private val TAG = DetailFragment::class.java.canonicalName

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        getData()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                MainNetworkDataProvider(RetrofitClient.MAIN_SERVICE),
                requireActivity().application
            )
        ).get(MainViewModel::class.java)
        binding.vm = viewModel
        binding.content = viewModel.selectedContent
        binding.executePendingBindings()
        viewModel.fromDetail.postValue(true)
    }

    private fun getData() {
        viewModel.selectedContent = arguments?.getParcelable<ContentModel>("content")!!
        binding.content = viewModel.selectedContent
        binding.executePendingBindings()
        (activity as MainActivity).supportActionBar?.title =
            viewModel.selectedContent.Title ?: activity?.actionBar?.title
        Glide.with(requireActivity()).load(viewModel.selectedContent.Poster).centerCrop()
            .placeholder(R.drawable.placeholder_for_missing_posters)
            .into(binding.imgvwDetail)
    }


}