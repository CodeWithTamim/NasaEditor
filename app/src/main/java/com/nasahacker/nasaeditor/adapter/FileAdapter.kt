package com.nasahacker.nasaeditor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nasahacker.nasaeditor.R
import com.nasahacker.nasaeditor.databinding.FileItemBinding
import com.nasahacker.nasaeditor.listener.OnClickListener
import com.nasahacker.nasaeditor.util.Constants.AAC_FILE
import com.nasahacker.nasaeditor.util.Constants.AVI_FILE
import com.nasahacker.nasaeditor.util.Constants.BMP_FILE
import com.nasahacker.nasaeditor.util.Constants.CSS_FILE
import com.nasahacker.nasaeditor.util.Constants.FLAC_FILE
import com.nasahacker.nasaeditor.util.Constants.FLV_FILE
import com.nasahacker.nasaeditor.util.Constants.GIF_FILE
import com.nasahacker.nasaeditor.util.Constants.HTML_FILE
import com.nasahacker.nasaeditor.util.Constants.ICO_FILE
import com.nasahacker.nasaeditor.util.Constants.JPEG_FILE
import com.nasahacker.nasaeditor.util.Constants.JPG_FILE
import com.nasahacker.nasaeditor.util.Constants.JS_FILE
import com.nasahacker.nasaeditor.util.Constants.M4A_FILE
import com.nasahacker.nasaeditor.util.Constants.MKV_FILE
import com.nasahacker.nasaeditor.util.Constants.MOV_FILE
import com.nasahacker.nasaeditor.util.Constants.MP3_FILE
import com.nasahacker.nasaeditor.util.Constants.MP4_FILE
import com.nasahacker.nasaeditor.util.Constants.MPEG_FILE
import com.nasahacker.nasaeditor.util.Constants.OGG_FILE
import com.nasahacker.nasaeditor.util.Constants.PNG_FILE
import com.nasahacker.nasaeditor.util.Constants.SCSS_FILE
import com.nasahacker.nasaeditor.util.Constants.TEXT_FILE
import com.nasahacker.nasaeditor.util.Constants.THREE_GP_FILE
import com.nasahacker.nasaeditor.util.Constants.TIFF_FILE
import com.nasahacker.nasaeditor.util.Constants.WAV_FILE
import com.nasahacker.nasaeditor.util.Constants.WEBM_FILE
import com.nasahacker.nasaeditor.util.Constants.WEBP_FILE
import com.nasahacker.nasaeditor.util.Constants.WMV_FILE
import java.io.File

class FileAdapter(
    private var list: List<File>,
    private val context: Context,
    private val listener: OnClickListener<String>
) : Adapter<FileAdapter.FileViewHolder>() {

    inner class FileViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding = FileItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(
            LayoutInflater.from(context).inflate(R.layout.file_item, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.tvFileName.text = currentItem.name

        // Check if the file is an asset (audio, video, image)
        if (!isAssetFile(currentItem.name)) {
            holder.itemView.setOnClickListener {
                listener.onClick(holder.binding.tvFileName.text.toString())
            }
        } else {
            // If it's an asset file, don't set the click listener
            holder.itemView.setOnClickListener(null)
        }

        holder.itemView.setOnLongClickListener {
            listener.onLongPress(holder.binding.tvFileName.text.toString())
            true
        }

        holder.binding.ivFileIcon.setImageResource(getIconResource(currentItem.name))
    }

    private fun isAssetFile(fileName: String): Boolean {
        return fileName.endsWith(MP4_FILE) || fileName.endsWith(WEBM_FILE) || fileName.endsWith(MKV_FILE) ||
                fileName.endsWith(FLV_FILE) || fileName.endsWith(AVI_FILE) || fileName.endsWith(MOV_FILE) ||
                fileName.endsWith(WMV_FILE) || fileName.endsWith(THREE_GP_FILE) || fileName.endsWith(MPEG_FILE) ||
                fileName.endsWith(MP3_FILE) || fileName.endsWith(WAV_FILE) || fileName.endsWith(OGG_FILE) ||
                fileName.endsWith(M4A_FILE) || fileName.endsWith(FLAC_FILE) || fileName.endsWith(AAC_FILE) ||
                fileName.endsWith(PNG_FILE) || fileName.endsWith(JPG_FILE) || fileName.endsWith(JPEG_FILE) ||
                fileName.endsWith(GIF_FILE) || fileName.endsWith(BMP_FILE) || fileName.endsWith(WEBP_FILE) ||
                fileName.endsWith(TIFF_FILE) || fileName.endsWith(ICO_FILE)
    }

    private fun getIconResource(fileName: String): Int {
        return when {
            // HTML, CSS, JS, and Text files
            fileName.endsWith(HTML_FILE) -> R.drawable.html_ic
            fileName.endsWith(CSS_FILE) -> R.drawable.css_ic
            fileName.endsWith(JS_FILE) -> R.drawable.js_ic
            fileName.endsWith(SCSS_FILE) -> R.drawable.scss_ic
            fileName.endsWith(TEXT_FILE) -> R.drawable.file_ic

            // Video files
            fileName.endsWith(MP4_FILE) || fileName.endsWith(WEBM_FILE) || fileName.endsWith(MKV_FILE) ||
                    fileName.endsWith(FLV_FILE) || fileName.endsWith(AVI_FILE) || fileName.endsWith(
                MOV_FILE) ||
                    fileName.endsWith(WMV_FILE) || fileName.endsWith(THREE_GP_FILE) || fileName.endsWith(MPEG_FILE) ->
                R.drawable.baseline_slow_motion_video_24

            // Audio files
            fileName.endsWith(MP3_FILE) || fileName.endsWith(WAV_FILE) || fileName.endsWith(OGG_FILE) ||
                    fileName.endsWith(M4A_FILE) || fileName.endsWith(FLAC_FILE) || fileName.endsWith(AAC_FILE) ->
                R.drawable.baseline_audiotrack_24

            // Image files
            fileName.endsWith(PNG_FILE) || fileName.endsWith(JPG_FILE) || fileName.endsWith(JPEG_FILE) ||
                    fileName.endsWith(GIF_FILE) || fileName.endsWith(BMP_FILE) || fileName.endsWith(WEBP_FILE) ||
                    fileName.endsWith(TIFF_FILE) || fileName.endsWith(ICO_FILE) ->
                R.drawable.baseline_image_24

            // Default icon for other file types
            else -> R.drawable.file_ic
        }
    }

    fun updateData(updatedList: List<File>) {
        this.list = updatedList
        refresh()
    }
    private fun refresh() {
        notifyDataSetChanged()
    }
}
