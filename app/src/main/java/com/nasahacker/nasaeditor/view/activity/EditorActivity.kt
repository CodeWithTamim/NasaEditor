package com.nasahacker.nasaeditor.view.activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.nasahacker.nasaeditor.R
import com.nasahacker.nasaeditor.adapter.FileAdapter
import com.nasahacker.nasaeditor.databinding.ActivityEditorBinding
import com.nasahacker.nasaeditor.listener.OnClickListener
import com.nasahacker.nasaeditor.util.Constants.FILE_URI
import com.nasahacker.nasaeditor.util.Constants.PROJECT_NAME
import com.nasahacker.nasaeditor.util.FileUtils
import com.nasahacker.nasaeditor.view.widget.CodeEditorView
import com.nasahacker.nasaeditor.viewmodel.EditorViewModel

class EditorActivity : AppCompatActivity(), OnClickListener<String> {

    private val binding by lazy { ActivityEditorBinding.inflate(layoutInflater) }
    private val editorViewModel: EditorViewModel by viewModels()
    private lateinit var adapter: FileAdapter
    private var projectName: String? = null
    private var currentFile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialize()
        setupObservers()
        setupListeners()
    }

    private fun initialize() {
        projectName = intent.getStringExtra(PROJECT_NAME)
        projectName?.let {
            editorViewModel.loadProjectFiles(this, it)
            adapter = FileAdapter(emptyList(), this, this)
            binding.rvFiles.adapter = adapter
            setSupportActionBar(binding.toolbar)
        } ?: run {
            Toast.makeText(
                this,
                getString(R.string.label_project_name_is_missing),
                Toast.LENGTH_SHORT
            ).show()
            finish()  // Close the activity if project name is missing
        }
    }

    private fun setupObservers() {
        editorViewModel.files.observe(this) { files ->
            adapter.updateData(files.map { it.name })
        }

        editorViewModel.file_content.observe(this) { content ->
            binding.edtCode.setText(content)
        }
    }

    private fun setupListeners() {
        binding.addFile.setOnClickListener { showCreateFileDialog() }
        binding.btnPreview.setOnClickListener { previewFile() }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.edtCode.setTheme(this, CodeEditorView.Theme.VS_CODE)


        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.top_files -> {
                    if (binding.drawerLayout.isOpen) {
                        binding.drawerLayout.close()
                    } else {
                        binding.drawerLayout.open()
                    }
                }

                R.id.top_themes -> {
                    showThemeChoosingDialog()
                }
            }
            true
        }
        if (currentFile.isEmpty()) {
            binding.edtCode.hint = getString(R.string.label_please_select_or_create_a_file)
            binding.edtCode.isEnabled = false
        }

        binding.edtCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveFile()  // Save on every text change
            }
        })

    }

    private fun showThemeChoosingDialog() {
        val themes = arrayOf(
            "Dracula",
            "Vs Code",
            "Monokai",
            "Solarized Dark",
            "Solarized Light",
            "Oceanic Next"
        )
        val themeMap = mapOf(
            "Dracula" to CodeEditorView.Theme.DRACULA,
            "Vs Code" to CodeEditorView.Theme.VS_CODE,
            "Monokai" to CodeEditorView.Theme.MONOKAI,
            "Solarized Dark" to CodeEditorView.Theme.SOLARIZED_DARK,
            "Solarized Light" to CodeEditorView.Theme.SOLARIZED_LIGHT,
            "Oceanic Next" to CodeEditorView.Theme.OCEANIC_NEXT
        )

        // Determine the index of the currently selected theme
        val currentThemeName =
            themeMap.entries.find { it.value == binding.edtCode.getCurrentTheme() }?.key
                ?: themes[0]
        val currentIndex = themes.indexOf(currentThemeName)

        MaterialAlertDialogBuilder(this)
            .setTitle("Choose Theme")
            .setSingleChoiceItems(themes, currentIndex) { dialog, which ->
                // Update the selected theme based on the user's choice
                selectedTheme = themeMap[themes[which]] ?: CodeEditorView.Theme.VS_CODE
            }
            .setPositiveButton("Set") { dialog, _ ->
                // Apply the selected theme
                editorViewModel.setTheme(this@EditorActivity, binding.edtCode, selectedTheme)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Define a variable to hold the selected theme
    private var selectedTheme = CodeEditorView.Theme.VS_CODE


    private fun showCreateFileDialog() {
        val dialog = Dialog(this).apply {
            setContentView(R.layout.create_dialog)
            window?.setBackgroundDrawableResource(R.drawable.blank_bg)
        }

        val edtFileName: TextInputEditText = dialog.findViewById(R.id.edtProjectName)
        val btnCreate: MaterialButton = dialog.findViewById(R.id.btnCreate)
        val tvDialogTitle: TextView = dialog.findViewById(R.id.tv_dialog_title)
        tvDialogTitle.text = getString(R.string.label_enter_a_file_name)

        btnCreate.setOnClickListener {
            val fileName = edtFileName.text.toString().trim()
            if (fileName.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.label_file_name_is_required), Toast.LENGTH_SHORT
                ).show()
            } else {
                projectName?.let {
                    editorViewModel.createFile(this, it, fileName)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun previewFile() {
        projectName?.let {
            if (currentFile.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.label_please_select_a_file_to_preview), Toast.LENGTH_SHORT
                ).show()
                return
            }
            val fileUri = FileUtils.getFileUri(this, it, currentFile)
            val intent = Intent(this, PreviewActivity::class.java).apply {
                putExtra(FILE_URI, fileUri.toString())
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(
                this,
                getString(R.string.label_project_name_is_missing), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveFile() {
        projectName?.let {

            editorViewModel.updateFile(
                this,
                it,
                currentFile,
                binding.edtCode.text.toString()
            )
        } ?: run {
            Toast.makeText(
                this,
                getString(R.string.label_project_name_is_missing),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onClick(data: String) {
        currentFile = data
        projectName?.let {
            editorViewModel.loadFileContent(this, it, data)
            Toast.makeText(this, getString(R.string.label_opened_file, data), Toast.LENGTH_SHORT)
                .show()
            binding.edtCode.hint = getString(R.string.hint_start_writing_your_code_on, data)
            binding.edtCode.isEnabled = true
            if (binding.drawerLayout.isOpen) {
                binding.drawerLayout.close()
            }
        }
    }

    override fun onLongPress(data: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.label_delete))
            .setMessage(getString(R.string.label_are_you_sure_you_want_to_delete, data))
            .setPositiveButton(getString(R.string.label_delete)) { dialog, _ ->
                projectName?.let {
                    editorViewModel.deleteFile(this, it, data)
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.label_cancel), null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }
}
