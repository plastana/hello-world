# Shopping List Scanner - App Android per Zebra TC21

## 📱 Descrizione
App Android sviluppata per palmari Zebra TC21 per la gestione di liste della spesa tramite scansione barcode. L'app permette di caricare liste CSV dal gestionale SIGAO, scansionare articoli, controllare quantità e unità di misura, ed esportare i risultati.

## 🎯 Funzionalità Principali

### ✅ Implementate nella versione base:
- **Caricamento file CSV** con struttura compatibile SIGAO
- **Interfaccia utente** ottimizzata per palmare
- **Gestione articoli** con visualizzazione dettagliata
- **Controllo quantità** e unità di misura
- **Esportazione CSV** dei risultati
- **Supporto scanner barcode** (ZXing)
- **Preparazione DataWedge** per Zebra TC21

### 🔄 Da implementare nelle prossime versioni:
- Integrazione completa DataWedge Zebra
- OCR come backup al barcode
- Sincronizzazione server remoto
- Aggiunta articoli non in lista
- Conversioni automatiche unità di misura avanzate

## 📋 Struttura CSV Supportata

Il file CSV deve contenere le seguenti colonne:
```
codice_articolo, descrizione, codice_fornitore, barcode, ubicazione, 
quantita_da_prelevare, unita_misura, quantita_prelevata, 
unita_misura_principale, valore_principale, 
unita_misura_secondaria, valore_secondaria, destinazione, note
```

### Esempio:
```csv
ART001,Pasta Barilla 500g,FOR001,8076809513524,A1-B2-C3,10,pz,0,pz,1.0,sc,12.0,Magazzino A,Controllare scadenza
```

## 🚀 Installazione e Setup

### Prerequisiti:
- Android Studio Arctic Fox o superiore
- Android SDK API 21+ (Android 5.0)
- Dispositivo Zebra TC21 o emulatore Android

### Passi per l'installazione:

1. **Clona/Scarica il progetto**
   ```bash
   # Se hai git installato
   git clone [repository-url]
   
   # Oppure scarica e estrai il file ZIP
   ```

2. **Apri in Android Studio**
   - Apri Android Studio
   - File → Open → Seleziona la cartella `ShoppingListScanner`
   - Attendi la sincronizzazione Gradle

3. **Configura il dispositivo**
   - Collega il Zebra TC21 via USB
   - Abilita "Opzioni sviluppatore" e "Debug USB"
   - Verifica che il dispositivo sia riconosciuto

4. **Compila e installa**
   - Build → Make Project
   - Run → Run 'app'

## 📱 Come Usare l'App

### 1. **Avvio e Caricamento Lista**
   - Apri l'app sul TC21
   - Tocca "Carica CSV"
   - Seleziona il file CSV della lista spesa
   - Verifica che gli articoli siano caricati correttamente

### 2. **Scansione Articoli**
   - Tocca "Scansiona" per avviare il lettore barcode
   - Inquadra il barcode dell'articolo
   - L'app mostrerà automaticamente le informazioni dell'articolo
   - Conferma o modifica la quantità prelevata

### 3. **Gestione Articoli**
   - Tocca un articolo nella lista per vedere i dettagli
   - Visualizza: ubicazione, quantità, unità di misura, note
   - Aggiungi quantità manualmente se necessario
   - Marca come completato quando finito

### 4. **Esportazione Risultati**
   - Tocca "Esporta CSV" quando hai finito
   - Il file viene salvato nella cartella Download
   - Trasferisci il file al server per il ricaricamento in SIGAO

## 🔧 Configurazione Zebra TC21

### DataWedge Setup (per versioni future):
1. Apri DataWedge sul TC21
2. Crea un nuovo profilo per l'app
3. Configura:
   - **Package**: `com.zebra.shoppinglistscanner`
   - **Activity**: `*` (tutte le activity)
   - **Intent Output**: Abilitato
   - **Intent Action**: `com.zebra.shoppinglistscanner.SCAN`

### Scanner Settings:
- **Decoder**: Tutti i formati barcode comuni
- **Illumination**: Auto
- **Beep**: Abilitato per conferma scansione

## 📂 Struttura del Progetto

```
ShoppingListScanner/
├── app/
│   ├── src/main/java/com/zebra/shoppinglistscanner/
│   │   ├── MainActivity.kt              # Activity principale
│   │   ├── ScannerActivity.kt           # Activity scansione
│   │   ├── ItemDetailActivity.kt        # Dettaglio articolo
│   │   ├── adapter/
│   │   │   └── ShoppingListAdapter.kt   # Adapter RecyclerView
│   │   ├── model/
│   │   │   ├── ShoppingItem.kt          # Modello dati articolo
│   │   │   └── ScanResult.kt            # Modello risultato scansione
│   │   ├── viewmodel/
│   │   │   └── ShoppingListViewModel.kt # ViewModel principale
│   │   ├── utils/
│   │   │   └── CsvHelper.kt             # Helper gestione CSV
│   │   └── barcode/
│   │       └── DataWedgeReceiver.kt     # Receiver DataWedge
│   └── src/main/res/
│       ├── layout/                      # File layout XML
│       ├── values/                      # Stringhe, colori, temi
│       └── xml/                         # Configurazioni
├── sample_data/
│   └── lista_spesa_esempio.csv          # File CSV di esempio
└── README.md                            # Questo file
```

## 🧪 Testing

### File di Test:
- Usa `sample_data/lista_spesa_esempio.csv` per testare l'app
- Contiene 10 articoli di esempio con barcode reali

### Test Scenarios:
1. **Caricamento CSV**: Verifica che tutti gli articoli vengano caricati
2. **Scansione**: Testa con barcode reali o generati
3. **Quantità**: Verifica calcoli e conversioni unità di misura
4. **Esportazione**: Controlla che il CSV finale sia corretto

## 🔍 Troubleshooting

### Problemi Comuni:

**App non si avvia:**
- Verifica permessi camera e storage
- Controlla compatibilità Android (min API 21)

**CSV non si carica:**
- Verifica formato del file (UTF-8, virgole come separatori)
- Controlla che tutte le colonne richieste siano presenti

**Scanner non funziona:**
- Verifica permessi camera
- Su emulatore, usa input manuale
- Su TC21, configura DataWedge

**Esportazione fallisce:**
- Verifica permessi scrittura storage
- Controlla spazio disponibile su dispositivo

## 🔄 Prossimi Sviluppi

### Versione 1.1 (Pianificata):
- [ ] Integrazione completa DataWedge Zebra
- [ ] OCR per codici non barcode
- [ ] Input manuale migliorato
- [ ] Filtri e ricerca articoli

### Versione 1.2 (Futura):
- [ ] Sincronizzazione server automatica
- [ ] Supporto multiple liste
- [ ] Statistiche e report
- [ ] Backup cloud

## 📞 Supporto

Per problemi o domande:
1. Controlla la sezione Troubleshooting
2. Verifica i log di Android Studio
3. Testa con il file CSV di esempio
4. Documenta il problema con screenshot

## 📄 Licenza

Progetto sviluppato per uso interno aziendale.
Tutti i diritti riservati.

---

**Versione**: 1.0.0  
**Data**: Ottobre 2025  
**Compatibilità**: Android 5.0+ (API 21+)  
**Dispositivo Target**: Zebra TC21