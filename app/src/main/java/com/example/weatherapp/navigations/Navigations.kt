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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.utiles.LocationUtils
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.home.view.HomeScreen
import com.example.weatherapp.home.viewModel.WeatherViewModel
import com.example.weatherapp.home.viewModel.WeatherViewModelFactory
import com.example.weatherapp.notifications.NotificationScreen
import com.example.weatherapp.favorite.FavoriteScreen
import com.example.weatherapp.favorite.FavoriteViewModel
import com.example.weatherapp.favorite.FavoriteViewModelFactory
import com.example.weatherapp.setting.MapScreen
import com.example.weatherapp.setting.SettingsScreen
import com.example.weatherapp.setting.SettingsViewModel
import com.example.weatherapp.setting.SettingsViewModelFactory
import com.example.weatherapp.ui.theme.Purple80
import com.example.weatherapp.ui.theme.purpleDark
import com.example.weatherapp.utils.NetworkUtils
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    latitude: Double,
    longitude: Double,
    weatherRepository: WeatherRepository,
    locationRepository: LocationRepository
) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context,
        LocationRepository(locationUtils = LocationUtils(context))))

    val weatherViewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory(
        weatherRepository,
        locationRepository,
        settingsViewModel,context
    ))

    val favoriteViewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModelFactory(weatherRepository))


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

            NavHostContainer(navController, weatherViewModel, favoriteViewModel, Modifier.padding(innerPadding))
        }
    }
    NetworkObserver()
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
    val currentRoute = currentBackStackEntry?.destination?.route

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
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
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
    favViewModel: FavoriteViewModel,
    modifier: Modifier
) {
    NavHost(
        navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(viewModel, context = LocalContext.current) }
        composable("search") { FavoriteScreen(navController, favViewModel,viewModel) }
        composable("notification") { NotificationScreen() }
        composable("settings") { SettingsScreen(
            navController,
            weatherViewModel = viewModel
        ) }
        composable("map") { MapScreen(navController, viewModel) }
    }
}


@Composable
fun NetworkObserver() {
    val context = LocalContext.current
    var isNetworkAvailable by remember { mutableStateOf(NetworkUtils.isNetworkAvailable(context)) }
    val coroutineScope = rememberCoroutineScope()
    var alertShown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (isNetworkAvailable && !alertShown) {
            val currentStatus = NetworkUtils.isNetworkAvailable(context)
            if (!currentStatus) {
                isNetworkAvailable = false
            }
            delay(5000)
        }
    }

    if (!isNetworkAvailable) {
        NetworkAlertDialog(onDismiss = { isNetworkAvailable = true })
    }
}
@Composable
fun NetworkAlertDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no))
                val progress by animateLottieCompositionAsState(composition)


                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "No Internet Connection!,Check Network to get fresh data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}




data class BottomNavItem(val route: String, val icon: ImageVector)
