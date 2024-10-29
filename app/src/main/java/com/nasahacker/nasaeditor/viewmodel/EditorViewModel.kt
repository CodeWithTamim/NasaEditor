package com.nasahacker.nasaeditor.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasahacker.nasaeditor.util.AppUtils
import com.nasahacker.nasaeditor.util.AppUtils.copyFileToProjectFolder
import com.nasahacker.nasaeditor.util.AppUtils.createProject
import com.nasahacker.nasaeditor.util.AppUtils.getFileNameFromUri
import com.nasahacker.nasaeditor.view.widget.CodeEditorView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class EditorViewModel : ViewModel() {
    private val _files = MutableLiveData<Array<File>>()
    val files get() = _files
    private val _file_content = MutableLiveData<String>()
    val file_content get() = _file_content


    fun loadProjectFiles(context: Context, projectName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _files.postValue(AppUtils.listProjectFiles(context, projectName))
        }
    }


    fun createFile(context: Context, projectName: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AppUtils.createFile(context, projectName, fileName)
            _files.postValue(AppUtils.listProjectFiles(context, projectName))
        }
    }

    fun loadFileContent(context: Context, projectName: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _file_content.postValue(AppUtils.readProjectFile(context, projectName, fileName))
        }
    }

    fun updateFile(context: Context, projectName: String, fileName: String, newContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val projectFolder = createProject(context, projectName)
            val file = File(projectFolder, fileName)


            loadProjectFiles(context, projectName)
            if (fileName.isNotEmpty() && AppUtils.fileExists(file)) {
                AppUtils.updateFile(context, projectName, fileName, newContent)
            }
        }
    }

    fun deleteFile(context: Context, projectName: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AppUtils.deleteFile(context, projectName, fileName)
            loadProjectFiles(context, projectName)
            loadFileContent(context, projectName, fileName)
        }
    }

    fun setTheme(context: Context, codeView: CodeEditorView, theme: CodeEditorView.Theme) {
        viewModelScope.launch(Dispatchers.Main) {
            codeView.setTheme(context, theme)
        }
    }

    fun refresh(context: Context, projectName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AppUtils.refreshFolder(context, projectName!!)
        }
    }

    fun copyFile(context: Context, uri: Uri, projectName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = getFileNameFromUri(context, uri)
            copyFileToProjectFolder(context, uri, projectName, fileName)

            // Load project files again to update the list
            loadProjectFiles(context, projectName)
        }
    }



}