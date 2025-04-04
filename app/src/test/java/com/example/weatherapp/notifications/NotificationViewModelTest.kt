package com.example.weatherapp.notifications

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherapp.data.local.NotificationDao
import com.example.weatherapp.data.local.NotificationDatabase
import com.example.weatherapp.data.repo.FakeWeatherRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.notifications.viewModel.NotificationViewModel
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class NotificationViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: NotificationDatabase
    private lateinit var dao: NotificationDao
    private lateinit var viewModel: NotificationViewModel
    private val application = ApplicationProvider.getApplicationContext<Application>()
    lateinit var repo : WeatherRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            application, NotificationDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.notificationDao()
        repo= FakeWeatherRepository()
        viewModel = NotificationViewModel(application)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun addNotification_retrievesNotification() = runTest {
        // Given
        val date = "2025-04-01"
        val time = "12:00"

        // When
        viewModel.addNotification(date, time)

        delay(500)

        // Then
        val result = viewModel.notifications.first()
        assertTrue(result.isNotEmpty())
        assertEquals(date, result.first().date)
        assertEquals(time, result.first().time)
    }

    @Test
    fun deleteNotification_notificationIsDeleted() = runTest {
        // Given
        val date = "2025-04-01"
        val time = "12:00"
        viewModel.addNotification(date, time)

        delay(500)

        // When
        viewModel.deleteNotification(time)

        delay(500)

        // Then
        val notifications = dao.getAllNotifications().first()
        assertTrue(notifications.isEmpty())
    }
}
