package com.zebra.shoppinglistscanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zebra.shoppinglistscanner.R
import com.zebra.shoppinglistscanner.databinding.ItemShoppingListBinding
import com.zebra.shoppinglistscanner.model.ShoppingItem

class ShoppingListAdapter(
    private val onItemClick: (ShoppingItem) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {
    
    private var items = listOf<ShoppingItem>()
    
    fun updateItems(newItems: List<ShoppingItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
    
    override fun getItemCount(): Int = items.size
    
    inner class ViewHolder(
        private val binding: ItemShoppingListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: ShoppingItem) {
            binding.apply {
                // Dati principali
                tvDescrizione.text = item.descrizione
                tvCodice.text = item.codiceArticolo
                tvUbicazione.text = "📍 ${item.ubicazione}"
                tvQuantita.text = "Da prelevare: ${item.quantitaDaPrelevare} ${item.unitaMisura}"
                tvQuantitaPrelevata.text = "Prelevato: ${item.quantitaPrelevata} ${item.unitaMisura}"
                
                // Note (mostra solo se presenti)
                if (item.note.isNotEmpty()) {
                    tvNote.text = "Note: ${item.note}"
                    tvNote.visibility = View.VISIBLE
                } else {
                    tvNote.visibility = View.GONE
                }
                
                // Indicatore di stato
                val statusColor = when {
                    item.isCompleted -> ContextCompat.getColor(root.context, android.R.color.holo_green_dark)
                    item.isScanned -> ContextCompat.getColor(root.context, android.R.color.holo_orange_dark)
                    else -> ContextCompat.getColor(root.context, android.R.color.holo_red_dark)
                }
                statusIndicator.setBackgroundColor(statusColor)
                
                // Stile del testo in base allo stato
                val textAlpha = if (item.isCompleted) 0.6f else 1.0f
                tvDescrizione.alpha = textAlpha
                tvQuantita.alpha = textAlpha
                tvQuantitaPrelevata.alpha = textAlpha
                
                // Click listeners
                root.setOnClickListener { onItemClick(item) }
                btnItemAction.setOnClickListener { onItemClick(item) }
                
                // Icona del pulsante azione
                val actionIcon = when {
                    item.isCompleted -> android.R.drawable.ic_menu_view
                    item.isScanned -> android.R.drawable.ic_menu_edit
                    else -> android.R.drawable.ic_menu_camera
                }
                btnItemAction.setImageResource(actionIcon)
            }
        }
    }
}