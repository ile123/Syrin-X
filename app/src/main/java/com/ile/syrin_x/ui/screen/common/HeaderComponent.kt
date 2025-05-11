package com.ile.syrin_x.ui.screen.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.ui.icon.LogoutIcon
import com.ile.syrin_x.ui.navigation.NavigationGraph
import com.ile.syrin_x.viewModel.HeaderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderComponent(
    navHostController: NavHostController,
    headerViewModel: HeaderViewModel = hiltViewModel()
) {

    fun logoutUser() {
        headerViewModel.logout()
        navHostController.navigate(NavigationGraph.LoginScreen.route) {
            popUpTo(NavigationGraph.LoginScreen.route) {
                inclusive = true
            }
        }
    }

    TopAppBar(
        title = { },
        windowInsets = WindowInsets(0.dp, 20.dp, 0.dp, 0.dp),
        colors = TopAppBarDefaults
            .topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        actions = {
            IconButton(onClick = { navHostController.navigate(NavigationGraph.SearchScreen.route) }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            IconButton(onClick = { logoutUser() }) {
                Icon(
                    imageVector = LogoutIcon,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
    )
}
