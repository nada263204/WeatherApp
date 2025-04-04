package com.example.weatherapp.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.data.repo.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {
    val favoritePlaces: StateFlow<List<FavoritePlace>> = repository.getFavoritePlaces()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addFavoritePlace(place: FavoritePlace) {
        viewModelScope.launch {
            repository.saveFavoritePlace(place)
        }
    }

    fun removeFavoritePlace(place: FavoritePlace) {
        viewModelScope.launch {
            repository.deleteFavoritePlace(place)
        }
    }
}

class FavoriteViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
