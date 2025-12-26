package com.example.mobil

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnaEkran(viewModel: UygulamaModeli, ayarlarGit: () -> Unit) {
    // Surface, MainActivity'den gelen karanlık/aydınlık mod renklerini uygular
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // SAĞ ÜST KÖŞE: Ayarlar Çark İkonu
            IconButton(
                onClick = ayarlarGit,
                modifier = Modifier
                    .align(Alignment.TopEnd) // Sağ üst köşeye yasla
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ayarlar",
                    tint = MaterialTheme.colorScheme.primary, // Temanın ana rengini kullanır
                    modifier = Modifier.size(32.dp)
                )
            }

            // MERKEZ: Hoş Geldiniz Yazısı
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hoş Geldiniz",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground // Karanlık modda otomatik beyaz olur
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Uygulamanız kullanıma hazır.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // Daha sönük, estetik bir alt metin
                )
            }
        }
    }
}