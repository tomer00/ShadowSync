package com.tomer.backup

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.tomer.backup.data.AppProperties
import com.tomer.backup.data.initDatabase
import com.tomer.backup.service.BackupService
import com.tomer.backup.ui.BackupToolUI
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) = application {
    AppProperties.props
    initDatabase()
    if ((args.firstOrNull() ?: "") == "backup") {
        runBlocking {
            BackupService().startBackup()
        }
        return@application
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "ShadowSync",
        state = rememberWindowState(size = DpSize(840.dp, 640.dp))
    ) {
        BackupToolUI()
    }
}