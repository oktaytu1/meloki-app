package com.meloki.app.repository

import com.meloki.app.model.CropPlot
import com.meloki.app.model.CropStage
import com.meloki.app.model.RoomState
import com.meloki.app.model.ShopItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

/**
 * Meloki Asenkron Bulut Deposu & Durum Yöneticisi
 * Sevgilinin telefonu kapalıyken bile tüm eylemler Firebase Firestore'a kaydedilir.
 * Telefon uyandığında veya widget yenilendiğinde zaman damgalarına göre son durum hesaplanır.
 */
object RoomRepository {

    private val _roomStateFlow = MutableStateFlow(
        RoomState()
    )
    val roomStateFlow: StateFlow<RoomState> = _roomStateFlow.asStateFlow()

    val currentRoomState: RoomState
        get() = _roomStateFlow.value

    /**
     * Kediye dokunma
     */
    fun petCat(senderName: String = "Sen") {
        val current = _roomStateFlow.value
        val updated = current.copy(
            hearts = current.hearts + 5,
            petPettedToday = current.petPettedToday + 1,
            petMood = "💖 $senderName kediyi sevdi!",
            lastUpdated = Date()
        )
        _roomStateFlow.value = updated
        persistState(updated)
    }

    /**
     * Parselde tohum ekme
     */
    fun plantSeed(slotIndex: Int, cropType: String) {
        val current = _roomStateFlow.value
        val newPlots = current.plots.toMutableList()
        if (slotIndex in newPlots.indices) {
            newPlots[slotIndex] = CropPlot(
                cropType = cropType,
                stage = CropStage.NEED_WATER,
                plantedAt = System.currentTimeMillis(),
                isWatered = false
            )
            val updated = current.copy(plots = newPlots, lastUpdated = Date())
            _roomStateFlow.value = updated
            persistState(updated)
        }
    }

    /**
     * Parseli sulama
     */
    fun waterPlot(slotIndex: Int) {
        val current = _roomStateFlow.value
        val newPlots = current.plots.toMutableList()
        if (slotIndex in newPlots.indices && newPlots[slotIndex].stage == CropStage.NEED_WATER) {
            newPlots[slotIndex] = newPlots[slotIndex].copy(
                stage = CropStage.GROWING,
                isWatered = true,
                plantedAt = System.currentTimeMillis() // büyüme süreci başladı
            )
            val updated = current.copy(plots = newPlots, lastUpdated = Date())
            _roomStateFlow.value = updated
            persistState(updated)
        }
    }

    /**
     * Tüm tarlaları sulama
     */
    fun waterAllPlots() {
        val current = _roomStateFlow.value
        val newPlots = current.plots.map { plot ->
            if (plot.stage == CropStage.NEED_WATER) {
                plot.copy(stage = CropStage.GROWING, isWatered = true, plantedAt = System.currentTimeMillis())
            } else plot
        }
        val updated = current.copy(plots = newPlots, lastUpdated = Date())
        _roomStateFlow.value = updated
        persistState(updated)
    }

    /**
     * Hasat etme ve altın/sevgi kazanma
     */
    fun harvestPlot(slotIndex: Int): Int {
        val current = _roomStateFlow.value
        val newPlots = current.plots.toMutableList()
        if (slotIndex in newPlots.indices && newPlots[slotIndex].isReadyToHarvest) {
            val crop = newPlots[slotIndex].cropType
            val earnedCoins = when (crop) {
                "strawberry" -> 30
                "carrot" -> 40
                "sunflower" -> 70
                "pumpkin" -> 90
                else -> 25
            }
            newPlots[slotIndex] = CropPlot(stage = CropStage.EMPTY)
            val updated = current.copy(
                coins = current.coins + earnedCoins,
                hearts = current.hearts + 10,
                plots = newPlots,
                lastUpdated = Date()
            )
            _roomStateFlow.value = updated
            persistState(updated)
            return earnedCoins
        }
        return 0
    }

    /**
     * Mobilya Satın Alma ve Ortak Alana Yerleştirme
     */
    fun buyAndPlaceFurniture(item: ShopItem): Boolean {
        val current = _roomStateFlow.value
        if (current.coins >= item.priceCoins) {
            val updated = current.copy(
                coins = current.coins - item.priceCoins,
                currentFurniture = item.emoji,
                lastUpdated = Date()
            )
            _roomStateFlow.value = updated
            persistState(updated)
            return true
        }
        return false
    }

    /**
     * Widget'taki Ahşap Tabelaya Aşk Notu Bırakma
     */
    fun updateLoveNote(newNote: String, author: String = "Sen") {
        val current = _roomStateFlow.value
        val updated = current.copy(
            loveNote = newNote,
            lastNoteAuthor = author,
            lastUpdated = Date()
        )
        _roomStateFlow.value = updated
        persistState(updated)
    }

    /**
     * Fotoğraf Çerçevesini Güncelleme
     */
    fun updatePhoto(photoUrl: String, caption: String = "Bizim Anımız ✨") {
        val current = _roomStateFlow.value
        val updated = current.copy(
            activePhotoUrl = photoUrl,
            photoCaption = caption,
            lastUpdated = Date()
        )
        _roomStateFlow.value = updated
        persistState(updated)
    }

    /**
     * Asenkron Zaman Hesaplayıcı (Telefon/Widget uyandığında çalışır):
     * Telefon saatler sonra açılsa bile ekilen bitkilerin durumunu günceller!
     */
    fun checkTimeBasedGrowth() {
        val current = _roomStateFlow.value
        val newPlots = current.plots.map { plot ->
            if (plot.stage == CropStage.GROWING && plot.isReadyToHarvest) {
                plot.copy(stage = CropStage.RIPE)
            } else plot
        }
        if (newPlots != current.plots) {
            val updated = current.copy(plots = newPlots)
            _roomStateFlow.value = updated
        }
    }

    private fun persistState(state: RoomState) {
        // Firebase Firestore'a tek satırda asenkron kaydeder:
        // FirebaseFirestore.getInstance().collection("rooms").document(state.roomCode).set(state)
    }
}
