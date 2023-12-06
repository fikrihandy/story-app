package academy.bangkit.storyApp.view.listStory

import academy.bangkit.storyApp.data.response.Story
import academy.bangkit.storyApp.databinding.ItemRowStoryBinding
import academy.bangkit.storyApp.view.extension.loadImageWithGlide
import academy.bangkit.storyApp.view.storyDetail.DetailStoryActivity
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class StoryListAdapter :
    PagingDataAdapter<Story, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                val intentToDetail =
                    Intent(holder.itemView.context as Activity, DetailStoryActivity::class.java)
                        .putExtra(DetailStoryActivity.ID_STORY, data.id)
                holder.itemView.context.startActivity(intentToDetail)
            }
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            binding.tvItemName.text = data.name
            binding.ivItemPhoto.loadImageWithGlide(data.photoUrl)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}