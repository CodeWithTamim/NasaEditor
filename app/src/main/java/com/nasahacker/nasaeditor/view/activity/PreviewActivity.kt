package com.nasahacker.nasaeditor.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nasahacker.nasaeditor.databinding.ActivityPreviewBinding
import com.nasahacker.nasaeditor.util.Constants.FILE_URI
import com.nasahacker.nasaeditor.viewmodel.PreviewViewModel

class PreviewActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPreviewBinding.inflate(layoutInflater)
    }
    private val previewViewModel: PreviewViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val webSettings = binding.previewWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true

        binding.previewWebView.webViewClient = WebViewClient()

        // Retrieve file URI from intent and set it in the ViewModel
        val fileUriString = intent.getStringExtra(FILE_URI)
        previewViewModel.setFileUri(fileUriString)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Observe changes in the ViewModel
        previewViewModel.fileUri.observe(this) { uri ->
            uri?.let {
                binding.previewWebView.loadUrl(it.toString())
            }
        }

        previewViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
