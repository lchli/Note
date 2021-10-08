package com.lch.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.owl.databinding.NoteListItemBinding

class NoteListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listData: List<Any>? = null

    fun setListData(datas: List<Any>) {
        listData = ArrayList(datas)
        notifyItemRangeChanged(0,listData?.size?:0)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(NoteListItemBinding.inflate(LayoutInflater.from(parent.context)))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = listData?.get(position) ?: return
        (holder as? Holder)?.bind(data as? NoteMetaData)

    }



    override fun getItemCount(): Int {
        return listData?.size ?: 0
    }


    class Holder(val binding: NoteListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteMetaData?) {
            data?.apply {
               binding.noteMeta=this
                binding.executePendingBindings()
            }

        }

    }



}