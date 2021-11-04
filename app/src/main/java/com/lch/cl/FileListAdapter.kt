package com.lch.cl

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.owl.R
import java.io.File


class FileListAdapter : androidx.recyclerview.widget.ListAdapter<String, RecyclerView.ViewHolder>(
    DiffCallback()
) {

    private class DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return File(oldItem).name == File(newItem).name&&File(oldItem).length() == File(newItem).length()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Vh(View.inflate(parent.context, R.layout.file_list_item, null))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as? Vh ?: return
        val data = getItem(position) ?: return
        vh.bind(data)
    }

    private inner class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName = itemView.findViewById<TextView>(R.id.file_name)
        val file_size = itemView.findViewById<TextView>(R.id.file_size)
        val file_del = itemView.findViewById<TextView>(R.id.file_del)

        fun bind(data: String) {
            val formatedSize= CommonUtil.formatFileSize(File(data).length(), false)
            file_size.text="${formatedSize}"
            fileName.text="${File(data).name}"
            file_del.setOnClickListener {
                val res=File(data).delete()
                Toast.makeText(itemView.context,"del:${res}",Toast.LENGTH_LONG).show()
                if(res){
                   //FileScanner.del(data)
                }
            }
        }

    }
}