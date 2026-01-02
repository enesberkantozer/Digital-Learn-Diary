# Dijital Öğrenme Günlüğü (Digital Learn Diary) 📔

Digital Learn Diary, öğrencilerin çalışma süreçlerini dijitalleştirmek için tasarlanmış kapsamlı bir Android uygulamasıdır. Yapay zeka destekli not alma (OCR), donanım takibi içeren odaklanma seansları ve bulut senkronizasyonunu tek bir platformda birleştirir.

## ✨ Temel Özellikler
• **Akıllı Not Alma (OCR):** Google Cloud Vision API entegrasyonu ile fotoğraflardaki metinleri anında nota dönüştürün.  
• **Odaklanma Modu:** Çalışma sırasında telefonun hareketini ve Wi-Fi durumunu izleyen Ön Plan Servisi (Foreground Service).  
• **Akıllı Sensör Takibi:** İvmeölçer kullanarak yerçekimi dengeli özel bir algoritma ile odak kaybını tespit eder.  
• **Gelişmiş Bildirim Sistemi:** Odak ihlalleri, bağlantı olayları ve seans başarıları için anlık geri bildirim.  
• **Veri Senkronizasyonu:** Yerel Room Veritabanı ile Firebase Firestore arasında çift yönlü senkronizasyon.  
• **Görev Yönetimi:** Ders bazlı yapılacaklar listeleri ve görev takibi.  
• **Google Kimlik Doğrulama:** Credential Manager ve Firebase Auth ile güvenli giriş.  
• **Modern Arayüz:** Jetpack Compose ve Material 3 ile geliştirilmiş reaktif kullanıcı arayüzü.

## 🛠️ Teknoloji Yığını
• **Dil:** Kotlin  
• **UI:** Jetpack Compose (Material 3)  
• **Veritabanı:** Room (Yerel), Firestore (Bulut)  
• **Eşzamanlılık:** Coroutines & Flow  
• **Ağ:** Retrofit & GSON  
• **Mimari:** MVVM (Model-View-ViewModel)  
• **Donanım:** İvmeölçer Sensörü ve Bildirim Yöneticisi (NotificationManager) entegrasyonu.

## 📂 Proje Yapısı
• `auth`: Google Giriş ve Firebase Auth mantığı.  
• `backgroundForegroundServices`: Odaklanma seansları için servisler.  
• `notification`: Bildirim kanalları ve uyarı yönetimi için `AppNotificationHelper`.  
• `sensor`: İvmeölçer entegrasyonu ve hareket algılama mantığı.  
• `basicData`: DataStore tercihler ve yerel ayar yönetimi.  
• `cloud`: OCR mantığı ve Google Vision API uygulaması.  
• `connectivity`: Wi-Fi ile odak takip kontrolü yapılıyor.  
• `firestore`: Senkronizasyon yönetimi ve uzak depo uygulaması.  
• `room`: Yerel depolama için veritabanı, DAO'lar ve tablolar.  
• `ui`: Jetpack Compose ekranları ve navigasyon.

## 📡 Sensör Algoritması ve Bildirim Mantığı
Uygulama, kaliteli bir odaklanma süreci için özel bir takip mekanizması kullanır:
1. **Hareket Algılama Algoritması:** `MotionSensorManager`, X, Y ve Z eksenlerinin mutlak toplamından yerçekimi ivmesini çıkararak net hareketi hesaplar (\(|x| + |y| + |z| - G\)).
    - **Eşik Değeri:** 5'ten büyük hareketler olay tetikler.
    - **Bekleme Süresi (Cooldown):** 5 saniyelik bekleme süresi ile peş peşe uyarı gönderilmesi engellenir.
2. **Bildirim Mimarisi:**
    - **`AppNotificationHelper`:** Bildirim kanallarının oluşturulmasını ve yüksek öncelikli uyarıları yönetir.
    - **`AlarmReceiver`:** Süre bittiğinde kullanıcıya "Tebrikler" bildirimi gönderir ve servisi durdurur.
    - **`ConnectivityBroadcastReceiver`:** Ağ değişiklikleri için gerçek zamanlı bildirimler sağlar.

## ⚙️ Kurulum ve Yapılandırma
Projeyi çalıştırmak için aşağıdaki anahtarları `BuildConfig` veya `local.properties` dosyasına eklemeniz gerekir:
1. **Google Vision API Key:** OCR özelliği için gereklidir.
2. **Google Web Client ID:** Google Giriş işlemi için gereklidir.
3. **Firebase:** `google-services.json` dosyanızı `app/` dizinine ekleyin.

## 📡 Odaklanma Seansı Sürekliliği
Uygulama, seansların sistem tarafından kapatılmaması için `Foreground Service` kullanır.
1. **Hareket:** Telefon belirgin şekilde hareket ettirildiğinde uyarı tetikler.
2. **Ağ:** Seans sırasında Wi-Fi açıldığında/kapatıldığında bildirim gönderir.
3. **Süreklilik:** `BootReceiver` sayesinde cihaz yeniden başlatılsa bile aktif seanslar otomatik olarak geri yüklenir.