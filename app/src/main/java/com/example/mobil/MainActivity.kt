package com.example.mobil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ayarlar = AyarlarYoneticisi(applicationContext)
        val viewModel = UygulamaModeli(ayarlar)

        setContent {
            val isKaranlikMod by viewModel.karanlikModDurumu.collectAsState()
            val navController = rememberNavController()

            MaterialTheme(
                colorScheme = if (isKaranlikMod) darkColorScheme() else lightColorScheme()
            ) {
                NavHost(navController = navController, startDestination = "ana_ekran") {
                    composable("ana_ekran") {
                        AnaEkran(viewModel) { navController.navigate("ayarlar_ekrani") }
                    }
                    composable("ayarlar_ekrani") {
                        AyarlarEkranı(viewModel)
                    }
                }
            }
        }
    }
}