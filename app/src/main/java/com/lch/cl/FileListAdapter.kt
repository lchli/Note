

package com.lch.cl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lch.cl.util.ShapeAppearanceTransformation
import com.lch.cln.R
import com.lch.cln.databinding.CourseItemBinding

class MyCoursesAdapter(val vm: FileListVm) : ListAdapter<String, MyCoursesAdapter.MyCourseViewHolder>(
    CourseDiff2(vm)
) {

     class CourseDiff2(private val vm: FileListVm) : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            // return File(oldItem).name == File(newItem).name
            return oldItem == newItem && vm.isChecked(oldItem)==vm.isChecked(newItem)
        }
    }

    private object onClick : CourseViewClick {
        override fun onClick(view: View, courseId: String) {
            val extras = FragmentNavigatorExtras(
                view to "shared_element"
            )
            val action = FileListFragmentDirections.actionOnboardingToLearn(courseId)
            view.findNavController().navigate(action, extras)
        }
    }

    private val shapeTransform =
        ShapeAppearanceTransformation(R.style.ShapeAppearance_Owl_SmallComponent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCourseViewHolder {
        return MyCourseViewHolder(
            CourseItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyCourseViewHolder, position: Int) {

        getItem(position)?.apply {
            holder.bind(this, shapeTransform, onClick)
        }
    }


    inner class MyCourseViewHolder(
        private val binding: CourseItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            course: String,
            imageTransform: ShapeAppearanceTransformation,
            onClick: CourseViewClick
        ) {
            binding.run {
                this.state = FileListUiState(course,vm)

                executePendingBindings()
            }
        }
    }

}

interface CourseViewClick {
    fun onClick(view: View, courseId: String)
}

