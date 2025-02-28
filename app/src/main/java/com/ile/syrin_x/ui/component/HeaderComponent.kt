package com.ile.syrin_x.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ile.syrin_x.viewModel.HeaderViewModel
import com.ile.syrin_x.viewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderComponent(
    headerViewModel: HeaderViewModel
) {
    TopAppBar(
        title = {
            Text(text = "Home",
                color = MaterialTheme.colorScheme.surface
            )
        },
        modifier = Modifier.padding(top = 16.dp),
        windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        colors = TopAppBarDefaults
            .topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        actions = {
            IconButton(onClick = { headerViewModel.logout() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
    )
}
