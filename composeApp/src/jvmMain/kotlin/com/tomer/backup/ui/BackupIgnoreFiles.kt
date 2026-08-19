package com.tomer.backup.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sambabackup.composeapp.generated.resources.Res
import sambabackup.composeapp.generated.resources.folder
import java.awt.Desktop
import java.io.File

@Composable
fun AddBackupToolUI(foldersWithoutBackupIgnore: List<File>) {
    Column(Modifier.fillMaxSize()) {
        Text(
            "Add .backupignore file to these folders to backup them",
            color = Color.Black.copy(.8f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            modifier = Modifier.padding(12.dp)
        )
        FlowRow(Modifier.fillMaxWidth().padding(12.dp).weight(1f)) {
            foldersWithoutBackupIgnore.forEach {
                FolderCard(it) {
                    val backupFile = File(it, ".backupignore")
                    if (!backupFile.exists()) {
                        backupFile.createNewFile()
                        backupFile.writeText(com.tomer.backup.utils.backupfilecontent)
                    }
                    Desktop.getDesktop().open(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FolderCard(file: File, onClick: () -> Unit) {
    val h10 = Modifier.height(10.dp)
    var hovrerd by remember { mutableStateOf(false) }
    val size by animateDpAsState(if (hovrerd) 120.dp else 0.dp)
    Column(
        Modifier.width(124.dp).wrapContentHeight()
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                onClick.invoke()
            }
            .onPointerEvent(onEvent = { hovrerd = true }, eventType = PointerEventType.Enter)
            .onPointerEvent(onEvent = { hovrerd = false }, eventType = PointerEventType.Exit),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(top = 10.dp)) {
            Box(Modifier.size(size).clip(CircleShape).background(Color.Red))
            Image(
                org.jetbrains.compose.resources.painterResource(Res.drawable.folder),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
        Text(text = file.name, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(h10)
    }
}