package com.zebra.shoppinglistscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zebra.shoppinglistscanner.model.ActionType
import com.zebra.shoppinglistscanner.model.ItemCheckResult
import com.zebra.shoppinglistscanner.model.ShoppingItem

class ShoppingListViewModel : ViewModel() {
    
    private val _shoppingItems = MutableLiveData<List<ShoppingItem>>()
    val shoppingItems: LiveData<List<ShoppingItem>> = _shoppingItems
    
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _scanResult = MutableLiveData<ItemCheckResult>()
    val scanResult: LiveData<ItemCheckResult> = _scanResult
    
    /**
     * Carica una nuova lista della spesa
     */
    fun loadShoppingList(items: List<ShoppingItem>) {
        _isLoading.value = true
        _shoppingItems.value = items
        _isLoading.value = false
        _message.value = "Lista caricata con ${items.size} articoli"
    }
    
    /**
     * Processa il risultato di una scansione
     */
    fun processScanResult(barcode: String) {
        val currentItems = _shoppingItems.value ?: return
        
        // Cerca l'articolo per barcode
        val foundItem = currentItems.find { it.barcode == barcode }
        
        if (foundItem != null) {
            // Articolo trovato
            if (foundItem.isCompleted) {
                _scanResult.value = ItemCheckResult(
                    found = true,
                    item = foundItem,
                    message = "Articolo già completato",
                    suggestedAction = ActionType.COMPLETE_ITEM
                )
            } else {
                _scanResult.value = ItemCheckResult(
                    found = true,
                    item = foundItem,
                    message = "Articolo trovato: ${foundItem.descrizione}",
                    suggestedAction = ActionType.ADD_QUANTITY
                )
            }
        } else {
            // Articolo non trovato
            _scanResult.value = ItemCheckResult(
                found = false,
                message = "Articolo non trovato nella lista",
                suggestedAction = ActionType.ADD_NEW_ITEM
            )
        }
    }
    
    /**
     * Aggiunge quantità a un articolo
     */
    fun addQuantityToItem(item: ShoppingItem, quantity: Double, unitaMisura: String = "") {
        val currentItems = _shoppingItems.value?.toMutableList() ?: return
        
        val index = currentItems.indexOfFirst { it.codiceArticolo == item.codiceArticolo }
        if (index != -1) {
            val updatedItem = currentItems[index]
            val actualUnit = unitaMisura.ifEmpty { updatedItem.unitaMisura }
            
            updatedItem.addQuantitaPrelevata(quantity, actualUnit)
            currentItems[index] = updatedItem
            
            _shoppingItems.value = currentItems
            
            val message = if (updatedItem.isCompleted) {
                "Articolo completato: ${updatedItem.descrizione}"
            } else {
                "Aggiunta quantità: $quantity $actualUnit"
            }
            _message.value = message
        }
    }
    
    /**
     * Aggiunge un nuovo articolo alla lista
     */
    fun addNewItem(item: ShoppingItem) {
        val currentItems = _shoppingItems.value?.toMutableList() ?: mutableListOf()
        currentItems.add(item)
        _shoppingItems.value = currentItems
        _message.value = "Nuovo articolo aggiunto: ${item.descrizione}"
    }
    
    /**
     * Rimuove un articolo dalla lista
     */
    fun removeItem(item: ShoppingItem) {
        val currentItems = _shoppingItems.value?.toMutableList() ?: return
        currentItems.removeAll { it.codiceArticolo == item.codiceArticolo }
        _shoppingItems.value = currentItems
        _message.value = "Articolo rimosso: ${item.descrizione}"
    }
    
    /**
     * Resetta tutti i dati
     */
    fun resetList() {
        _shoppingItems.value = emptyList()
        _message.value = "Lista resettata"
    }
    
    /**
     * Resetta solo le quantità prelevate
     */
    fun resetQuantities() {
        val currentItems = _shoppingItems.value?.map { item ->
            item.copy(
                quantitaPrelevata = 0.0,
                isCompleted = false,
                isScanned = false
            )
        } ?: return
        
        _shoppingItems.value = currentItems
        _message.value = "Quantità resettate"
    }
    
    /**
     * Ottiene statistiche della lista
     */
    fun getStatistics(): Map<String, Int> {
        val items = _shoppingItems.value ?: return emptyMap()
        
        return mapOf(
            "total" to items.size,
            "completed" to items.count { it.isCompleted },
            "scanned" to items.count { it.isScanned },
            "pending" to items.count { !it.isScanned }
        )
    }
    
    /**
     * Filtra articoli per stato
     */
    fun getItemsByStatus(completed: Boolean? = null, scanned: Boolean? = null): List<ShoppingItem> {
        val items = _shoppingItems.value ?: return emptyList()
        
        return items.filter { item ->
            (completed == null || item.isCompleted == completed) &&
            (scanned == null || item.isScanned == scanned)
        }
    }
    
    /**
     * Cerca articoli per testo
     */
    fun searchItems(query: String): List<ShoppingItem> {
        val items = _shoppingItems.value ?: return emptyList()
        
        return if (query.isBlank()) {
            items
        } else {
            items.filter { item ->
                item.descrizione.contains(query, ignoreCase = true) ||
                item.codiceArticolo.contains(query, ignoreCase = true) ||
                item.ubicazione.contains(query, ignoreCase = true) ||
                item.barcode.contains(query, ignoreCase = true)
            }
        }
    }
}