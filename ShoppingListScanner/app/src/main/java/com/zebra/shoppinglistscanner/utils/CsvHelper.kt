package com.zebra.shoppinglistscanner.utils

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import com.zebra.shoppinglistscanner.model.ShoppingItem
import java.io.*

/**
 * Helper per la gestione dei file CSV
 */
object CsvHelper {
    
    /**
     * Legge un file CSV e restituisce una lista di ShoppingItem
     */
    fun readShoppingListFromCsv(inputStream: InputStream): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        
        try {
            val reader = CSVReader(InputStreamReader(inputStream))
            val lines = reader.readAll()
            
            // Salta l'header se presente
            val dataLines = if (lines.isNotEmpty() && isHeaderLine(lines[0])) {
                lines.drop(1)
            } else {
                lines
            }
            
            for (line in dataLines) {
                if (line.size >= 12) { // Minimo numero di colonne richieste
                    try {
                        val item = ShoppingItem(
                            codiceArticolo = line[0].trim(),
                            descrizione = line[1].trim(),
                            codiceFornitore = line[2].trim(),
                            barcode = line[3].trim(),
                            ubicazione = line[4].trim(),
                            quantitaDaPrelevare = parseDouble(line[5]),
                            unitaMisura = line[6].trim(),
                            quantitaPrelevata = parseDouble(line[7]),
                            unitaMisuraPrincipale = line[8].trim(),
                            valorePrincipale = parseDouble(line[9]),
                            unitaMisuraSecondaria = line[10].trim(),
                            valoreSecondaria = parseDouble(line[11]),
                            destinazione = if (line.size > 12) line[12].trim() else "",
                            note = if (line.size > 13) line[13].trim() else ""
                        )
                        items.add(item)
                    } catch (e: Exception) {
                        // Log dell'errore ma continua con la riga successiva
                        println("Errore parsing riga CSV: ${line.joinToString(",")}")
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            throw Exception("Errore lettura file CSV: ${e.message}")
        }
        
        return items
    }
    
    /**
     * Scrive una lista di ShoppingItem in un file CSV
     */
    fun writeShoppingListToCsv(items: List<ShoppingItem>, outputStream: OutputStream) {
        try {
            val writer = CSVWriter(OutputStreamWriter(outputStream))
            
            // Scrivi header
            val header = arrayOf(
                "codice_articolo", "descrizione", "codice_fornitore", "barcode", 
                "ubicazione", "quantita_da_prelevare", "unita_misura", "quantita_prelevata",
                "unita_misura_principale", "valore_principale", 
                "unita_misura_secondaria", "valore_secondaria", "destinazione", "note"
            )
            writer.writeNext(header)
            
            // Scrivi dati
            for (item in items) {
                val line = arrayOf(
                    item.codiceArticolo,
                    item.descrizione,
                    item.codiceFornitore,
                    item.barcode,
                    item.ubicazione,
                    item.quantitaDaPrelevare.toString(),
                    item.unitaMisura,
                    item.quantitaPrelevata.toString(),
                    item.unitaMisuraPrincipale,
                    item.valorePrincipale.toString(),
                    item.unitaMisuraSecondaria,
                    item.valoreSecondaria.toString(),
                    item.destinazione,
                    item.note
                )
                writer.writeNext(line)
            }
            
            writer.close()
        } catch (e: Exception) {
            throw Exception("Errore scrittura file CSV: ${e.message}")
        }
    }
    
    /**
     * Verifica se una riga è un header
     */
    private fun isHeaderLine(line: Array<String>): Boolean {
        return line.isNotEmpty() && 
               (line[0].lowercase().contains("codice") || 
                line[0].lowercase().contains("article"))
    }
    
    /**
     * Parse sicuro di un double
     */
    private fun parseDouble(value: String): Double {
        return try {
            value.trim().replace(",", ".").toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
    
    /**
     * Genera un nome file con timestamp
     */
    fun generateOutputFileName(prefix: String = "lista_completata"): String {
        val timestamp = System.currentTimeMillis()
        return "${prefix}_${timestamp}.csv"
    }
}