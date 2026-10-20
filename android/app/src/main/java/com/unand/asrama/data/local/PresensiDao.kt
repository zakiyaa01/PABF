package com.unand.asrama.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unand.asrama.data.local.entity.PresensiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresensiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresensi(presensi: PresensiEntity): Long

    @Query("SELECT * FROM presensi_local WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getUnsyncedPresensi(): List<PresensiEntity>

    @Query("UPDATE presensi_local SET isSynced = 1, status = 'Hadir (Tersinkron)' WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Query("SELECT * FROM presensi_local ORDER BY createdAt DESC")
    fun getAllLocalHistory(): Flow<List<PresensiEntity>>

    @Query("SELECT COUNT(*) FROM presensi_local WHERE isSynced = 0")
    suspend fun getUnsyncedCount(): Int
}
