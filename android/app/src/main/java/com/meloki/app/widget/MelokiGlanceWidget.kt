package com.meloki.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.meloki.app.model.CropStage
import com.meloki.app.repository.RoomRepository

/**
 * Meloki Saydam Çiftlik & Bahçe Android Widget'ı
 * Arka plan %100 SAYDAM (Color.Transparent).
 * Duvar kağıdınız üzerinde sadece ahşap tabela, polaroid fotoğraf, ekinler, kedi ve mobilyalar süzülür.
 */
class MelokiGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        RoomRepository.checkTimeBasedGrowth() // Zaman aşımına göre bitkileri kontrol et
        provideContent {
            GlanceTheme {
                MelokiCozyFarmWidgetContent(context)
            }
        }
    }

    @Composable
    private fun MelokiCozyFarmWidgetContent(context: Context) {
        val room = RoomRepository.currentRoomState

        // ANA KAPSAYICI: Tamamen ŞEFFAF
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color.Transparent)) // %100 ŞEFFAFLIK
                .padding(6.dp)
        ) {
            // 1. ÜST SATIR: Ahşap Aşk Tabelası (Sol) + Polaroid Fotoğraf Çerçevesi (Sağ)
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalAlignment = Alignment.Top
            ) {
                // Ahşap Tabela (Aşk Notu)
                Column(
                    modifier = GlanceModifier
                        .width(135.dp)
                        .background(ColorProvider(Color(0xD98B5A2B)))
                        .cornerRadius(10.dp)
                        .padding(6.dp)
                        .clickable(actionRunCallback<OpenNoteCallback>())
                ) {
                    Text(
                        text = "💌 AŞK TABELASI",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFFFE066)),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = room.loveNote,
                        maxLines = 2,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.width(16.dp))

                // Polaroid Fotoğraf Çerçevesi
                Column(
                    modifier = GlanceModifier
                        .width(85.dp)
                        .background(ColorProvider(Color(0xF2FDFBF7)))
                        .cornerRadius(6.dp)
                        .padding(4.dp)
                        .clickable(actionRunCallback<OpenPhotoCallback>()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(ColorProvider(Color(0xFF2C3E50)))
                            .cornerRadius(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📸 Anımız",
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = room.photoCaption,
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF343A40)),
                            fontSize = 8.sp
                        )
                    )
                }
            }

            // 2. ORTA / ALT: Tarla Parselleri + Kedi + Mobilya
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tarla Parselleri & Kedi & Mobilya Sırası
                    Row(
                        modifier = GlanceModifier.fillMaxWidth().padding(bottom = 6.dp),
                        horizontalAlignment = Alignment.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Parsel 1
                        CropPlotWidget(room.plots.getOrNull(0), 0)

                        // Parsel 2
                        CropPlotWidget(room.plots.getOrNull(1), 1)

                        // Parsel 3
                        CropPlotWidget(room.plots.getOrNull(2), 2)

                        // Kedi
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = GlanceModifier.clickable(actionRunCallback<PetCatWidgetCallback>())
                        ) {
                            Text(text = "🐱", style = TextStyle(fontSize = 32.sp))
                            Text(
                                text = "Mırr~",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFFFF8787)),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Yerleştirilen Mobilya
                        Text(
                            text = room.currentFurniture,
                            style = TextStyle(fontSize = 28.sp)
                        )
                    }

                    // 3. EN ALT: Widget İçi Hızlı Kontrol Barı
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .background(ColorProvider(Color(0x99000000)))
                            .cornerRadius(12.dp)
                            .padding(4.dp),
                        horizontalAlignment = Alignment.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🪙 ${room.coins}",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFFFFE066)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "💖 ${room.hearts}",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFFFF8787)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Box(
                            modifier = GlanceModifier
                                .clickable(actionRunCallback<WaterAllWidgetCallback>())
                                .padding(horizontal = 6.dp)
                        ) {
                            Text(
                                text = "💧 Sula",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF4DABF7)),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CropPlotWidget(plot: com.meloki.app.model.CropPlot?, index: Int) {
        val params = actionParametersOf(ActionParameters.Key<Int>("plotIndex") to index)
        val icon = when (plot?.stage) {
            CropStage.RIPE -> when (plot.cropType) {
                "strawberry" -> "🍓"
                "carrot" -> "🥕"
                "sunflower" -> "🌻"
                else -> "🌾"
            }
            CropStage.GROWING -> "🌿"
            CropStage.NEED_WATER -> "🌱"
            else -> "🕳️"
        }

        val subText = when (plot?.stage) {
            CropStage.RIPE -> "Hasat!"
            CropStage.GROWING -> "Büyüyor"
            CropStage.NEED_WATER -> "Su Ver"
            else -> "Boş"
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier
                .size(46.dp)
                .background(ColorProvider(Color(0xB35C3A21)))
                .cornerRadius(10.dp)
                .padding(2.dp)
                .clickable(actionRunCallback<PlotClickWidgetCallback>(params))
        ) {
            Text(text = icon, style = TextStyle(fontSize = 20.sp))
            Text(
                text = subText,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFFFE066)),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

/**
 * Widget Callback'leri
 */
class PlotClickWidgetCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val idx = parameters[ActionParameters.Key<Int>("plotIndex")] ?: 0
        val plot = RoomRepository.currentRoomState.plots.getOrNull(idx)

        if (plot?.stage == CropStage.EMPTY) {
            RoomRepository.plantSeed(idx, "strawberry")
        } else if (plot?.stage == CropStage.NEED_WATER) {
            RoomRepository.waterPlot(idx)
        } else if (plot?.stage == CropStage.RIPE) {
            RoomRepository.harvestPlot(idx)
        }
        MelokiGlanceWidget().update(context, glanceId)
    }
}

class WaterAllWidgetCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        RoomRepository.waterAllPlots()
        MelokiGlanceWidget().update(context, glanceId)
    }
}

class PetCatWidgetCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        RoomRepository.petCat()
        MelokiGlanceWidget().update(context, glanceId)
    }
}

class OpenNoteCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Doğrudan not düzenleme aktivitesine veya diyaloğuna yönlendirir
    }
}

class OpenPhotoCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Fotoğraf albümüne yönlendirir
    }
}

class MelokiGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MelokiGlanceWidget()
}
