package com.akeedapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akeedapp.data.main.MainNetworkDataProvider
import com.akeedapp.data.main.MainRepository
import com.akeedapp.ui.main.MainViewModel

class ViewModelFactory<T>(private val dataProvider: T, private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                application,
                MainRepository(dataProvider as MainNetworkDataProvider)
            ) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}