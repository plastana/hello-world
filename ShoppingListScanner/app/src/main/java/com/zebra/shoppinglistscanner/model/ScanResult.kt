package com.zebra.shoppinglistscanner.model

/**
 * Risultato di una scansione barcode
 */
data class ScanResult(
    val barcode: String,
    val timestamp: Long = System.currentTimeMillis(),
    val scanType: ScanType = ScanType.BARCODE
)

enum class ScanType {
    BARCODE,
    OCR,
    MANUAL
}

/**
 * Risultato del controllo di un articolo scansionato
 */
data class ItemCheckResult(
    val found: Boolean,
    val item: ShoppingItem? = null,
    val message: String = "",
    val suggestedAction: ActionType = ActionType.NONE
)

enum class ActionType {
    NONE,
    ADD_QUANTITY,
    COMPLETE_ITEM,
    ADD_NEW_ITEM,
    WRONG_ITEM,
    UNIT_CONVERSION_NEEDED
}