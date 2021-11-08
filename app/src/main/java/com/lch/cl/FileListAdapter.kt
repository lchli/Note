/*
 *   Copyright (c) 2019 Google Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the License
 *
 *   is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing permissions and limitations under
 *   the License.
 */

package com.lch.cl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lch.cln.R
import com.lch.cln.databinding.CourseItemBinding
import com.lch.cl.util.ShapeAppearanceTransformation

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

