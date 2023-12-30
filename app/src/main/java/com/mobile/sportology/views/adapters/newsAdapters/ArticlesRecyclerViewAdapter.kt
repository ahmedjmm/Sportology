package com.mobile.sportology.views.adapters.newsAdapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sportology.R
import com.mobile.sportology.databinding.ArticleItemBinding
import com.mobile.sportology.models.news.News

class ArticlesRecyclerViewAdapter(private val onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<ArticlesRecyclerViewAdapter.ViewHolder>() {
    lateinit var context: Context

    private val _diffCallback = object : DiffUtil.ItemCallback<News.Article>() {
        override fun areItemsTheSame(
            oldItem: News.Article,
            newItem: News.Article
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: News.Article,
            newItem: News.Article
        ): Boolean = oldItem == newItem
    }
    val differ = AsyncListDiffer(this, _diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticlesRecyclerViewAdapter.ViewHolder = ViewHolder(ArticleItemBinding.inflate(
        LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: ArticlesRecyclerViewAdapter.ViewHolder,
        position: Int
    ) = holder.bind(differ.currentList[position])

    override fun getItemCount() = differ.currentList.size

    inner class ViewHolder(
        private val _articleItem: ArticleItemBinding
    ) : RecyclerView.ViewHolder(_articleItem.root) {
        init {
            _articleItem.share.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, differ.currentList[layoutPosition].url)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
            _articleItem.root.setOnClickListener {
                onItemClickListener.onItemClick(differ.currentList[layoutPosition])
            }
        }

        fun bind(item: News.Article) {
            _articleItem.article = item
            _articleItem.author.text = "${context.getString(R.string.author)} ${item.author}"
            _articleItem.source.text = "${context.getString(R.string.source)} ${item.source?.name}"
            _articleItem.content.text = item.content

        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: News.Article)
    }
}