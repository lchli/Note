package com.lch.note

import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.NoteContentImgItemBinding
import com.materialstudies.owl.databinding.NoteContentTextItemBinding

class NoteContentAdapter(val mEditNoteViewModel:EditNoteViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listData: List<Any>? = null

    fun setListData(datas: List<Any>) {
        listData = ArrayList(datas)
        //notifyItemRangeChanged(0,listData?.size?:0)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.note_content_img_item -> return ImgHolder(NoteContentImgItemBinding.inflate(LayoutInflater.from(parent.context)))
            else -> return TextHolder(NoteContentTextItemBinding.inflate(LayoutInflater.from(parent.context)))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = listData?.get(position) ?: return
        when(holder){
            is TextHolder->{
                holder.bind(data as? NoteContentText,position)
            }

            is ImgHolder->{
                holder.bind(data as? NoteContentImg,position)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val def = R.layout.note_content_text_item

        if (listData.isNullOrEmpty()) {
            return def
        }
        if (position < 0 || position >= listData!!.size) {
            return def
        }
        val data = listData?.get(position) ?: return def
        if (data is NoteContentImg) {
            return R.layout.note_content_img_item
        }

        return def

    }

    override fun getItemCount(): Int {
        return listData?.size ?: 0
    }


    inner class TextHolder(val binding: NoteContentTextItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteContentText?,position:Int) {
            data?.apply {
                binding.edit.setText(text)
                binding.moreOp.setOnClickListener {
                    showMenu(it,R.menu.content_ele_op,position)
                }
            }

        }

    }

    inner class ImgHolder(val binding: NoteContentImgItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteContentImg?,position:Int) {
            data?.apply {
                //binding.img.setImageURI(Uri.parse(resPath))
                Glide.with(binding.img).load(resPath).into(binding.img)
                binding.moreOp.setOnClickListener {
                    showMenu(it,R.menu.content_ele_op,position)
                }
            }

        }

    }

    private fun showMenu(v: View, @MenuRes menuRes: Int,position:Int) {
        val popup = PopupMenu(v.context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
           when(menuItem.itemId){
               R.id.option_del->{
                   mEditNoteViewModel.del(v.context,position)
               }
               R.id.option_after->{
                   mEditNoteViewModel.insertAfter(position+1)
               }
               R.id.option_before->{
                   mEditNoteViewModel.insertBefore(position)
               }
           }
           return@setOnMenuItemClickListener true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }


}