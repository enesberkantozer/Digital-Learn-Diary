package com.example.digitallearndiary.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.digitallearndiary.auth.signInWithGoogle
import com.example.digitallearndiary.basicData.AyarlarYoneticisi
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun AuthProfileScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val ayarlarYoneticisi = remember { AyarlarYoneticisi(context) }

    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // İkon veya Logo Alanı
            Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            if (user == null) {
                // Giriş Yapılmamış Durum
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Bulut Eşitleme",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verilerinize her yerden erişmek için giriş yapın.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Google Stilli Buton
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val signedInUser = signInWithGoogle(context)
                            if (signedInUser != null) {
                                user = signedInUser
                                signedInUser.email?.let { email ->
                                    ayarlarYoneticisi.emailKaydet(email)
                                    Log.d("AuthProfileScreen", "Email kaydedildi: $email")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Not: Buraya Google logosu eklenebilir
                        Text("Google ile Devam Et")
                    }
                }
            } else {
                // Giriş Yapılmış Durum
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        // Buraya fillMaxWidth() ekledik
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Profil Resmi
                        AsyncImage(
                            model = user?.photoUrl,
                            contentDescription = "Profil Resmi",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Hoş geldin,",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = user?.displayName ?: "Kullanıcı",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center // Uzun isimler için metni de ortalayalım
                        )
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                TextButton(onClick = {
                    scope.launch {
                        Firebase.auth.signOut()
                        user = null
                        ayarlarYoneticisi.emailKaydet("")
                        Log.d("AuthProfileScreen", "Oturum kapatıldı")
                    }
                }) {
                    Text("Oturumu Kapat", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}