package com.nasahacker.nasaeditor.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasahacker.nasaeditor.util.FileUtils
import com.nasahacker.nasaeditor.util.FileUtils.createProject
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
            _files.postValue(FileUtils.listProjectFiles(context, projectName))
        }
    }


    fun createFile(context: Context, projectName: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.createFile(context, projectName, fileName)
            _files.postValue(FileUtils.listProjectFiles(context, projectName))
        }
    }

    fun loadFileContent(context: Context, projectName: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _file_content.postValue(FileUtils.readProjectFile(context, projectName, fileName))
        }
    }

    fun updateFile(context: Context, projectName: String, fileName: String, newContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val projectFolder = createProject(context, projectName)
            val file = File(projectFolder, fileName)


            loadProjectFiles(context, projectName)
            if (FileUtils.fileExists(file)) {
                FileUtils.updateFile(context, projectName, fileName, newContent)
            }
        }
    }

    fun deleteFile(context: Context, projectName: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteFile(context, projectName, fileName)
            loadProjectFiles(context, projectName)
            loadFileContent(context, projectName, fileName)
        }
    }

    fun setTheme(context: Context, codeView: CodeEditorView, theme: CodeEditorView.Theme) {
        viewModelScope.launch(Dispatchers.Main) {
            codeView.setTheme(context, theme)
        }
    }


}