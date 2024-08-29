package com.nasahacker.nasaeditor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nasahacker.nasaeditor.R
import com.nasahacker.nasaeditor.databinding.ProjectItemBinding
import com.nasahacker.nasaeditor.listener.OnClickListener

class ProjectAdapter(
    private val context: Context,
    private var list: List<String>,
    private val listener: OnClickListener<String>
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {


    inner class ProjectViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding = ProjectItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        return ProjectViewHolder(
            LayoutInflater.from(context).inflate(R.layout.project_item, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.binding.tvProjectName.text = list[position]
        holder.itemView.setOnClickListener {
            listener.onClick(holder.binding.tvProjectName.text.toString())
        }
        holder.itemView.setOnLongClickListener {
            listener.onLongPress(holder.binding.tvProjectName.text.toString())
            true
        }
    }

    fun updateData(data: List<String>) {
        list = data
        notifyDataSetChanged()
    }
}