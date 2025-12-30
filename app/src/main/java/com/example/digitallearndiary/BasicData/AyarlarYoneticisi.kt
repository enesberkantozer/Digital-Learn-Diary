package com.example.digitallearndiary.BasicData

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Context üzerinden DataStore'a erişim
public val Context.dataStore by preferencesDataStore(name = "uygulama_ayarlari")

public class AyarlarYoneticisi(public val context: Context) {

    /**
     * Esnek Kaydetme Fonksiyonu: Herhangi bir tipi güvenli bir şekilde kaydeder.
     */
    public suspend fun <T> kaydet(anahtarAdı: String, deger: T) {
        context.dataStore.edit { p ->
            @Suppress("UNCHECKED_CAST")
            val anahtar = when (deger) {
                is String -> stringPreferencesKey(anahtarAdı)
                is Int -> intPreferencesKey(anahtarAdı)
                is Boolean -> booleanPreferencesKey(anahtarAdı)
                is Float -> floatPreferencesKey(anahtarAdı)
                is Long -> longPreferencesKey(anahtarAdı)
                else -> throw IllegalArgumentException("Tip desteklenmiyor")
            } as Preferences.Key<T>
            p[anahtar] = deger
        }
    }

    /**
     * Esnek Okuma Fonksiyonu: Akış (Flow) üzerinden veriyi sürekli takip eder.
     */
    public inline fun <reified T> oku(anahtarAdı: String, varsayilan: T): Flow<T> {
        val anahtar = when (T::class) {
            String::class -> stringPreferencesKey(anahtarAdı)
            Int::class -> intPreferencesKey(anahtarAdı)
            Boolean::class -> booleanPreferencesKey(anahtarAdı)
            Float::class -> floatPreferencesKey(anahtarAdı)
            Long::class -> longPreferencesKey(anahtarAdı)
            else -> throw IllegalArgumentException("Tip desteklenmiyor")
        } as Preferences.Key<T>

        return context.dataStore.data.map { p -> p[anahtar] ?: varsayilan }
    }

    // --- Kullanıma Hazır Akışlar ---
    public val karanlikModAkisi: Flow<Boolean> = oku("karanlik_mod", false)
    public val profilResmiAkisi: Flow<String> = oku("profil_resmi_uri", "")

    public suspend fun karanlikModSet(aktif: Boolean) = kaydet("karanlik_mod", aktif)
    public suspend fun profilResmiSet(uri: String) = kaydet("profil_resmi_uri", uri)
}