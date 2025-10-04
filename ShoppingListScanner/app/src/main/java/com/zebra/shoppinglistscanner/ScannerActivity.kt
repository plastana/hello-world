package com.zebra.shoppinglistscanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.zebra.shoppinglistscanner.databinding.ActivityScannerBinding

class ScannerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityScannerBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        startBarcodeScanner()
    }
    
    private fun setupUI() {
        binding.btnManualInput.setOnClickListener {
            // TODO: Implementare input manuale
            showManualInputDialog()
        }
        
        binding.btnScanAgain.setOnClickListener {
            startBarcodeScanner()
        }
        
        binding.btnClose.setOnClickListener {
            finish()
        }
    }
    
    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Inquadra il barcode dell'articolo")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.setOrientationLocked(true)
        integrator.initiateScan()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scansione annullata", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // Scansione riuscita
                val barcode = result.contents
                binding.tvScanResult.text = "Barcode: $barcode"
                
                // Restituisci il risultato alla MainActivity
                val resultIntent = Intent().apply {
                    putExtra("SCAN_RESULT", barcode)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    private fun showManualInputDialog() {
        // TODO: Implementare dialog per input manuale
        Toast.makeText(this, "Input manuale non ancora implementato", Toast.LENGTH_SHORT).show()
    }
}