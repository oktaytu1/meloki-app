# 🌸 Meloki: Çiftler İçin Canlı & Saydam Android Widget Oyunu

Meloki, sevgilinizle aranızdaki mesafeyi sıcacık hissettiren, telefonunuzun ana ekranında **arka planı tamamen saydam (%100 transparent)** olarak yaşayan interaktif bir mini oda ve anı köşesidir.

---

## ✨ Öne Çıkan Özellikler

1. **%100 Gerçek Saydamlık (Transparent Widget):**
   - Widget arka planı tamamen şeffaftır; telefonunuzun duvar kağıdı üzerinde sanki simgelerin yanındaymış gibi sadece canlı kedi, saksıdaki çiçek, fotoğraf çerçevesi ve sevimli mobilyalar havada süzülür.
2. **Widget Üzerinden Doğrudan Etkileşim (Oyuna Girmeden Oynama):**
   - 🐱 **Kediye Dokun:** Mırlar, kalpler saçar ve sevgilinizin telefonuna anında *"Seni düşündü / mırlattı!"* bildirimi gider.
   - 🌱 **Çiçeğe Dokun:** Sulanır ve tohumdan filize, ardından rengarenk açan bir çiçeğe dönüşür.
   - 📸 **Anı Çerçevesine Dokun:** Galerinizden seçtiğiniz ortak fotoğraflar widget içinde döner.
3. **Uğraşsız İçerik: Günlük Gizemli Kutu (Algoritmik Gacha):**
   - Manuel seviye tasarlamakla uğraşmanıza gerek yoktur. Algoritma her gün ikinize de rastgele bir sevimli eşya hediye eder (sıcak kahve kupası, pelüş ayıcık, nostaljik plak çalar, yıldız projektörü vb.).
4. **Zahmetsiz Eşleşme (6 Haneli Kod):**
   - Bir taraf "Oda Kur" diyerek `MELO-42` gibi 6 haneli bir kod alır. Diğer taraf bu kodu girdiğinde iki telefon kalıcı olarak senkronize olur.
5. **Sıfır Sunucu Maliyeti (Firebase):**
   - Google Firebase Firestore (ücretsiz Spark planı) ile anlık veri akışı sağlanır.

---

## 🚀 1. Canlı Web Simülatörünü Hemen Deneyin

Projeyi anında tarayıcınızda görmek ve çift etkileşimini (senin telefonun & sevgilinin telefonu yan yana) test etmek için:

### Yöntem A (Doğrudan Tarayıcıda Açma):
`simulator/index.html` dosyasını çift tıklayarak herhangi bir tarayıcıda (Chrome, Edge vb.) açabilirsiniz.

### Yöntem B (Yerel Sunucu ile):
Terminalde şu komutu çalıştırabilirsiniz:
```bash
node simulator/server.js
```
Ardından tarayıcınızda `http://localhost:3000` adresine gidin.

#### Simülatörde Neleri Test Edebilirsiniz?
- **Kediye tıklayın:** Her iki ekranda aynı anda uçan kalpleri ve kedi animasyonunu görün.
- **Çiçeğe tıklayın:** Sulama sayacının artışını ve çiçeğin büyümesini izleyin.
- **Duvar Kağıdını Değiştir butonuna basın:** Şeffaf widget'ın farklı duvar kağıtlarında nasıl tamamen arka plansız durduğunu görün.
- **Galeriden Fotoğraf Seçin:** Bilgisayarınızdan bir görsel seçtiğinizde fotoğraf çerçevesinin anında güncellenmesini deneyimleyin.
- **Günün Gizemli Kutusunu Açın:** Algoritmanın odaya yeni bir eşya eklemesini test edin.

---

## 📱 2. Android Studio ile APK Derleme

Android projesi `android/` klasörü içinde hazır olarak bulunmaktadır:

1. **Android Studio'yu İndirin ve Kurun:**
   - [developer.android.com/studio](https://developer.android.com/studio) adresinden Android Studio'yu kurun.
2. **Projeyi Açın:**
   - Android Studio'da `Open` seçeneğine tıklayın ve `meloki-app/android` klasörünü seçin.
3. **Firebase Bağlantısı (İsteğe Bağlı ama Önerilen):**
   - [console.firebase.google.com](https://console.firebase.google.com) adresinden ücretsiz bir proje açın.
   - Android uygulaması ekleyin (`com.meloki.app`).
   - İndirdiğiniz `google-services.json` dosyasını `android/app/` klasörüne yapıştırın.
   - Firestore Database'i "Test Modunda" başlatın.
4. **Çalıştırın:**
   - Telefonunuzu USB hata ayıklama ile bilgisayara bağlayın veya emülatör seçin.
   - Sağ üstteki yeşil **Run (Çalıştır)** butonuna basarak uygulamayı yükleyin.
5. **Widget'ı Ana Ekrana Ekleyin:**
   - Telefonun ana ekranına uzun basın -> **Araç Takımları (Widgets)** -> **Meloki** -> Ekranınıza sürükleyin!

---

## 🛠️ Mimari & Teknolojiler

- **Platform:** Native Android (Kotlin)
- **UI:** Jetpack Compose & Material 3
- **Widget Altyapısı:** Android Jetpack Glance (`androidx.glance.appwidget`)
- **Şeffaflık:** `ColorProvider(Color.Transparent)` ile sıfır arka plan katmanı
- **Senkronizasyon:** Google Firebase Firestore Realtime Listeners
- **Görseller:** 16-bit Cozy Pixel Art
