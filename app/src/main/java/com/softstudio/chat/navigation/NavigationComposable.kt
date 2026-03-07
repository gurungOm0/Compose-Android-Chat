package com.softstudio.chat.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.softstudio.chat.ui.screens.chat.Chat
import com.softstudio.chat.ui.screens.chat.ChatViewModel
import com.softstudio.chat.ui.screens.home.Home
import com.softstudio.chat.ui.screens.home.HomeViewModel
import com.softstudio.chat.ui.screens.onBoarding.AuthenticationScreen
import com.softstudio.chat.ui.screens.onBoarding.AuthenticationViewModel
import com.softstudio.chat.ui.screens.onBoarding.OnBoarding
import com.softstudio.chat.util.sharedViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationComposable(navController: NavHostController, innerPaddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = AuthenticationScreenDes.route,
        modifier = Modifier.padding(innerPaddingValues)
    ) {
        navigation(startDestination = AuthenticationScreenDes.route, route = AuthenticationGroupDes.route) {
            composable(
                route = AuthenticationScreenDes.route
            ) { backStackEntry ->
                val viewModel: AuthenticationViewModel = backStackEntry.sharedViewModel(navController)
                AuthenticationScreen(navController,viewModel)
            }
            composable(
                route = OnBoardingDes.route
            ) { backStackEntry ->
                val viewModel: AuthenticationViewModel = backStackEntry.sharedViewModel(navController)
                OnBoarding(navController,viewModel)
            }
        }

        composable(
            route = HomeDes.route
        ){
            Home(navController)
        }

        composable(
            route = ChatDes.route+"/{chatId}",
            arguments = listOf(navArgument("chatId"){ type = NavType.StringType })
        ){ backStackEntry ->
            val item = backStackEntry.arguments?.getString("chatId")
            Chat(navHostController = navController, conversationId = item ?: "")
        }
    }
}