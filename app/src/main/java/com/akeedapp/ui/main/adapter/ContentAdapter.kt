package com.akeedapp.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.akeedapp.R
import com.akeedapp.data.ContentModel
import com.akeedapp.databinding.RawContentBinding
import com.akeedapp.utility.OnItemClickListner
import com.bumptech.glide.Glide
import java.util.*

class ContentAdapter(
    private val context: Context,
    private var arylstContent: ArrayList<ContentModel>
) :
    RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    lateinit var onItemClickListner: OnItemClickListner

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RawContentBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.raw_content,
            parent,
            false
        )
        return ViewHolder(
            context,
            binding
        )
    }

    override fun getItemCount() = arylstContent.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = arylstContent.get(position)
        holder.itemView.setOnClickListener {
            onItemClickListner.onItemClickListner(position)
        }
        holder.bindItems(content)
    }

    fun setContent(arylstContent: ArrayList<ContentModel>) {
        this.arylstContent = arylstContent
        notifyDataSetChanged()
    }

    fun addContent(arylstContent: ArrayList<ContentModel>) {
        this.arylstContent.addAll(arylstContent)
        notifyDataSetChanged()
    }

    class ViewHolder(val context: Context, val binding: RawContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val calYear = Calendar.getInstance().get(Calendar.YEAR)

        fun bindItems(content: ContentModel) {
            if (!content.Poster.equals("N/A", true)) {
                Glide.with(context).load(content.Poster).centerCrop()
                    .placeholder(R.drawable.placeholder_for_missing_posters)
                    .into(binding.imgvwContent)
            } else {
                Glide.with(context).load(R.drawable.placeholder_for_missing_posters).centerCrop()
                    .into(binding.imgvwContent)
            }

            if (!content.Year.isNullOrEmpty()) {
                try {
                    val year = calYear - content.Year!!.takeLast(4).toInt()
                    binding.txtvwYear.text = "$year years ago"
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    binding.txtvwYear.text = "${content.Year}"
                }

            }

            binding.content = content
            binding.executePendingBindings()
        }
    }
}