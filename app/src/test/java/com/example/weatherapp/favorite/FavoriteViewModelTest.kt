package com.example.weatherapp.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Clouds
import com.example.weatherapp.data.models.Coord
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.WeatherItem
import com.example.weatherapp.data.models.Wind
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.favorite.viewModel.FavoriteViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoriteViewModelTest {

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var repository: WeatherRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    //coroutines in same thread

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        coEvery { repository.getFavoritePlaces() } returns flowOf(emptyList())
        favoriteViewModel = FavoriteViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun removeFavoritePlace_deletesFromRepository() = runTest {
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

        favoriteViewModel.removeFavoritePlace(place)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteFavoritePlace(place) }
    }

    @Test
    fun favoritePlaces_initiallyEmpty() = runTest {
        val result = favoriteViewModel.favoritePlaces.first()
        assertTrue(result.isEmpty())
    }
}
