package com.zebra.shoppinglistscanner.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modello dati per un articolo della lista spesa
 * Corrisponde alla struttura del CSV dal gestionale SIGAO
 */
@Parcelize
data class ShoppingItem(
    val codiceArticolo: String,
    val descrizione: String,
    val codiceFornitore: String,
    val barcode: String,
    val ubicazione: String,
    val quantitaDaPrelevare: Double,
    val unitaMisura: String,
    var quantitaPrelevata: Double = 0.0,
    val unitaMisuraPrincipale: String,
    val valorePrincipale: Double,
    val unitaMisuraSecondaria: String,
    val valoreSecondaria: Double,
    val destinazione: String,
    val note: String = "",
    var isCompleted: Boolean = false,
    var isScanned: Boolean = false
) : Parcelable {
    
    /**
     * Calcola la quantità rimanente da prelevare
     */
    fun getQuantitaRimanente(): Double {
        return quantitaDaPrelevare - quantitaPrelevata
    }
    
    /**
     * Verifica se l'articolo è completato
     */
    fun isItemCompleted(): Boolean {
        return quantitaPrelevata >= quantitaDaPrelevare
    }
    
    /**
     * Converte la quantità dall'unità secondaria a quella principale
     */
    fun convertToMainUnit(quantita: Double, fromSecondary: Boolean = false): Double {
        return if (fromSecondary && valoreSecondaria > 0) {
            quantita * valoreSecondaria
        } else {
            quantita
        }
    }
    
    /**
     * Aggiunge quantità prelevata con controllo unità di misura
     */
    fun addQuantitaPrelevata(quantita: Double, unitaMisuraScansionata: String): Double {
        val quantitaConvertita = when (unitaMisuraScansionata.lowercase()) {
            unitaMisuraSecondaria.lowercase() -> convertToMainUnit(quantita, true)
            unitaMisuraPrincipale.lowercase() -> quantita
            else -> quantita // Default: assume unità principale
        }
        
        quantitaPrelevata += quantitaConvertita
        isScanned = true
        isCompleted = isItemCompleted()
        
        return quantitaConvertita
    }
    
    /**
     * Restituisce una stringa formattata per la visualizzazione
     */
    fun getDisplayInfo(): String {
        return "$descrizione\n" +
                "Codice: $codiceArticolo\n" +
                "Ubicazione: $ubicazione\n" +
                "Da prelevare: $quantitaDaPrelevare $unitaMisura\n" +
                "Prelevato: $quantitaPrelevata $unitaMisura"
    }
}