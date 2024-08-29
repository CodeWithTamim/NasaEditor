package com.nasahacker.nasaeditor.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nasahacker.nasaeditor.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _projectNames = MutableLiveData<MutableList<String>>()
    val projectNames: LiveData<MutableList<String>> = _projectNames

    fun loadProjectList() {
        if (FileUtils.isPermissionGranted(getApplication())) {
            viewModelScope.launch(Dispatchers.IO) {
                val projectList = FileUtils.listProjects(getApplication())
                _projectNames.postValue(
                    projectList?.map { it.name }?.toMutableList() ?: mutableListOf()
                )
            }
        }
    }

    fun addProject(projectName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.createProject(getApplication(), projectName)
            _projectNames.postValue(_projectNames.value?.apply { add(projectName) })
        }
    }

    fun deleteProject(context: Context, projectName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteProject(context, projectName)
            loadProjectList()
        }
    }
}
