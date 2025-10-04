package com.zebra.shoppinglistscanner.barcode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Receiver per gestire i dati dal DataWedge di Zebra
 * Questo è specifico per i dispositivi Zebra come il TC21
 */
class DataWedgeReceiver : BroadcastReceiver() {
    
    companion object {
        const val INTENT_ACTION = "com.zebra.shoppinglistscanner.SCAN"
        const val INTENT_CATEGORY = "android.intent.category.DEFAULT"
        
        // Chiavi per i dati DataWedge
        const val SOURCE_TAG = "com.symbol.datawedge.source"
        const val LABEL_TYPE_TAG = "com.symbol.datawedge.label_type"
        const val DATA_STRING_TAG = "com.symbol.datawedge.data_string"
        const val DECODE_DATA_TAG = "com.symbol.datawedge.decode_data"
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        
        val action = intent.action
        if (INTENT_ACTION == action) {
            
            // Estrai i dati dalla scansione
            val source = intent.getStringExtra(SOURCE_TAG)
            val labelType = intent.getStringExtra(LABEL_TYPE_TAG)
            val dataString = intent.getStringExtra(DATA_STRING_TAG)
            
            // Dati decodificati (per barcode più complessi)
            val decodeDataBundle = intent.getBundleExtra(DECODE_DATA_TAG)
            
            if (dataString != null && dataString.isNotEmpty()) {
                // Invia il risultato della scansione all'activity corrente
                val scanIntent = Intent("com.zebra.shoppinglistscanner.SCAN_RESULT").apply {
                    putExtra("SCAN_DATA", dataString)
                    putExtra("SCAN_TYPE", labelType)
                    putExtra("SCAN_SOURCE", source)
                    
                    // Aggiungi dati decodificati se disponibili
                    decodeDataBundle?.let { bundle ->
                        putExtra("DECODE_DATA", bundle)
                    }
                }
                
                // Invia broadcast locale
                context.sendBroadcast(scanIntent)
            }
        }
    }
}