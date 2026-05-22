package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.FoodRepository
import com.example.ui.FoodViewModel
import com.example.ui.FoodViewModelFactory
import com.example.ui.screens.CartScreen
import com.example.ui.screens.DetailsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Instantiate local Room database
    val database = Room.databaseBuilder(
      applicationContext,
      AppDatabase::class.java,
      "food_delivery_database"
    ).fallbackToDestructiveMigration().build()

    // Instantiate Repository
    val repository = FoodRepository(database.foodDao)

    setContent {
      MyApplicationTheme {
        val viewModel: FoodViewModel = viewModel(
          factory = FoodViewModelFactory(repository)
        )
        
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          val navController = rememberNavController()
          NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
          ) {
            // 1. Home list of Restaurants sorted by Cuisines, Meal-types, etc.
            composable("home") {
              HomeScreen(
                viewModel = viewModel,
                onRestaurantClick = { restaurantId ->
                  navController.navigate("details/$restaurantId")
                },
                onCartClick = {
                  navController.navigate("cart")
                },
                onOrdersClick = {
                  // Direct to tracking the latest ordered item
                  val lastOrder = viewModel.allOrders.value.firstOrNull()
                  if (lastOrder != null) {
                    navController.navigate("tracking/${lastOrder.id}")
                  }
                }
              )
            }

            // 2. Restaurant details with their meal options
            composable(
              route = "details/{restaurantId}",
              arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
            ) { backStackEntry ->
              val restaurantId = backStackEntry.arguments?.getString("restaurantId").orEmpty()
              DetailsScreen(
                restaurantId = restaurantId,
                viewModel = viewModel,
                onBackClick = {
                  navController.popBackStack()
                },
                onCartClick = {
                  navController.navigate("cart")
                }
              )
            }

            // 3. Cart with choosing delivery service and paystack/momo selection
            composable("cart") {
              CartScreen(
                viewModel = viewModel,
                onBackClick = {
                  navController.popBackStack()
                },
                onOrderPlaced = { orderId ->
                  navController.navigate("tracking/$orderId") {
                    popUpTo("home") { saveState = true }
                  }
                }
              )
            }

            // 4. Order status tracking screen with nice scooter visualization
            composable(
              route = "tracking/{orderId}",
              arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) { backStackEntry ->
              val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
              TrackingScreen(
                orderId = orderId,
                viewModel = viewModel,
                onBackClick = {
                  navController.popBackStack()
                }
              )
            }
          }
        }
      }
    }
  }
}

