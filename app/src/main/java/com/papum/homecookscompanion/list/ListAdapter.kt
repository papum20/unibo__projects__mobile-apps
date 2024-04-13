package com.papum.homecookscompanion.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.papum.homecookscompanion.R


class ListAdapter(private val items:MutableList<ListItem>) : Adapter<ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_card_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.apply {
            tvItemName.text = items[position].name
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(newItem:ListItem) {
        items.add(newItem)
        notifyItemInserted(items.size - 1)
    }

    fun deleteItem(name:String) {
        val index = items.indexOf(
            items.find { item -> item.name == name }
        )
        items.removeAt(index)
        notifyItemRemoved(index)
    }

}