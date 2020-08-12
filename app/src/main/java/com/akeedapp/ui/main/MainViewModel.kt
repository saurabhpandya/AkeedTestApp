package com.akeedapp.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.akeedapp.data.ContentModel
import com.akeedapp.data.main.MainRepository
import com.akeedapp.ui.main.adapter.ContentAdapter
import com.diagnal.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    application: Application,
    val mainRepository: MainRepository
) : AndroidViewModel(application) {

    private val TAG = MainViewModel::class.java.canonicalName
    private val context = getApplication<Application>().applicationContext

    var contentAdapter: ContentAdapter
    var arylstContent: ArrayList<ContentModel>

    var totalPageCount: Int = 0
    var pageNum: Int = 1

    var title: String = ""
    var finalQuery: String = ""

    var isLoading: Boolean = false
    var isSearchOn: Boolean = false

    var searchResultResponse = MutableLiveData<Resource<ArrayList<ContentModel>>>()

    var selectedContent = ContentModel()

    var fromDetail = MutableLiveData<Boolean>(false)

    init {
        arylstContent = ArrayList<ContentModel>()
        contentAdapter =
            ContentAdapter(context, arylstContent)
    }

    fun searchQuery(query: String) = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            searchResultResponse.value = Resource.loading(null)
        }
        try {
            if (query.length >= 3) {
                finalQuery = query
                Log.d(TAG, "searchQuery::$query")
                val searchResultResponse_ =
                    mainRepository.getSearchResult(query, pageNum.toString())
                if (searchResultResponse_.Response.equals("True", true)) {
                    totalPageCount = (searchResultResponse_.totalResults!!.toInt() / 10)
                    withContext(Dispatchers.Main) {
                        searchResultResponse.value =
                            Resource.success(searchResultResponse_.Search!!)

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        searchResultResponse.value = Resource.error(
                            null,
                            searchResultResponse_.Error ?: "Something went wrong"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                searchResultResponse.value = Resource.error(null, e.localizedMessage)
            }
        }
    }

    fun fetchMoreData() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            if (pageNum < totalPageCount) {
                pageNum = pageNum + 1
                val contentResponse = mainRepository.getSearchResult(finalQuery, pageNum.toString())
                if (contentResponse.Response.equals("True", true)) {
                    totalPageCount = (contentResponse.totalResults!!.toInt() / 10)
                    emit(Resource.success(contentResponse.Search!!))
                } else {
                    emit(
                        Resource.error(
                            null,
                            contentResponse.Error ?: "Something went wrong"
                        )
                    )

                }

            } else {
                emit(Resource.error(null, "Page count exceeded"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.error(null, e.localizedMessage))
        }
    }

    fun updatePagingData(aryLstContent: ArrayList<ContentModel>) {
        if (pageNum == 1) {
            arylstContent = aryLstContent
        } else {
            arylstContent.addAll(aryLstContent)
        }
        updateAdapter(arylstContent)

    }

    private fun updateAdapter(arylstContent: ArrayList<ContentModel>) {
        if (pageNum == 1) {
            contentAdapter.setContent(arylstContent)
        } else {
            contentAdapter.addContent(arylstContent)
        }
    }
}


