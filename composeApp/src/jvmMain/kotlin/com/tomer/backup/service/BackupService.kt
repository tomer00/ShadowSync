package com.tomer.backup.service

import com.tomer.backup.data.FileMetaData
import com.tomer.backup.data.daos.H2FileMetaDataRepository
import com.tomer.backup.data.daos.H2SourcePathRepository
import com.tomer.backup.data.remote.RemoteRepo
import com.tomer.backup.data.remote.SambaClient
import com.tomer.backup.utils.sha256OfFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.util.regex.Pattern
import kotlin.jvm.optionals.getOrNull

//Entry Point for backups
class BackupService {
    //    val thisDeviceDir = AppProperties.getProperty("app.dir") ?: "test"
    val thisDeviceDir = "test"
    val sourcesDir = H2SourcePathRepository.getAllSourcePaths()
    val client: RemoteRepo = SambaClient(thisDeviceDir)

    fun File.saveToMetaDataDb(sha: String? = null) {
        H2FileMetaDataRepository.saveFileMetaData(
            FileMetaData(
                this.absolutePath, this.length(),
                this.lastModified(),
                sha ?: sha256OfFile(this)
            )
        )
    }

    fun processFile(f: File, relDir: File? = null) {
        val metaData = H2FileMetaDataRepository.getFileMetaDataByPath(f.absolutePath)
            .getOrNull()
        val relPath = if (relDir == null) "$thisDeviceDir/${f.name}"
        else "$thisDeviceDir/${relDir.name}${f.absolutePath.removePrefix(relDir.absolutePath)}"
        //First time seeing this file
        //Not present in metadata db
        if (metaData == null) {
            f.saveToMetaDataDb()
            if (client.isFilePresent(relPath).not())
                client.uploadFile(relPath, f.inputStream())
            return
        }
        //File is in metadata db
        //File absent on remote upload new copy
//        if (client.isFilePresent(relPath).not()) {
//            client.uploadFile(relPath, f.inputStream())
//            f.saveToMetaDataDb()
//            return
//        }

        //Check local file against metadata
        if (f.lastModified() == metaData.lastModified)
            return
        if (f.length() != metaData.lastSize) {
            f.saveToMetaDataDb()
            client.uploadFile(relPath, f.inputStream())
            return
        }
        val shaOfFile = sha256OfFile(f)
        if (shaOfFile != metaData.shaDigest) {
            f.saveToMetaDataDb(shaOfFile)
            client.uploadFile(relPath, f.inputStream())
        }
    }

    suspend fun startBackup() {
        withContext(Dispatchers.IO) {
            sourcesDir.map { path ->
                launch {
                    val f = File(path.absolutePath)
                    if (path.isFile) {
                        processFile(f)
                        return@launch
                    }
                    //Source is a directory
                    val outerDir = File(path.absolutePath)
                    if (client.isDirPresent("$thisDeviceDir/" + outerDir.name).not()) {
                        println("DELETING ALL : ${outerDir.absolutePath}")
                        H2FileMetaDataRepository.deleteFileMetaDataOfDir(outerDir.absolutePath)
                        println("PRINTING ALL--")
                        H2FileMetaDataRepository.getAllFileMeta().forEach {
                            println(it)
                        }
                    }
                    val patterns = loadIgnoreFile(outerDir)
                    client.createDir(outerDir.name)
                    //recursively process all files and folders
                    //process them async
                    val allFiles = outerDir.walkTopDown().toList()
                    allFiles.forEach { f ->
                        if (f.isFile.not()) {
                            client.createDir(f.toRelativeString(outerDir.parentFile))
                        }
                    }
                    allFiles.map { f ->
                        launch {
                            if (f.isFile) {
                                if (patterns.none { pattern -> pattern.matcher(f.absolutePath).matches() })
                                    processFile(f, outerDir)
                            }
                        }
                    }.joinAll()
                }
            }.joinAll()
            client.closeConnection()
        }
    }

    fun loadIgnoreFile(dir: File): List<Pattern> {
        val ignoreFile = File(dir, ".backupignore")
        if (!ignoreFile.exists()) return emptyList()

        return Files.readAllLines(ignoreFile.toPath())
            .mapNotNull { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) null
                else {
                    // Glob-like to regex
                    val regex = trimmed
                        .replace(".", "\\.")
                        .replace("*", ".*")
                        .replace("?", ".")
                    Pattern.compile(regex)
                }
            }
    }
}