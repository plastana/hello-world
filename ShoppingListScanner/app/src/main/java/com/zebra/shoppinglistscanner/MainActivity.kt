package com.zebra.shoppinglistscanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zebra.shoppinglistscanner.adapter.ShoppingListAdapter
import com.zebra.shoppinglistscanner.databinding.ActivityMainBinding
import com.zebra.shoppinglistscanner.model.ShoppingItem
import com.zebra.shoppinglistscanner.utils.CsvHelper
import com.zebra.shoppinglistscanner.viewmodel.ShoppingListViewModel
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var adapter: ShoppingListAdapter
    
    // Request codes per i permessi
    private val PERMISSION_REQUEST_CODE = 100
    
    // ActivityResultLauncher per selezionare file CSV
    private val csvFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                loadCsvFromUri(uri)
            }
        }
    }
    
    // ActivityResultLauncher per la scansione
    private val scanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val barcode = result.data?.getStringExtra("SCAN_RESULT")
            barcode?.let { 
                viewModel.processScanResult(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inizializza ViewModel
        viewModel = ViewModelProvider(this)[ShoppingListViewModel::class.java]
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup observers
        setupObservers()
        
        // Setup click listeners
        setupClickListeners()
        
        // Richiedi permessi
        requestPermissions()
    }
    
    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter { item ->
            // Click su un articolo - mostra dettagli
            showItemDetail(item)
        }
        
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItems.adapter = adapter
    }
    
    private fun setupObservers() {
        // Osserva la lista degli articoli
        viewModel.shoppingItems.observe(this) { items ->
            adapter.updateItems(items)
            updateUI(items)
        }
        
        // Osserva i messaggi
        viewModel.message.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        
        // Osserva lo stato di caricamento
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnLoadCsv.isEnabled = !isLoading
            binding.btnStartScan.isEnabled = !isLoading && viewModel.shoppingItems.value?.isNotEmpty() == true
        }
    }
    
    private fun setupClickListeners() {
        binding.btnLoadCsv.setOnClickListener {
            openCsvFilePicker()
        }
        
        binding.btnStartScan.setOnClickListener {
            startScanning()
        }
        
        binding.btnExportCsv.setOnClickListener {
            exportCsvFile()
        }
        
        binding.btnReset.setOnClickListener {
            showResetConfirmation()
        }
    }
    
    private fun updateUI(items: List<ShoppingItem>) {
        val totalItems = items.size
        val completedItems = items.count { it.isCompleted }
        val progress = if (totalItems > 0) (completedItems * 100) / totalItems else 0
        
        binding.tvItemCount.text = "$completedItems/$totalItems articoli"
        binding.tvProgress.text = "Progresso: $progress%"
        binding.progressBar.progress = progress
        
        binding.btnExportCsv.isEnabled = totalItems > 0
        binding.btnStartScan.isEnabled = totalItems > 0
    }
    
    private fun openCsvFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "text/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        csvFileLauncher.launch(intent)
    }
    
    private fun loadCsvFromUri(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val items = CsvHelper.readShoppingListFromCsv(inputStream)
                viewModel.loadShoppingList(items)
                Toast.makeText(this, "Caricati ${items.size} articoli", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Errore caricamento CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun startScanning() {
        val intent = Intent(this, ScannerActivity::class.java)
        scanLauncher.launch(intent)
    }
    
    private fun exportCsvFile() {
        val items = viewModel.shoppingItems.value ?: return
        
        try {
            val fileName = CsvHelper.generateOutputFileName()
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                CsvHelper.writeShoppingListToCsv(items, outputStream)
            }
            
            Toast.makeText(this, "File esportato: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Errore esportazione: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showItemDetail(item: ShoppingItem) {
        val intent = Intent(this, ItemDetailActivity::class.java).apply {
            putExtra("ITEM", item)
        }
        startActivity(intent)
    }
    
    private fun showResetConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Reset Lista")
            .setMessage("Sei sicuro di voler resettare tutti i dati?")
            .setPositiveButton("Sì") { _, _ ->
                viewModel.resetList()
            }
            .setNegativeButton("No", null)
            .show()
    }
    
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }
            
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Alcuni permessi sono necessari per il funzionamento dell'app",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}