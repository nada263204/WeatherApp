package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.FakeLocalDataSource
import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.data.models.*
import com.example.weatherapp.data.remote.FakeRemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class WeatherRepositoryImplTest {

    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource()

        repository = WeatherRepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            localDataSource = fakeLocalDataSource,
            context = mock()
        )
    }

    @Test
    fun insertFavoritePlace_addsPlaceToFavorites() = runTest {
        // Given
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

        // When
        repository.saveFavoritePlace(place)
        val result = repository.getFavoritePlaces().first()

        // Then
        assertThat(result, hasItem(place))
    }

    @Test
    fun deleteFavoritePlace_removesPlaceFromFavorites() = runTest {
        // Given
        val place = FavoritePlace(
            cityName = "Los Angeles",
            coord = Coord(-118.2437, 34.0522),
            main = Main(25.0, 22.0, 55, 1010, 28.0),
            clouds = Clouds(20),
            weather = listOf(WeatherItem("02d", "few clouds", "Clouds")),
            wind = Wind(4.5),
            forecast = emptyList(),
            city = City("Los Angeles", Coord(-118.2437, 34.0522))
        )
        repository.saveFavoritePlace(place)

        // When
        repository.deleteFavoritePlace(place)
        val result = repository.getFavoritePlaces().first()

        // Then
        assertThat(result, not(hasItem(place)))
    }
}
