package com.softstudio.chat.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.softstudio.chat.ui.screens.onBoarding.AuthenticationViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun generateChatId(uid1: String,uid2: String): String{
    return listOf(uid1,uid2).sorted().joinToString("_")
}

@Composable
inline fun <reified T : ViewModel> getSharedViewModel(
    navController: NavController,
    route: String
): T {
    // Grab the backstack entry of the parent navigation graph
    val navBackStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(route)
    }
    return viewModel(viewModelStoreOwner = navBackStackEntry)
}

@Composable
fun NavBackStackEntry.sharedViewModel(navController: NavController): AuthenticationViewModel {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatChatTimestamp(timestamp: Long): String {
    val messageTime = Instant.ofEpochMilli(timestamp)
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()

    val now = LocalDateTime.now()
    val startOfToday = now.toLocalDate().atStartOfDay()

    return when {
        // 1. Today: Show time only (e.g., 10:40)
        messageTime.isAfter(startOfToday) -> {
            messageTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        // 2. Yesterday: Show "Yesterday"
        messageTime.isAfter(startOfToday.minusDays(1)) -> {
            "Yesterday"
        }

        // 3. Under a week: Show Day Name (e.g., Monday)
        messageTime.isAfter(startOfToday.minusDays(7)) -> {
            messageTime.format(DateTimeFormatter.ofPattern("EEEE"))
        }

        // 4. Over a week: Show Date (e.g., 13/02/26)
        else -> {
            messageTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
        }
    }
}