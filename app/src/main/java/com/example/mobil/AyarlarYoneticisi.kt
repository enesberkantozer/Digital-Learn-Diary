package com.example.mobil

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Context üzerinden DataStore'a erişim
val Context.dataStore by preferencesDataStore(name = "uygulama_ayarlari")

class AyarlarYoneticisi(private val context: Context) {

    companion object {
        private val ANAHTAR_KARANLIK_MOD = booleanPreferencesKey("karanlik_mod")
        private val ANAHTAR_PROFIL_URI = stringPreferencesKey("profil_resmi_uri")
    }

    // Karanlık mod tercihini oku
    val karanlikModAkisi: Flow<Boolean> = context.dataStore.data.map { p ->
        p[ANAHTAR_KARANLIK_MOD] ?: false
    }

    // Profil resmi URI'sını oku
    val profilResmiAkisi: Flow<String> = context.dataStore.data.map { p ->
        p[ANAHTAR_PROFIL_URI] ?: ""
    }

    // Karanlık modu kaydet
    suspend fun karanlikModSet(aktif: Boolean) {
        context.dataStore.edit { p -> p[ANAHTAR_KARANLIK_MOD] = aktif }
    }

    // Profil resmini kaydet
    suspend fun profilResmiSet(uri: String) {
        context.dataStore.edit { p -> p[ANAHTAR_PROFIL_URI] = uri }
    }
}