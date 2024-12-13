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
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nasahacker.nasaeditor.util.Constants.APP_FOLDER_NAME
import com.nasahacker.nasaeditor.util.Constants.FILE_PROVIDER
import com.nasahacker.nasaeditor.util.Constants.PROJECTS_FOLDER_NAME
import java.io.*

object AppUtils {

    /**
     * Applies window insets to the provided view, adjusting the padding to avoid system UI overlap.
     *
     * @param view The view to which window insets should be applied.
     */
    fun applyInsetsToView(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    /**
     * Checks if the required permissions for reading and writing to external storage are granted.
     *
     * @param context The context used to check permissions.
     * @return True if permissions are granted, otherwise false.
     */
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

    /**
     * Requests permission to read and write external storage.
     *
     * @param context The activity requesting the permissions.
     */
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

    /**
     * Creates a new project folder if it does not already exist.
     *
     * @param context The context used to access external storage.
     * @param projectName The name of the project to create.
     * @return The created project folder.
     */
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

    /**
     * Creates a new file in the specified project folder.
     *
     * @param context The context used to access external storage.
     * @param projectName The name of the project.
     * @param fileName The name of the file to create.
     * @return The created file.
     * @throws IOException If an error occurs during file creation.
     */
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

    /**
     * Writes the specified content to a file.
     *
     * @param file The file to write content to.
     * @param content The content to write to the file.
     * @throws IOException If an error occurs during writing.
     */
    @Throws(IOException::class)
    fun writeToFile(file: File, content: String) {
        if (file.isDirectory) {
            throw IOException("Cannot write to a directory: ${file.absolutePath}")
        }
        FileOutputStream(file).use { fos ->
            fos.write(content.toByteArray())
        }
    }

    /**
     * Reads the content of a file and returns it as a string.
     *
     * @param file The file to read from.
     * @return The content of the file as a string.
     * @throws IOException If an error occurs during reading.
     */
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

    /**
     * Reads a project file's content if it exists.
     *
     * @param context The context used to access external storage.
     * @param projectName The project name.
     * @param fileName The file name.
     * @return The content of the file, or an empty string if the file does not exist.
     */
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

    /**
     * Refreshes the folder of a project by listing its contents.
     *
     * @param context The context used to access external storage.
     * @param projectName The project name.
     * @return An array of files in the project folder, or null if the folder doesn't exist.
     */
    fun refreshFolder(context: Context, projectName: String): Array<File>? {
        val projectFolder = createProject(context, projectName)
        return if (projectFolder.exists() && projectFolder.isDirectory) {
            projectFolder.listFiles() // Returns an array of files in the folder
        } else {
            null // Folder doesn't exist
        }
    }

    /**
     * Converts a file into a Bitmap image.
     *
     * @param file The file to convert.
     * @return A Bitmap representing the file, or null if an error occurs.
     */
    fun getBitmapFromFile(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an error
        }
    }

    /**
     * Converts a resource into a Bitmap image.
     *
     * @param context The context used to access resources.
     * @param resId The resource ID of the image.
     * @return A Bitmap representing the resource, or null if an error occurs.
     */
    fun getBitmapFromResource(context: Context, resId: Int): Bitmap? {
        return try {
            BitmapFactory.decodeResource(context.resources, resId)
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an error
        }
    }

    /**
     * Updates the content of a file.
     *
     * @param context The context used to access external storage.
     * @param projectName The project name.
     * @param fileName The file name.
     * @param newContent The new content to write to the file.
     * @throws IOException If an error occurs during writing.
     */
    @Throws(IOException::class)
    fun updateFile(context: Context, projectName: String, fileName: String, newContent: String) {
        val projectFolder = createProject(context, projectName)
        val file = File(projectFolder, fileName)
        writeToFile(file, newContent)
    }

    /**
     * Opens the file picker to select a file.
     *
     * @param activity The activity requesting the file picker.
     * @param requestCode The request code for identifying the result.
     */
    fun openFilePicker(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Allow any file type
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*", "audio/*"))
        }
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Copies a file from a URI to the project folder.
     *
     * @param context The context used to access external storage.
     * @param uri The URI of the file to copy.
     * @param projectName The project name.
     * @param fileName The file name in the project folder.
     */
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

    /**
     * Retrieves the file name from a URI.
     *
     * @param context The context used to access content resolver.
     * @param uri The URI of the file.
     * @return The file name extracted from the URI.
     */
    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var fileName = "unknown"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }

    /**
     * Deletes a file from the project folder.
     *
     * @param context The context used to access external storage.
     * @param projectName The project name.
     * @param fileName The file name to delete.
     * @return True if the file was successfully deleted, otherwise false.
     */
    fun deleteFile(context: Context, projectName: String, fileName: String): Boolean {
        val file = File(createProject(context, projectName), fileName)
        if (file.isDirectory) {
            return false
        }
        return file.exists() && file.delete()
    }

    /**
     * Deletes a project folder and its contents.
     *
     * @param context The context used to access external storage.
     * @param projectName The project name to delete.
     * @return True if the project folder was successfully deleted, otherwise false.
     */
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

    /**
     * Recursively deletes files and directories.
     *
     * @param file The file or directory to delete.
     * @return True if deletion was successful, otherwise false.
     */
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

    /**
     * Lists all files in a project's folder.
     *
     * @param context The context used to access external storage.
     * @param projectName The project name.
     * @return An array of files in the project folder.
     */
    fun listProjectFiles(context: Context, projectName: String): Array<File>? {
        val projectFolder = File(
            context.getExternalFilesDir(null),
            "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME/$projectName"
        )
        return projectFolder.listFiles()
    }

    /**
     * Lists all projects in the project folder.
     *
     * @param context The context used to access external storage.
     * @return An array of project folders.
     */
    fun listProjects(context: Context): Array<File>? {
        val projectsFolder =
            File(context.getExternalFilesDir(null), "$APP_FOLDER_NAME/$PROJECTS_FOLDER_NAME")
        return projectsFolder.listFiles()
    }

    /**
     * Gets the URI for a file in a project's folder.
     *
     * @param context The context used to access external storage.
     * @param projectName The project name.
     * @param fileName The file name.
     * @return The URI for the file.
     */
    fun getFileUri(context: Context, projectName: String, fileName: String): Uri {
        val file = File(createProject(context, projectName), fileName)
        return FileProvider.getUriForFile(context, "${context.packageName}$FILE_PROVIDER", file)
    }

    /**
     * Checks if a file exists.
     *
     * @param file The file to check.
     * @return True if the file exists, otherwise false.
     */
    fun fileExists(file: File): Boolean {
        return file.exists()
    }
}
