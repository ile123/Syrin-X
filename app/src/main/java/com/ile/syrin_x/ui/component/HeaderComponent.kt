package com.ile.syrin_x.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ile.syrin_x.ui.theme.SoftTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderComponent() {
    TopAppBar(
        title = {
            Text("Home")
        },
        modifier = Modifier.padding(top = 16.dp),
        windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        colors = TopAppBarDefaults
            .topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Preview
@Composable
fun HeaderComponentPreview() {
    HeaderComponent()
}
