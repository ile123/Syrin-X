package com.ile.syrin_x.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.viewModel.HeaderViewModel
import com.ile.syrin_x.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    headerViewModel: HeaderViewModel = hiltViewModel()
) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HeaderComponent(navHostController, headerViewModel)
            },
            bottomBar = {
                BottomBarNavigationComponent(navHostController)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.secondary)
                    .verticalScroll(rememberScrollState())
            ) {

            }
        }
}