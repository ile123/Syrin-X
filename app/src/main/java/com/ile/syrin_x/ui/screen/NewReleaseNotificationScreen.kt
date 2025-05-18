package com.ile.syrin_x.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.viewModel.NewReleaseNotificationViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NewReleaseNotificationScreen(
    navHostController: NavHostController,
    viewModel: NewReleaseNotificationViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getAllNotifications()
    }

    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = { HeaderComponent(navHostController) },
        bottomBar = { BottomBarNavigationComponent(navHostController) }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(notifications, key = { "${it.artistId}-${it.trackId}" }) { notification ->
                NotificationRow(
                    notification = notification,
                    onClick = { viewModel.markAsSeen(notification.trackId) }
                )
            }
        }
    }
}

@Composable
fun NotificationRow(
    notification: NewReleaseNotificationEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (notification.seen) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = Instant.ofEpochMilli(notification.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
