package com.nasahacker.nasaeditor.view.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.nasahacker.nasaeditor.R
import com.nasahacker.nasaeditor.adapter.ProjectAdapter
import com.nasahacker.nasaeditor.databinding.ActivityMainBinding
import com.nasahacker.nasaeditor.listener.OnClickListener
import com.nasahacker.nasaeditor.util.Constants.PROJECT_NAME
import com.nasahacker.nasaeditor.util.FileUtils
import com.nasahacker.nasaeditor.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), OnClickListener<String> {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var adapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = ProjectAdapter(this, emptyList(), this)
        binding.recyclerViewProjects.adapter = adapter
        setupObservers()
        mainViewModel.loadProjectList()

        binding.fabAddProject.setOnClickListener {
            if (!FileUtils.isPermissionGranted(this)) {
                FileUtils.requestPermission(this)
            } else {
                showCreateProjectDialog()
            }
        }
    }

    private fun setupObservers() {
        mainViewModel.projectNames.observe(this) { projectNames ->
            updateUI(projectNames)
        }
    }

    private fun updateUI(projectNames: List<String>) {
        if (projectNames.isEmpty()) {
            binding.animNotFound.visibility = View.VISIBLE
            binding.tvNoProjects.visibility = View.VISIBLE
            binding.recyclerViewProjects.visibility = View.GONE
        } else {
            binding.animNotFound.visibility = View.GONE
            binding.tvNoProjects.visibility = View.GONE
            binding.recyclerViewProjects.visibility = View.VISIBLE
            adapter.updateData(projectNames)
        }
    }

    private fun showCreateProjectDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.create_dialog)
        dialog.window?.setBackgroundDrawableResource(R.drawable.blank_bg)
        val edtProjectName: TextInputEditText = dialog.findViewById(R.id.edtProjectName)
        val btnCreate: MaterialButton = dialog.findViewById(R.id.btnCreate)
        dialog.show()

        btnCreate.setOnClickListener {
            val projectName = edtProjectName.text.toString().trim()
            if (projectName.isEmpty()) {
                Toast.makeText(this,
                    getString(R.string.label_project_name_is_required), Toast.LENGTH_SHORT).show()
            } else {
                mainViewModel.addProject(projectName)
                dialog.dismiss()
            }
        }
    }

    override fun onClick(data: String) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(PROJECT_NAME, data)
        startActivity(intent)
    }

    override fun onLongPress(data: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.label_delete))
            .setMessage(getString(R.string.label_are_you_sure_you_want_to_delete, data))
            .setPositiveButton(getString(R.string.label_delete)) { dialog, p1 ->
                mainViewModel.deleteProject(this@MainActivity, data)
                dialog?.dismiss()
            }
            .setNegativeButton(getString(R.string.label_cancel), null)
            .show()
    }
}
