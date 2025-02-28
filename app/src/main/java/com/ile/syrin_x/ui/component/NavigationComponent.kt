package com.ile.syrin_x.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NavigationComponent() {

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = {  }) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Home",
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Search",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Library",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Library",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Premium",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Premium",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Login",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Login",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun NavigationComponentPreview() {
    NavigationComponent(
    )
}