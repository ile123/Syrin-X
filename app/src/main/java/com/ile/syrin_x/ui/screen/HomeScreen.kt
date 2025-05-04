package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ile.syrin_x.data.model.deezer.MusicGenre
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.HeaderViewModel
import com.ile.syrin_x.viewModel.HomeViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    headerViewModel: HeaderViewModel = hiltViewModel(),
) {
    val homeState by homeViewModel.homeFlow.collectAsState(initial = Response.Loading)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeaderComponent(navHostController, headerViewModel)
        },
        bottomBar = {
            BottomBarNavigationComponent(navHostController)
        }
    ) { innerPadding ->
        when (homeState) {
            is Response.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    MyCircularProgress()
                }
            }

            is Response.Error -> {
                val message = (homeState as Response.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $message")
                }
            }

            is Response.Success<*> -> {
                HomeContent(
                    paddingValues = innerPadding,
                    navHostController = navHostController,
                    genres = homeViewModel.categories
                )
            }
        }
    }
}


@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    navHostController: NavHostController,
    genres: List<MusicGenre>
) {
    val genresLazyListState = rememberLazyListState()

    LazyColumn(
        state = genresLazyListState,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        items(genres, key = { it.id }) { genre ->
            MusicGenreRow(musicGenre = genre, navHostController)
        }
    }
}


@Composable
fun MusicGenreRow(
    musicGenre: MusicGenre,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { navHostController.navigate("trending_songs_by_genre_screen/${musicGenre.id}") })
            .padding(8.dp)
    ) {
        AsyncImage(
            model = musicGenre.picture,
            contentDescription = musicGenre.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = musicGenre.name,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}