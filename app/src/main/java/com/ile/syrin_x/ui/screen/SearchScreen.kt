package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.R
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.icon.Book_icon
import com.ile.syrin_x.ui.navigation.NavigationGraph
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.SearchViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.ile.syrin_x.ui.screen.common.MyAlertDialog
import com.ile.syrin_x.utils.GlobalContext

@Composable
fun SearchScreen(
    navHostController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    fun search(keyword: String, musicSource: String) {
        searchViewModel.searchAll(keyword, musicSource)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Image(
            painter = painterResource(id = R.drawable.background_image_1),
            contentDescription = "Background image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Content(
            paddingValues = paddingValues,
            searchFlowState = searchViewModel.searchFlow,
            onSearch = { keyword: String, musicSource: String -> search(keyword, musicSource) },
            searchSuccess = { navHostController.navigate(NavigationGraph.SearchResultScreen.route) },
            searchError = { errorMessage ->
                scope.launch {
                    hostState.showSnackbar(errorMessage)
                }
            },
            onMusicSourceChange = { searchViewModel.clearPreviouslyFetchedContent() }
        )
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    searchFlowState: MutableSharedFlow<Response<Any>>,
    onSearch: (String, String) -> Unit,
    searchSuccess: () -> Unit,
    searchError: (error: String) -> Unit,
    onMusicSourceChange: () -> Unit
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        val keywordText = remember {
            mutableStateOf("")
        }

        val musicSourceSelect = remember {
            mutableStateOf("Spotify")
        }

        var showSearchInvalidDialog by remember {
            mutableStateOf(false)
        }

        if (showSearchInvalidDialog) {
            MyAlertDialog(
                onDismissRequest = { showSearchInvalidDialog = false },
                onConfirmation = { showSearchInvalidDialog = false },
                title = "Invalid Search Term",
                text = "Please type in something to the search field.",
                confirmButtonText = "Ok",
                dismissButtonText = "Cancel",
                cancelable = true
            )
        }

        val search = {
            val fields = mapOf(
                "keyword" to keywordText.value
            )

            val emptyField = fields.entries.find { it.value.isEmpty() || it.value.isBlank() }
            if (emptyField != null) {
                showSearchInvalidDialog = true
            } else {
                onSearch(keywordText.value, musicSourceSelect.value)
            }
        }

        Text(
            text = "Search",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp, top = 20.dp),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Please select the source and write a keyword.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp, top = 5.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 20.dp),
            value = keywordText.value,
            onValueChange = { text -> keywordText.value = text },
            label = { Text("Search") },
            singleLine = true,
            leadingIcon = { Icon(Book_icon, "keyword") },
        )
        MusicSourceDropdownMenu(
            onSelect = { musicSource ->
                run {
                    musicSourceSelect.value = musicSource
                    onMusicSourceChange()
                }
            }
        )
        Button(
            onClick = { search() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            content = { Text(text = "Search") },
        )

    }

    SearchState(
        searchFlowState = searchFlowState,
        onSuccess = { searchSuccess() },
        onError = { error -> searchError(error) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSourceDropdownMenu(
    onSelect: (musicSource: String) -> Unit
) {
    val options = GlobalContext.loggedInMusicSources
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Music Source") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        selectedOption = option
                        onSelect(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
fun SearchState(
    searchFlowState: MutableSharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        searchFlowState.collect {
            when(it) {
                is Response.Loading -> {
                    Log.i("Search state -> ", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Search state -> ", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("Search state -> ", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}