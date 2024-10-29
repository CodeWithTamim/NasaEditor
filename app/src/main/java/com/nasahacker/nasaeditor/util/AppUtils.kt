package com.nasahacker.nasaeditor.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.nasahacker.nasaeditor.util.Constants.APP_FOLDER_NAME
import com.nasahacker.nasaeditor.util.Constants.FILE_PROVIDER
import com.nasahacker.nasaeditor.util.Constants.PROJECTS_FOLDER_NAME
import java.io.*

object AppUtils {

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

    @Throws(IOException::class)
    fun writeToFile(file: File, content: String) {
        if (file.isDirectory) {
            throw IOException("Cannot write to a directory: ${file.absolutePath}")
        }
        FileOutputStream(file).use { fos ->
            fos.write(content.toByteArray())
        }
    }

    @Throws(IOException::class)
    fun readFromFile(file: File): String {
        if (file.isDirectory) {
            throw IOException("Cannot read from a directory: ${file.absolutePath}")
        }
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
        val projectFolder = createProject(context, projectName)
        val file = File(projectFolder, fileName)
        return if (fileExists(file)) {
            readFromFile(file)
        } else {
            "" // Handle case where file doesn't exist
        }
    }
    fun refreshFolder(context: Context, projectName: String): Array<File>? {
        val projectFolder = createProject(context, projectName)
        return if (projectFolder.exists() && projectFolder.isDirectory) {
            projectFolder.listFiles() // Returns an array of files in the folder
        } else {
            null // Folder doesn't exist
        }
    }

    fun getBitmapFromFile(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an error
        }
    }
    fun getBitmapFromResource(context: Context, resId: Int): Bitmap? {
        return try {
            BitmapFactory.decodeResource(context.resources, resId)
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an error
        }
    }

    @Throws(IOException::class)
    fun updateFile(context: Context, projectName: String, fileName: String, newContent: String) {
        val projectFolder = createProject(context, projectName)
        val file = File(projectFolder, fileName)
        writeToFile(file, newContent)
    }

    fun openFilePicker(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Allow any file type
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*", "audio/*"))
        }
        activity.startActivityForResult(intent, requestCode)
    }


    fun copyFileToProjectFolder(context: Context, uri: Uri, projectName: String, fileName: String) {
        val projectFolder = createProject(context, projectName)
        val destinationFile = File(projectFolder, fileName)

        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(destinationFile)
        val buffer = ByteArray(1024)
        var length: Int

        try {
            if (inputStream != null) {
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error copying file: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            outputStream.close()
            inputStream?.close()
        }
    }


    fun getFileNameFromUri(context: Context,uri: Uri): String {
        var fileName = "unknown"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }


    fun deleteFile(context: Context, projectName: String, fileName: String): Boolean {
        val file = File(createProject(context, projectName), fileName)
        if (file.isDirectory) {
            // Optionally, you can throw an exception or handle the case where it's a directory
            return false
        }
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
            val files = file.listFiles()
            files?.forEach { child ->
                if (!deleteRecursively(child)) {
                    return false
                }
            }
        }
        return file.delete()
    }

    fun listProjectFiles(context: Context, projectName: String): Array<File>? {
        val projectFolder = File(
            context.getExternalFilesDir(null),
            "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME/$projectName"
        )
        return projectFolder.listFiles()
    }

    fun listProjects(context: Context): Array<File>? {
        val projectsFolder =
            File(context.getExternalFilesDir(null), "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME")
        return projectsFolder.listFiles()
    }

    fun getFileUri(context: Context, projectName: String, fileName: String): Uri {
        val file = File(createProject(context, projectName), fileName)
        return FileProvider.getUriForFile(context, "${context.packageName}$FILE_PROVIDER", file)
    }

    fun fileExists(file: File): Boolean {
        return file.exists()
    }
}
