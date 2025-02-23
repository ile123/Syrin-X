package com.ile.syrin_x.screen

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
import com.ile.syrin_x.ui.component.HeaderComponent
import com.ile.syrin_x.ui.component.NavigationComponent
import com.ile.syrin_x.ui.theme.SyrinXTheme

@Composable
fun HomeScreen() {
    SyrinXTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HeaderComponent()
            },
            bottomBar = {
                NavigationComponent()
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
}