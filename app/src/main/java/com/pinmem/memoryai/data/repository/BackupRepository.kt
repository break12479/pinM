package com.pinmem.memoryai.data.repository

import android.content.Context
import android.net.Uri
import com.pinmem.memoryai.data.local.database.MemoryDatabase
import com.pinmem.memoryai.data.model.BackupInfo
import com.pinmem.memoryai.util.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

/**
 * 备份 Repository
 */
class BackupRepository(
    private val context: Context,
    private val database: MemoryDatabase
) {

    /**
     * 导出备份
     */
    suspend fun exportBackup(destinationUri: Uri): Result<BackupInfo> = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(context.getExternalFilesDir(null), "backup")
            backupDir.mkdirs()

            val backupFile = File(backupDir, "memoryai_backup_${System.currentTimeMillis()}.db")

            // 复制数据库文件
            val dbFile = context.getDatabasePath("memoryai_database")
            dbFile.copyTo(backupFile, overwrite = true)

            // 计算校验和
            val checksum = calculateMD5(backupFile)

            // 获取记录数量
            val recordCount = database.memoryDao().getCount()

            val backupInfo = BackupInfo(
                path = backupFile.absolutePath,
                backupTime = System.currentTimeMillis(),
                recordCount = recordCount,
                fileSize = backupFile.length(),
                checksum = checksum
            )

            // 复制到目标 URI
            context.contentResolver.openOutputStream(destinationUri)?.use { output ->
                backupFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            AppLogger.i("Backup exported to $destinationUri")
            Result.success(backupInfo)
        } catch (e: Exception) {
            AppLogger.e("Export backup failed", e)
            Result.failure(e)
        }
    }

    /**
     * 导入备份
     */
    suspend fun importBackup(
        sourceUri: Uri,
        onProgress: (Int) -> Unit = {}
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val tempFile = File(context.cacheDir, "restore_temp_${System.currentTimeMillis()}.db")

            // 从 URI 复制到临时文件
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            onProgress(50)

            // 验证校验和（可选）
            // val checksum = calculateMD5(tempFile)

            // 关闭数据库连接
            database.close()

            // 替换数据库文件
            val dbFile = context.getDatabasePath("memoryai_database")
            tempFile.copyTo(dbFile, overwrite = true)

            onProgress(100)

            AppLogger.i("Backup imported from $sourceUri")
            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Import backup failed", e)
            Result.failure(e)
        }
    }

    /**
     * 计算 MD5 校验和
     */
    private fun calculateMD5(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * 获取备份目录
     */
    fun getBackupDir(): File {
        val dir = File(context.getExternalFilesDir(null), "backup")
        dir.mkdirs()
        return dir
    }
}
