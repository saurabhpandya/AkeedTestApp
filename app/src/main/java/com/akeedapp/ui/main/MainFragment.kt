package com.akeedapp.ui.main

import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akeedapp.R
import com.akeedapp.data.ContentModel
import com.akeedapp.data.main.MainNetworkDataProvider
import com.akeedapp.databinding.FragmentMainBinding
import com.akeedapp.networking.RetrofitClient
import com.akeedapp.ui.ViewModelFactory
import com.akeedapp.utility.AutoFitGridLayoutManager
import com.akeedapp.utility.OnItemClickListner
import com.diagnal.utils.PaginationScrollListener
import com.diagnal.utils.Status
import com.diagnal.utils.Utility


class MainFragment : Fragment(), OnItemClickListner {

    private val TAG = MainFragment::class.java.canonicalName

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    private val waitingTime = 200
    private var cntr: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated")
        setupViewModel()
        setupRecyclrView()
        registerObserver()
        setupSearchView()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.toolbar.title = viewModel.title
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                setLayoutManager(newConfig.orientation)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                setLayoutManager(newConfig.orientation)
            }
        }
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
    }

    private fun setupRecyclrView() {
        setLayoutManager(resources.configuration.orientation)
        binding.rcyclrvwContent.setHasFixedSize(true)
        binding.rcyclrvwContent.adapter = viewModel.contentAdapter
        viewModel.contentAdapter.onItemClickListner = this
        binding.rcyclrvwContent.addOnScrollListener(object :
            PaginationScrollListener(binding.rcyclrvwContent.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                viewModel.isLoading = true
                fetchMoreData()
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading
            }

            override fun isLastPage(): Boolean {
                return viewModel.totalPageCount == viewModel.pageNum
            }

            override fun isSearchOn(): Boolean {
                return viewModel.isSearchOn
            }
        })
    }

    private fun registerObserver() {
        viewModel.searchResultResponse.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    viewModel.isLoading = true
                    binding.prgrs.visibility = View.VISIBLE
                    binding.noData.visibility = View.GONE
                    binding.rcyclrvwContent.visibility = View.GONE
                }
                Status.ERROR -> {
                    viewModel.isLoading = false
                    binding.prgrs.visibility = View.GONE
                    binding.noData.visibility = View.VISIBLE
                    binding.rcyclrvwContent.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    viewModel.isLoading = false
                    binding.prgrs.visibility = View.GONE
                    binding.noData.visibility = View.GONE
                    binding.rcyclrvwContent.visibility = View.VISIBLE
                    if (!it.data!!.isEmpty()) {
                        viewModel.updatePagingData(it.data)
                    }
                }
            }
        })
    }

    private fun setupSearchView() {
        val searchMenuItem: MenuItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.isSearchOn = true
                cntr?.cancel()
                cntr = object : CountDownTimer(waitingTime.toLong(), 500) {
                    override fun onTick(millisUntilFinished: Long) {
                        Log.d(
                            "TIME",
                            "seconds remaining: " + millisUntilFinished / 1000
                        )
                    }

                    override fun onFinish() {
                        Log.d("FINISHED", "DONE")
                        if (!newText.isNullOrEmpty() && newText.length > 3) {
                            searchQuery(newText)
                        } else {
                            searchQuery("")
                        }
                    }
                }
                (cntr as CountDownTimer).start()
                return false
            }
        })

        searchView.setIconifiedByDefault(true)
        searchView.onActionViewExpanded()
        searchView.setQuery("", true)

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                viewModel.isSearchOn = false
                searchQuery("")
                searchView.onActionViewCollapsed()
                return false
            }
        })
    }

    private fun setLayoutManager(orientation: Int) {
        var numOfColumns: Int = 0
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> numOfColumns = 2
            Configuration.ORIENTATION_LANDSCAPE -> numOfColumns = 4
        }
        binding.rcyclrvwContent.layoutManager =
            AutoFitGridLayoutManager(
                activity,
                (Utility.getDisplayMetrics(requireActivity()).widthPixels) / numOfColumns
            )
    }

    private fun searchQuery(query: String) {
        if (query.isNullOrEmpty()) {
            viewModel.contentAdapter.setContent(ArrayList<ContentModel>())
            viewModel.arylstContent = ArrayList<ContentModel>()
            viewModel.pageNum = 1
            viewModel.finalQuery = ""
        } else {
            viewModel.searchQuery(query)
        }
    }

    private fun fetchMoreData() {

        viewModel.fetchMoreData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    viewModel.isLoading = true
                    binding.prgrs.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    viewModel.isLoading = false
                    binding.prgrs.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    viewModel.isLoading = false
                    binding.prgrs.visibility = View.GONE
                    binding.noData.visibility = View.GONE
                    binding.rcyclrvwContent.visibility = View.VISIBLE
                    if (!it.data!!.isEmpty()) {
                        viewModel.updatePagingData(it.data)
                    }
                }
            }
        })

    }

    override fun onItemClickListner(position: Int) {
        val content = viewModel.arylstContent.get(position)
        Log.d(TAG, "onItemClickListner : ${content}")
        val bundle = Bundle()
        bundle.putParcelable("content", content)
        view?.findNavController()?.navigate(R.id.action_mainFragment_to_detailFragment, bundle)

    }

}