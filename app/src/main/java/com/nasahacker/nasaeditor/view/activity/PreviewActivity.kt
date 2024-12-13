package com.nasahacker.nasaeditor.view.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nasahacker.nasaeditor.R
import com.nasahacker.nasaeditor.databinding.ActivityPreviewBinding
import com.nasahacker.nasaeditor.util.AppUtils
import com.nasahacker.nasaeditor.util.Constants
import com.nasahacker.nasaeditor.viewmodel.PreviewViewModel

class PreviewActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPreviewBinding.inflate(layoutInflater) }
    private val previewViewModel: PreviewViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        AppUtils.applyInsetsToView(binding.main)
        setupWebView()
        observeViewModel()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Retrieve file URI from intent and set it in the ViewModel
        intent.getStringExtra(Constants.FILE_URI)?.let {
            previewViewModel.setFileUri(it)
        }
    }

    private fun setupWebView() {
        with(binding.previewWebView.settings) {
            javaScriptEnabled = true
            allowFileAccess = true
            allowContentAccess = true
        }

        binding.previewWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.tvWebsiteTitle.text = view?.title
                binding.icWebsiteIc.setImageResource(getIconResource(view?.title.orEmpty()))
            }
        }

        binding.previewWebView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                super.onReceivedIcon(view, icon)
                binding.icWebsiteIc.setImageBitmap(icon)
            }
        }
    }

    private fun observeViewModel() {
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

    private fun getIconResource(fileName: String): Int {
        return when {
            // HTML, CSS, JS, and Text files
            fileName.endsWith(Constants.HTML_FILE) -> R.drawable.html_ic
            fileName.endsWith(Constants.CSS_FILE) -> R.drawable.css_ic
            fileName.endsWith(Constants.JS_FILE) -> R.drawable.js_ic
            fileName.endsWith(Constants.SCSS_FILE) -> R.drawable.scss_ic
            fileName.endsWith(Constants.TEXT_FILE) -> R.drawable.file_ic

            // Video files
            fileName.matchesFileTypes(
                Constants.MP4_FILE,
                Constants.WEBM_FILE,
                Constants.MKV_FILE,
                Constants.FLV_FILE,
                Constants.AVI_FILE,
                Constants.MOV_FILE,
                Constants.WMV_FILE,
                Constants.THREE_GP_FILE,
                Constants.MPEG_FILE
            ) -> R.drawable.baseline_slow_motion_video_24

            // Audio files
            fileName.matchesFileTypes(
                Constants.MP3_FILE,
                Constants.WAV_FILE,
                Constants.OGG_FILE,
                Constants.M4A_FILE,
                Constants.FLAC_FILE,
                Constants.AAC_FILE
            ) -> R.drawable.baseline_audiotrack_24

            // Image files
            fileName.matchesFileTypes(
                Constants.PNG_FILE,
                Constants.JPG_FILE,
                Constants.JPEG_FILE,
                Constants.GIF_FILE,
                Constants.BMP_FILE,
                Constants.WEBP_FILE,
                Constants.TIFF_FILE,
                Constants.ICO_FILE
            ) -> R.drawable.baseline_image_24

            // Default icon for other file types
            else -> R.drawable.file_ic
        }
    }

    private fun String.matchesFileTypes(vararg fileTypes: String): Boolean {
        return fileTypes.any { this.endsWith(it, ignoreCase = true) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
