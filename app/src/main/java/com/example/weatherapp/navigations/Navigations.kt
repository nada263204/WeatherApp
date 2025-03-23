package com.example.weatherapp.navigations

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.weatherapp.R
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.home.view.screen.HomeScreen
import com.example.weatherapp.home.viewModel.WeatherViewModel
import com.example.weatherapp.home.viewModel.WeatherViewModelFactory
import com.example.weatherapp.notifications.NotificationScreen
import com.example.weatherapp.search.SearchScreen
import com.example.weatherapp.setting.SettingsScreen
import com.example.weatherapp.ui.theme.Purple80
import com.example.weatherapp.ui.theme.purpleDark

@Composable
fun MainScreen(
    latitude: Double,
    longitude: Double,
    weatherRepository: WeatherRepository,
    locationRepository: LocationRepository
) {
    val navController = rememberNavController()

    val viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(weatherRepository, locationRepository)
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            NavHostContainer(navController, viewModel, Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", Icons.Filled.Home),
        BottomNavItem("search", Icons.Filled.Search),
        BottomNavItem("notification", Icons.Filled.Notifications),
        BottomNavItem("settings", Icons.Filled.Settings)
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"

    Surface(
        color = purpleDark,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        NavigationBar(
            containerColor = purpleDark,
            contentColor = Purple80
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = null
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Purple80,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    viewModel: WeatherViewModel,
    modifier: Modifier
) {
    NavHost(
        navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(viewModel) }
        composable("search") { SearchScreen() }
        composable("notification") { NotificationScreen() }
        composable("settings") { SettingsScreen() }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector)
