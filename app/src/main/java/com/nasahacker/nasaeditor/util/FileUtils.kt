package com.nasahacker.nasaeditor.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.nasahacker.nasaeditor.util.Constants.APP_FOLDER_NAME
import com.nasahacker.nasaeditor.util.Constants.FILE_PROVIDER
import com.nasahacker.nasaeditor.util.Constants.PROJECTS_FOLDER_NAME
import java.io.*

object FileUtils {

    fun isPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        }
    }

    fun requestPermission(context: Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }

    // Create a new project directory
    fun createProject(context: Context, projectName: String): File {
        val projectFolder = File(
            context.getExternalFilesDir(null),
            "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME/$projectName"
        )
        if (!projectFolder.exists()) {
            projectFolder.mkdirs()
        }
        return projectFolder
    }

    // Create a file within a specific project
    @Throws(IOException::class)
    fun createFile(
        context: Context,
        projectName: String,
        fileName: String
    ): File {
        val projectFolder = createProject(context, projectName)
        val file = File(projectFolder, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    // Write content to a file
    @Throws(IOException::class)
    fun writeToFile(file: File, content: String) {
        FileOutputStream(file).use { fos ->
            fos.write(content.toByteArray())
        }
    }

    // Read content from a file
    @Throws(IOException::class)
    fun readFromFile(file: File): String {
        val content = StringBuilder()
        FileInputStream(file).use { fis ->
            BufferedReader(InputStreamReader(fis)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    content.append(line).append("\n")
                }
            }
        }
        return content.toString()
    }

    @Throws(IOException::class)
    fun readProjectFile(context: Context, projectName: String, fileName: String): String {
        val projectFolder = createProject(context, projectName) // Create project folder if needed
        val file = File(projectFolder, fileName)
        return if (fileExists(file)) {
            readFromFile(file) // Use existing readFromFile function
        } else {
            "" // Handle case where file doesn't exist
        }
    }

    // Update a file with new content (overwrite existing content)
    @Throws(IOException::class)
    fun updateFile(context: Context, projectName: String, fileName: String, newContent: String) {
        val projectFolder = createProject(context, projectName) // Create project folder if needed
        val file = File(projectFolder, fileName)
        writeToFile(file, newContent)
    }

    // Delete a file
    fun deleteFile(context: Context, projectName: String, fileName: String): Boolean {
        val file = File(createProject(context, projectName), fileName)
        return file.exists() && file.delete()
    }

    fun deleteProject(context: Context, projectName: String): Boolean {
        val projectFolder = File(
            context.getExternalFilesDir(null),
            "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME/$projectName"
        )

        return if (projectFolder.exists()) {
            deleteRecursively(projectFolder)
        } else {
            false
        }
    }

    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) {
            // Delete all files and subdirectories
            val files = file.listFiles()
            files?.forEach { child ->
                if (!deleteRecursively(child)) {
                    return false
                }
            }
        }
        return file.delete()
    }

    // Get a list of all files in a specific project
    fun listProjectFiles(context: Context, projectName: String): Array<File>? {
        val projectFolder = File(
            context.getExternalFilesDir(null),
            "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME/$projectName"
        )
        return projectFolder.listFiles()
    }

    // Get a list of all projects
    fun listProjects(context: Context): Array<File>? {
        val projectsFolder =
            File(context.getExternalFilesDir(null), "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME")
        return projectsFolder.listFiles()
    }

    fun getFileUri(context: Context, projectName: String, fileName: String): Uri {
        val file = File(createProject(context, projectName), fileName)
        return FileProvider.getUriForFile(context, "${context.packageName}$FILE_PROVIDER", file)
    }

    // Check if a file exists
    fun fileExists(file: File): Boolean {
        return file.exists()
    }
}
