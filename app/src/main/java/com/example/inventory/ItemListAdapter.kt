package com.example.inventory


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.data.Item
import com.example.inventory.data.getFormattedPrice
import com.example.inventory.databinding.ItemListItemBinding

class ItemListAdapter(private val onItemClicked: (Item) -> Unit )
    : ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallback) {

    inner class ItemViewHolder(private var binding: ItemListItemBinding) :
        RecyclerView.ViewHolder(binding.root){

            fun bind(item: Item){
                binding.apply {
                    itemName.text = item.itemName
                    itemPrice.text = item.getFormattedPrice()
                    itemQuantity.text = item.quantityInStock.toString()
                }
            }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        //SE CREA EL VIEWHOLDER, Y SE ASOCIA A SU PLANTILLA DE DISEÑO XML
        return ItemViewHolder(
            ItemListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )

    }

    //ESTO SE LLAMA CADA VEZ QUE EL RECYCLER VIEW SE DESPLAZA
    //O SE ACTUALIZA
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem: Item = getItem( position )

        holder.itemView.setOnClickListener {
            onItemClicked(currentItem)
        }

        holder.bind(currentItem)

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.itemName == newItem.itemName
        }
    }
}