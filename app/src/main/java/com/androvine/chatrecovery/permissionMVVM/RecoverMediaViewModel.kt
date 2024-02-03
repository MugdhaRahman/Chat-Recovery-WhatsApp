package com.androvine.chatrecovery.permissionMVVM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androvine.chatrecovery.activity.MainActivity
import com.androvine.chatrecovery.utils.MediaFilesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecoverMediaViewModel(private val repository: MediaFilesRepository) : ViewModel() {

    val imageList = MutableLiveData<List<MainActivity.RecoveredMedia>>()
    val videoList = MutableLiveData<List<MainActivity.RecoveredMedia>>()

    private val _imageList = mutableListOf<MainActivity.RecoveredMedia>()
    private val _videoList = mutableListOf<MainActivity.RecoveredMedia>()


    fun getFiles() {
        viewModelScope.launch(Dispatchers.IO) {

            _imageList.addAll(repository.getImageFiles())
            _videoList.addAll(repository.getVideoFiles())

            withContext(Dispatchers.Main) {
                imageList.value = _imageList
                videoList.value = _videoList
            }
        }
    }


}