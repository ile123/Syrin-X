package com.ile.syrin_x.ui.screen.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
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
import androidx.navigation.NavHostController
import com.ile.syrin_x.ui.icon.CollectionIcon
import com.ile.syrin_x.ui.icon.MusicNoteIcon
import com.ile.syrin_x.ui.navigation.NavigationGraph

@Composable
fun BottomBarNavigationComponent(navHostController: NavHostController) {

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
                        CollectionIcon,
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
                IconButton(onClick = { navHostController.navigate(NavigationGraph.MusicSourceScreen.route) }) {
                    Icon(
                        MusicNoteIcon,
                        contentDescription = "Music Source",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Source",
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
                        contentDescription = "Profile",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Profile",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

}