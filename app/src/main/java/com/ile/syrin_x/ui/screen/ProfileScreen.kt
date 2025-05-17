package com.ile.syrin_x.ui.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.auth.AuthResult
import com.ile.syrin_x.R
import com.ile.syrin_x.data.model.UserInfo
import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.data.model.entity.PreviouslyPlayedTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.FavoriteTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.navigation.NavigationGraph
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.EditProfileDialog
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.ProfileViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }

    val userInfo by profileViewModel.userInfo
    val profileImage by profileViewModel.userProfileImage

    val isUserPremium by profileViewModel.isUserPremium.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileViewModel.changeUserProfile(uri, context)
    }

    LaunchedEffect(Unit) {
        profileViewModel.getUserInfo()
    }

    Scaffold(
        topBar = {
            HeaderComponent(navHostController)
        },
        bottomBar = {
            BottomBarNavigationComponent(navHostController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEditDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            ProfileContent(
                navHostController = navHostController,
                profileFlow = profileViewModel.profileFlow,
                onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } },
                userInfo = userInfo,
                profileImageUri = profileImage,
                onChangeProfilePicture = { launcher.launch("image/*") },
                previouslyPlayedTracks = profileViewModel.previouslyPlayedTracks,
                favoriteArtists = profileViewModel.favoriteArtists,
                isUserPremium = isUserPremium,
                favoriteTracks = profileViewModel.favoriteTracks
            )
        }

        if (showEditDialog) {
            EditProfileDialog(
                currentUsername = userInfo.userName,
                currentFullName = userInfo.fullName,
                onDismiss = { showEditDialog = false },
                onConfirm = { newUser, newFull ->
                    profileViewModel.updateUserInfo(newUser, newFull)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
private fun ProfileContent(
    navHostController: NavHostController,
    profileFlow: MutableSharedFlow<Response<Any>>,
    onError: (String) -> Unit,
    userInfo: UserInfo,
    isUserPremium: Boolean,
    profileImageUri: String,
    onChangeProfilePicture: () -> Unit,
    previouslyPlayedTracks: List<PreviouslyPlayedTrack>,
    favoriteArtists: List<FavoriteArtist>,
    favoriteTracks: List<FavoriteTrack>
) {
    val painter = rememberAsyncImagePainter(
        model = profileImageUri.ifBlank { R.drawable.default_profile_picture }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ProfileHeader(
                userInfo = userInfo,
                imagePainter = painter,
                onChangeProfilePicture = onChangeProfilePicture,
                isUserPremium = isUserPremium,
                onGetPremium = { navHostController.navigate(NavigationGraph.PaymentScreen.route) }
            )
        }

        item {
            SectionHeader("Previously Played Tracks")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                if (previouslyPlayedTracks.isEmpty()) {
                    Text(
                        "No previously played tracks found.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.TopCenter)
                    ) {
                        itemsIndexed(
                            previouslyPlayedTracks,
                            key = { _, t -> t.trackId }
                        ) { _, track ->
                            Text(
                                "• ${track.title ?: "Unknown Track"} (${track.artists?.get(0)})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            SectionHeader("Favorite Artists")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                if (favoriteArtists.isEmpty()) {
                    Text(
                        "No favorite artists found.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.TopCenter)
                    ) {
                        itemsIndexed(
                            favoriteArtists,
                            key = { _, a -> a.id }
                        ) { _, artist ->
                            Text(
                                "• ${artist.name}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            SectionHeader("Favorite Tracks")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                if (favoriteTracks.isEmpty()) {
                    Text(
                        "No favorite tracks found.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.TopCenter)
                    ) {
                        itemsIndexed(
                            favoriteTracks,
                            key = { _, t -> t.favoriteTrackId }
                        ) { _, track ->
                            Text(
                                "• ${track.name} (${track.artist})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    ProfileState(
        profileFlowState = profileFlow,
        onError = onError
    )
}

@Composable
private fun ProfileHeader(
    userInfo: UserInfo,
    isUserPremium: Boolean,
    imagePainter: Painter,
    onChangeProfilePicture: () -> Unit,
    onGetPremium: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Created Playlists",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = imagePainter,
            contentDescription = "Profile",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { onChangeProfilePicture() },
            contentScale = ContentScale.Crop
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = userInfo.userName,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = userInfo.fullName,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = userInfo.email,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        if(!isUserPremium) {
            Text(
                text = "Get premium",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onGetPremium() }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
private fun ProfileState(
    profileFlowState: MutableSharedFlow<Response<Any>>,
    onError: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(profileFlowState) {
        profileFlowState.collect { response ->
            when (response) {
                is Response.Loading -> isLoading = true
                is Response.Error -> {
                    isLoading = false
                    onError(response.message)
                }

                is Response.Success -> isLoading = false
            }
        }
    }
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            MyCircularProgress()
        }
    }
}