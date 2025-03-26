package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.weatherapp.utiles.WeatherTypeConverters

@Database(entities = [FavoritePlace::class], version = 1, exportSchema = false)
@TypeConverters(WeatherTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritePlaceDao(): FavoritePlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
