package academy.bangkit.storyApp.view.listStory

import academy.bangkit.storyApp.data.response.Story
import academy.bangkit.storyApp.databinding.ItemRowStoryBinding
import academy.bangkit.storyApp.view.extension.loadImageWithGlide
import academy.bangkit.storyApp.view.storyDetail.DetailStoryActivity
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class StoryAdapter : ListAdapter<Story, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val intentToDetail =
                Intent(holder.itemView.context as Activity, DetailStoryActivity::class.java)
                    .putExtra(DetailStoryActivity.ID_STORY, item.id)
            holder.itemView.context.startActivity(intentToDetail)
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.ivItemPhoto.loadImageWithGlide(story.photoUrl)
            binding.tvItemName.text = story.name
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}