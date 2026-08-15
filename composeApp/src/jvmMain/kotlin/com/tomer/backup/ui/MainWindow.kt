package com.tomer.backup.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomer.backup.service.BackupService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BackupToolUI() {
    val menuItems = listOf(
        "Sources",
        "Logs",
    )

    var selectedItem by remember { mutableStateOf("Sources") }

    Row(Modifier.fillMaxSize()) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight()
                .background(Color(0xFF1E1E1E))
                .padding(12.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "ShadowSync",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            menuItems.forEach { label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            selectedItem = label
                        }
                        .background(if (selectedItem == label) Color.DarkGray else Color.Transparent)
                        .padding(12.dp)
                ) {
                    Text(label, color = Color.White, fontSize = 14.sp)
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.weight(1f))
            var hovrerd by remember { mutableStateOf(false) }
            var backingUP by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(if (hovrerd) 1.1f else 1f)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        CoroutineScope(Dispatchers.Default).launch {
                            backingUP = true
                            BackupService().startBackup()
                            backingUP = false
                        }
                    }
                    .onPointerEvent(onEvent = { hovrerd = true }, eventType = PointerEventType.Enter)
                    .onPointerEvent(onEvent = { hovrerd = false }, eventType = PointerEventType.Exit)
                    .background(Color.DarkGray)
                    .padding(12.dp)

            ) {
                Text("SYNC NOW", color = Color.White, fontSize = 14.sp)
                if (backingUP) {
                    Spacer(Modifier.weight(1f))
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Top).size(32.dp),
                        color = Color.White,
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Main Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4))
                .padding(16.dp)
        ) {
            when (selectedItem) {
                "Logs" -> LogScreen()
                else -> SourcesScreen()
            }
        }
    }
}
