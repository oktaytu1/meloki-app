package com.meloki.app.model

import java.util.Date

/**
 * Meloki Cozy Farm & Room Ortak Bulut Modeli
 * Asenkron kalıcı depolama: Sevgilinin telefonu kapalıyken bile tüm eylemler kaydedilir.
 * Telefon açıldığında veya widget uyandığında zaman damgalarına (timestamp) göre büyüme hesaplanır.
 */
data class RoomState(
    val roomCode: String = "MELO-42",
    val partner1Name: String = "Sen",
    val partner2Name: String = "Sevgilin",

    // Ortak Ekonomi
    val coins: Int = 120,
    val hearts: Int = 85,
    val streakDays: Int = 14,

    // Aşk Notu (Widget üzerindeki ahşap tabelada sürekli durur)
    val loveNote: String = "Akşama kahveler benden! 💕",
    val lastNoteAuthor: String = "Sen",

    // Polaroid Fotoğraf Çerçevesi (Masaüstü widgetında asılı kalır)
    val activePhotoUrl: String = "",
    val photoCaption: String = "Bizim Anımız ✨",

    // Yerleştirilen Ana Mobilya (Fener, Masa, Salıncak vb.)
    val currentFurniture: String = "🏮",

    // 3 Adet Tarla Parseli (Zaman damgası ile asenkron büyüme)
    val plots: List<CropPlot> = listOf(
        CropPlot(cropType = "strawberry", stage = CropStage.RIPE),
        CropPlot(cropType = "carrot", stage = CropStage.GROWING),
        CropPlot(cropType = "sunflower", stage = CropStage.NEED_WATER)
    ),

    // Evcil Hayvan Durumu
    val petMood: String = "Mırıldıyor 💖",
    val petPettedToday: Int = 4,

    val lastUpdated: Date? = null
)

data class CropPlot(
    val cropType: String? = null, // "strawberry", "carrot", "sunflower", "pumpkin"
    val stage: CropStage = CropStage.EMPTY,
    val plantedAt: Long = System.currentTimeMillis(),
    val growthDurationSeconds: Long = 60, // Gerçekte örn: 3600 (1 saat)
    val isWatered: Boolean = false
) {
    /**
     * Telefon saatler sonra açılsa bile aradan geçen zamana bakarak olgunlaşıp olgunlaşmadığını anlar!
     */
    val isReadyToHarvest: Boolean
        get() {
            if (stage == CropStage.EMPTY || !isWatered) return false
            val elapsedSeconds = (System.currentTimeMillis() - plantedAt) / 1000
            return elapsedSeconds >= growthDurationSeconds
        }
}

enum class CropStage {
    EMPTY,
    NEED_WATER,
    GROWING,
    RIPE
}

data class ShopItem(
    val id: String,
    val name: String,
    val emoji: String,
    val priceCoins: Int
)

object ShopCatalog {
    val items = listOf(
        ShopItem("lantern", "Japon Bahçe Feneri", "🏮", 40),
        ShopItem("table", "Ahşap Piknik Masası", "🪵", 60),
        ShopItem("coffee", "Sıcak Kahve Fincanı", "☕", 25),
        ShopItem("teddy", "Büyük Pelüş Ayı", "🧸", 80),
        ShopItem("cherry", "Kiraz Çiçeği Saksısı", "🌸", 50),
        ShopItem("radio", "Retro Nostaljik Radyo", "📻", 70)
    )
}
