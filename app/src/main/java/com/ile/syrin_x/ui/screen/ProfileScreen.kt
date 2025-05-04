package com.ile.syrin_x.ui.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.ile.syrin_x.ui.screen.common.EditProfileDialog
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
    val hostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }

    val userInfo by profileViewModel.userInfo
    val profileImage = profileViewModel.userProfileImage.value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileViewModel.changeUserProfile(uri, context)
    }

    fun updateProfileInfo(userName: String, fullName: String) {
        profileViewModel.updateUserInfo(userName, fullName)
    }

    fun changeProfilePicture() {
        launcher.launch("image/*")
    }

    LaunchedEffect(Unit) {
        profileViewModel.getUserInfo()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEditDialog = true }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Image(
            painter = painterResource(id = R.drawable.background_image_1),
            contentDescription = "Background image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Content(
            navHostController = navHostController,
            paddingValues = paddingValues,
            profileFlow = profileViewModel.profileFlow,
            profileError = { errorMessage ->
                scope.launch {
                    hostState.showSnackbar(errorMessage)
                }
            },
            userInfo = userInfo,
            profileImage = profileImage,
            onChangeProfilePicture = ::changeProfilePicture,
            favoriteArtists = profileViewModel.favoriteArtists,
            favoriteTracks = profileViewModel.favoriteTracks,
            previouslyPlayedTracks = profileViewModel.previouslyPlayedTracks
        )

        if (showEditDialog) {
            EditProfileDialog(
                currentUsername = userInfo.userName,
                currentFullName = userInfo.fullName,
                onDismiss = { showEditDialog = false },
                onConfirm = { newUserName, newFullName ->
                    updateProfileInfo(newUserName, newFullName)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun Content(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    profileFlow: MutableSharedFlow<Response<Any>>,
    profileError: (error: String) -> Unit,
    userInfo: UserInfo,
    profileImage: String,
    onChangeProfilePicture: () -> Unit,
    favoriteArtists: List<FavoriteArtist>,
    favoriteTracks: List<FavoriteTrack>,
    previouslyPlayedTracks: List<PreviouslyPlayedTrack>
) {

    val profileImageUri = rememberAsyncImagePainter(
        model = profileImage.ifBlank { R.drawable.default_profile_picture }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = profileImageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { onChangeProfilePicture() },
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Username: ${userInfo.userName}", fontSize = 18.sp)
                Text("Full Name: ${userInfo.fullName}", fontSize = 18.sp)
                Text("Email: ${userInfo.email}", fontSize = 16.sp)
                Text(
                    text = "Get premium",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable {
                        navHostController.navigate(NavigationGraph.PaymentScreen.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Previously Played Tracks",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (previouslyPlayedTracks.isEmpty()) {
                    Text("No previously played tracks found.")
                } else {
                    LazyColumn {
                        itemsIndexed(previouslyPlayedTracks, key = { _, item -> item.trackId + "_previouslyPlayedTrack" }) { _, track ->
                            Text(
                                text = "• ${track.title ?: "Unknown Track"}",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Favorite Artists",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (favoriteArtists.isEmpty()) {
                    Text("No favorite artists found.")
                } else {
                    LazyColumn {
                        itemsIndexed(favoriteArtists, key = { _, item -> item.id + "_favoriteArtist" }) { _, artist ->
                            Text(
                                text = "• ${artist.name}",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Favorite Tracks",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (favoriteTracks.isEmpty()) {
                    Text("No favorite tracks found.")
                } else {
                    LazyColumn {
                        itemsIndexed(favoriteTracks, key = { _, item -> item.favoriteTrackId + "_favoriteTrack" }) { _, track ->
                            Text(
                                text = "• ${track.name} - ${track.artist}",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }


    ProfileState(
        profileFlowState = profileFlow,
        onSuccess = {},
        onError = profileError
    )
}


@Composable
fun ProfileState(
    profileFlowState: MutableSharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        profileFlowState.collect {
            when (it) {
                is Response.Loading -> isLoading.value = true
                is Response.Error -> {
                    isLoading.value = false
                    onError(it.message)
                }
                is Response.Success -> {
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}
