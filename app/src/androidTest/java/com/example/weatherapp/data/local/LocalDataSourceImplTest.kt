package com.example.weatherapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherapp.data.models.*
import com.example.weatherapp.notifications.NotificationDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalDataSourceImplTest {

    private lateinit var localDataSource: LocalDataSourceImpl
    private lateinit var favoritePlaceDao: FavoritePlaceDao
    private lateinit var notificationDao: NotificationDao
    private lateinit var homeScreenDao: HomeScreenDao
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        favoritePlaceDao = database.favoritePlaceDao()
        homeScreenDao = database.homeScreenDao()
        notificationDao = FakeNotificationDao()

        localDataSource = LocalDataSourceImpl(favoritePlaceDao, notificationDao, homeScreenDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavoritePlace_addsPlaceSuccessfully() = runTest {
        val place = createFavoritePlace("New York")

        localDataSource.insertFavoritePlace(place)
        val places = localDataSource.getAllFavoritePlaces().first()

        assertTrue(places.isNotEmpty())
    }

    @Test
    fun getAllFavoritePlaces_returnsAllInsertedPlaces() = runTest {
        val place1 = createFavoritePlace("New York")
        val place2 = createFavoritePlace("Port Said")

        localDataSource.insertFavoritePlace(place1)
        localDataSource.insertFavoritePlace(place2)

        val places = localDataSource.getAllFavoritePlaces().first()

        assertTrue(places.size == 2)
    }

    private fun createFavoritePlace(cityName: String) = FavoritePlace(
        cityName = cityName,
        coord = Coord(-74.006, 40.7128),
        main = Main(20.0, 18.0, 60, 1012, 22.0),
        clouds = Clouds(75),
        weather = listOf(WeatherItem("01d", "clear sky", "Clear")),
        wind = Wind(5.0),
        forecast = emptyList(),
        city = City(cityName, Coord(-74.006, 40.7128))
    )
}
