package com.example.digitallearndiary.basicData

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitallearndiary.cloud.VisionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

public class UygulamaModeli(public val ayarlar: AyarlarYoneticisi) : ViewModel() {

    // --- State: Arkadaşın bu değerleri .collectAsState() ile dinler ---
    public val karanlikModDurumu: StateFlow<Boolean> = ayarlar.karanlikModAkisi
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), false)

    public val profilResmiDurumu: StateFlow<String> = ayarlar.profilResmiAkisi
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), "")

    private val _tarananNot = MutableStateFlow("")
    public val tarananNot: StateFlow<String> = _tarananNot.asStateFlow()

    // --- Actions: Arkadaşın bu fonksiyonları butonlarına atar ---
    public fun temaDegistir(aktif: Boolean) {
        viewModelScope.launch { ayarlar.karanlikModSet(aktif) }
    }

    public fun profilResmiGuncelle(uri: String) {
        viewModelScope.launch { ayarlar.profilResmiSet(uri) }
    }

    public fun notuTara(context: Context, uri: Uri) {
        viewModelScope.launch {
            _tarananNot.value = "AI analiz ediyor, lütfen bekleyin..."
            _tarananNot.value = VisionService.resmiAnalizEt(context, uri)
        }
    }
}