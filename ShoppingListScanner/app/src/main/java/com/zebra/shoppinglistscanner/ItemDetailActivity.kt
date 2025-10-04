package com.zebra.shoppinglistscanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zebra.shoppinglistscanner.databinding.ActivityItemDetailBinding
import com.zebra.shoppinglistscanner.model.ShoppingItem

class ItemDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var item: ShoppingItem
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Recupera l'articolo dai parametri
        item = intent.getParcelableExtra("ITEM") ?: run {
            Toast.makeText(this, "Errore: articolo non trovato", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI()
        displayItemDetails()
    }
    
    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnAddQuantity.setOnClickListener {
            showAddQuantityDialog()
        }
        
        binding.btnCompleteItem.setOnClickListener {
            completeItem()
        }
        
        binding.btnScanBarcode.setOnClickListener {
            // TODO: Avvia scanner per questo articolo specifico
            Toast.makeText(this, "Scanner non ancora implementato", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun displayItemDetails() {
        binding.apply {
            tvItemCode.text = item.codiceArticolo
            tvItemDescription.text = item.descrizione
            tvItemLocation.text = item.ubicazione
            tvItemBarcode.text = item.barcode
            tvItemSupplierCode.text = item.codiceFornitore
            tvItemDestination.text = item.destinazione
            
            tvQuantityToPick.text = "${item.quantitaDaPrelevare} ${item.unitaMisura}"
            tvQuantityPicked.text = "${item.quantitaPrelevata} ${item.unitaMisura}"
            tvQuantityRemaining.text = "${item.getQuantitaRimanente()} ${item.unitaMisura}"
            
            // Unità di misura
            tvMainUnit.text = "${item.unitaMisuraPrincipale} (${item.valorePrincipale})"
            tvSecondaryUnit.text = "${item.unitaMisuraSecondaria} (${item.valoreSecondaria})"
            
            // Note
            if (item.note.isNotEmpty()) {
                tvItemNotes.text = item.note
            } else {
                tvItemNotes.text = "Nessuna nota"
            }
            
            // Stato
            val statusText = when {
                item.isCompleted -> "✅ Completato"
                item.isScanned -> "🔍 Scansionato"
                else -> "⏳ In attesa"
            }
            tvItemStatus.text = statusText
            
            // Abilita/disabilita pulsanti
            btnAddQuantity.isEnabled = !item.isCompleted
            btnCompleteItem.isEnabled = !item.isCompleted
        }
    }
    
    private fun showAddQuantityDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_quantity, null)
        
        AlertDialog.Builder(this)
            .setTitle("Aggiungi Quantità")
            .setView(dialogView)
            .setPositiveButton("Aggiungi") { _, _ ->
                // TODO: Implementare logica aggiunta quantità
                Toast.makeText(this, "Funzionalità in sviluppo", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
    
    private fun completeItem() {
        AlertDialog.Builder(this)
            .setTitle("Completa Articolo")
            .setMessage("Vuoi completare questo articolo con la quantità richiesta?")
            .setPositiveButton("Sì") { _, _ ->
                // Completa l'articolo
                item.quantitaPrelevata = item.quantitaDaPrelevare
                item.isCompleted = true
                item.isScanned = true
                
                displayItemDetails()
                Toast.makeText(this, "Articolo completato", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}