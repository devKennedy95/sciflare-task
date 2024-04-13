package com.task.sciflare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.sciflare.ui.listener.OnItemClick
import com.task.sciflare.databinding.LayoutMessageItemBinding
import com.task.sciflare.models.MessageModel
import com.task.sciflare.ui.viewModel.MessageViewModel

class SmsListAdapter(
    private val list: List<MessageModel>,
    private val listener: OnItemClick<MessageModel>? = null,
) : RecyclerView.Adapter<SmsListAdapter.ViewHolder>() {


    class ViewHolder(
        private val binding: LayoutMessageItemBinding, private val listener: OnItemClick<MessageModel>?
    ) : RecyclerView.ViewHolder(binding.root) {

        private val viewModel = MessageViewModel()
        fun bind(
            item: MessageModel,
        ) {
            binding.viewModel = viewModel
            viewModel.bind(item)
            binding.locationCard.setOnClickListener {
                listener?.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutMessageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ), listener
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}
