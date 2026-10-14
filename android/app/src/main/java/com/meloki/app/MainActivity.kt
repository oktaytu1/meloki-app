package com.meloki.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meloki.app.model.CropStage
import com.meloki.app.model.ShopCatalog
import com.meloki.app.model.ShopItem
import com.meloki.app.repository.RoomRepository

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RoomRepository.checkTimeBasedGrowth() // Zaman tabanlı asenkron büyüme kontrolü

        setContent {
            MelokiTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MelokiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF6B8B),
            secondary = Color(0xFFFFD43B),
            background = Color(0xFF14121E),
            surface = Color(0xFF221F2D)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Bahçemiz 🌾", "Mağaza 🛋️", "Not & Fotoğraf 💌", "Eşleşme 🔗")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🌾 MELOKI: BİZİM KÖŞEMİZ",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD43B)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF221F2D)
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF221F2D)) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title, fontSize = 10.sp) },
                        icon = {}
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF14121E))
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> FarmScreen()
                1 -> ShopScreen()
                2 -> NoteAndPhotoScreen()
                3 -> PairingScreen()
            }
        }
    }
}

/**
 * 1. TAB: Canlı Bahçe & Parseller
 */
@Composable
fun FarmScreen() {
    val room by RoomRepository.roomStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ortak Kasa Barı
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B263D)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🪙 ${room.coins} Altın", fontWeight = FontWeight.Bold, color = Color(0xFFFFE066), fontSize = 15.sp)
                Text("💖 ${room.hearts} Sevgi", fontWeight = FontWeight.Bold, color = Color(0xFFFF8787), fontSize = 15.sp)
                Text("🔥 ${room.streakDays} Gün", fontWeight = FontWeight.Bold, color = Color(0xFF69DB7C), fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aşk Notu Önizleme
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5A2B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("💌 SEVGİLİNDEN AŞK NOTU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE066))
                Text(room.loveNote, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tarla Parselleri
        Text(
            "🌾 Tarla Parselleri (Asenkron Büyür)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD43B),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            room.plots.forEachIndexed { index, plot ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clickable {
                            if (plot.stage == CropStage.EMPTY) {
                                RoomRepository.plantSeed(index, "strawberry")
                            } else if (plot.stage == CropStage.NEED_WATER) {
                                RoomRepository.waterPlot(index)
                            } else if (plot.stage == CropStage.RIPE) {
                                RoomRepository.harvestPlot(index)
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF38210F)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val icon = when (plot.stage) {
                            CropStage.RIPE -> if (plot.cropType == "carrot") "🥕" else "🍓"
                            CropStage.GROWING -> "🌿"
                            CropStage.NEED_WATER -> "🌱"
                            else -> "🕳️"
                        }
                        val status = when (plot.stage) {
                            CropStage.RIPE -> "Hasat Et!"
                            CropStage.GROWING -> "Büyüyor"
                            CropStage.NEED_WATER -> "Sula"
                            else -> "Ek"
                        }
                        Text(icon, fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE066))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hızlı Butonlar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { RoomRepository.waterAllPlots() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228BE6))
            ) {
                Text("💧 Tümünü Sula")
            }

            Button(
                onClick = { RoomRepository.petCat() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B8B))
            ) {
                Text("🐱 Kediyi Sev (+5💖)")
            }
        }
    }
}

/**
 * 2. TAB: Mobilya Mağazası
 */
@Composable
fun ShopScreen() {
    val room by RoomRepository.roomStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🛋️ Mobilya & Bahçe Mağazası", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD43B))
        Text(
            "Satın aldığınız mobilyalar hem sizin hem sevgilinizin saydam widget'ına yerleşir.",
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ShopCatalog.items) { item ->
                Card(
                    modifier = Modifier.clickable {
                        RoomRepository.buyAndPlaceFurniture(item)
                    },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2B263D)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(item.emoji, fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🪙 ${item.priceCoins} Altın", color = Color(0xFFFFE066), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * 3. TAB: Aşk Notu & Polaroid Fotoğraf Yükleyici
 */
@Composable
fun NoteAndPhotoScreen() {
    val room by RoomRepository.roomStateFlow.collectAsState()
    var noteText by remember { mutableStateOf(room.loveNote) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            RoomRepository.updatePhoto(it.toString(), "Bizim Anımız ✨")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("💌 Aşk Notu & Fotoğraf Çerçevesi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD43B))
        Spacer(modifier = Modifier.height(14.dp))

        // Not Yazma
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Sevgilinin Widget'ında Görünecek Not") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { RoomRepository.updateLoveNote(noteText) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5A2B))
        ) {
            Text("Tabelaya As ✨")
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Fotoğraf Seçme
        Text("📸 Polaroid Masaüstü Çerçevesi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8787))
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Galeriden seçtiğiniz fotoğraf sevgilinizin telefon ekranındaki saydam widget polaroidine anında yansır.",
            fontSize = 12.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { photoPicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B8B))
        ) {
            Text("Galeriden Fotoğraf Seç 📷")
        }
    }
}

/**
 * 4. TAB: Eşleşme Ekranı
 */
@Composable
fun PairingScreen() {
    val room by RoomRepository.roomStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔗 Çift Eşleşme Kodu", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD43B))
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Bu kodu sevgilinizin telefonuna girerek aynı bahçeyi paylaşın.",
            color = Color.LightGray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = room.roomCode,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6B8B)
        )
    }
}
