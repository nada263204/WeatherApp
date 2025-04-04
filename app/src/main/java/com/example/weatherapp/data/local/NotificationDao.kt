package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY id DESC")
     fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications WHERE time = :time")
    suspend fun deleteNotificationByTime(time: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Int)

    @Query("DELETE FROM notifications WHERE (date || ' ' || time) < :currentTime")
    suspend fun deleteExpiredNotifications(currentTime: String)

}
