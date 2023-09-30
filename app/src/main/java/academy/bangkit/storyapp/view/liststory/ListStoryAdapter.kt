package academy.bangkit.storyapp.view.liststory

import academy.bangkit.storyapp.R
import academy.bangkit.storyapp.data.response.Story
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListStoryAdapter(private val listStory: ArrayList<Story>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_story, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (photo, name) = listStory[position]
        // TODO: Glide load pic from Internet 
//        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listStory[holder.adapterPosition]) }
    }

    interface OnItemClickCallback {
        fun onItemClicked(story: Story)
    }
}