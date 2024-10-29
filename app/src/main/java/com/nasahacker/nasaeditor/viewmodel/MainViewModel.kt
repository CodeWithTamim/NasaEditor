package com.nasahacker.nasaeditor.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nasahacker.nasaeditor.util.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _projectNames = MutableLiveData<MutableList<String>>()
    val projectNames: LiveData<MutableList<String>> = _projectNames

    fun loadProjectList() {
        if (AppUtils.isPermissionGranted(getApplication())) {
            viewModelScope.launch(Dispatchers.IO) {
                val projectList = AppUtils.listProjects(getApplication())
                _projectNames.postValue(
                    projectList?.map { it.name }?.toMutableList() ?: mutableListOf()
                )
            }
        }
    }

    fun addProject(projectName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppUtils.createProject(getApplication(), projectName)
                _projectNames.postValue(_projectNames.value?.apply { add(projectName) })
            } catch (e: Exception) {
                //IDK
            }

        }
    }

    fun deleteProject(context: Context, projectName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AppUtils.deleteProject(context, projectName)
            loadProjectList()
        }
    }
}
