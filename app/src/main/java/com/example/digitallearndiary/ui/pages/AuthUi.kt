package com.example.digitallearndiary.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun AuthProfileScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Kullanıcı bilgisini saklayan state
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (user == null) {
            Text("Bulut Eşitleme", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                scope.launch {
                    val signedInUser = signInWithGoogle(context)
                    if (signedInUser != null) {
                        // İŞTE BURASI KRİTİK: State'i güncelliyoruz ki ekran değişsin
                        user = signedInUser
                    } else {
                        Log.e("Auth", "Giriş başarısız oldu veya iptal edildi")
                    }
                }
            }) {
                Text("Google ile Giriş Yap")
            }
        } else {
            // Giriş yapıldıktan sonra görünecek kısım
            Text("Hoş geldin, ${user?.displayName}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                Firebase.auth.signOut()
                user = null // State'i temizle, login butonuna geri döner
            }) {
                Text("Çıkış Yap")
            }
        }
    }

}