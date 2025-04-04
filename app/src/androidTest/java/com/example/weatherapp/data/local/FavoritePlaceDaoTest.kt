package com.example.weatherapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Clouds
import com.example.weatherapp.data.models.Coord
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.WeatherItem
import com.example.weatherapp.data.models.Wind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.rules.TestRule

class FavoritePlaceDaoTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var dao: FavoritePlaceDao
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.favoritePlaceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavoritePlace_and_getAllFavoritePlaces() = runBlocking {
        val place = FavoritePlace(
            cityName = "New York",
            coord = Coord(-74.006, 40.7128),
            main = Main(20.0, 18.0, 60, 1012, 22.0),
            clouds = Clouds(75),
            weather = listOf(WeatherItem("01d", "clear sky", "Clear")),
            wind = Wind(5.0),
            forecast = emptyList(),
            city = City("New York", Coord(-74.006, 40.7128))
        )
        dao.insertFavoritePlace(place)

        val places = dao.getAllFavoritePlaces().first()

        Assert.assertTrue(places.contains(place))
    }

    @Test
    fun insertFavoritePlace_and_getFavoritePlaceByName() = runBlocking {
        val place = FavoritePlace(
            cityName = "Portsaid",
            coord = Coord(-74.006, 40.7128),
            main = Main(20.0, 18.0, 60, 1012, 22.0),
            clouds = Clouds(75),
            weather = listOf(WeatherItem("01d", "clear sky", "Clear")),
            wind = Wind(5.0),
            forecast = emptyList(),
            city = City("New York", Coord(-74.006, 40.7128))
        )
        dao.insertFavoritePlace(place)

        val retrievedPlace = dao.getFavoritePlaceByName("Portsaid").first()

        Assert.assertNotNull(retrievedPlace)
    }
}
