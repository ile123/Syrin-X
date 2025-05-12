package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.R
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.icon.Book_icon
import com.ile.syrin_x.ui.navigation.NavigationGraph
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
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
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeaderComponent(navHostController)
        },
        bottomBar = {
            BottomBarNavigationComponent(navHostController)
        }
    ) { paddingValues ->
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
    val keywordText = remember { mutableStateOf("") }
    val musicSourceSelect = remember { mutableStateOf(GlobalContext.loggedInMusicSources.first()) }
    var showSearchInvalidDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val search = {
        if (keywordText.value.isBlank()) {
            showSearchInvalidDialog = true
        } else {
            onSearch(keywordText.value, musicSourceSelect.value)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Search",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Please select a source and enter a keyword.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = keywordText.value,
                    onValueChange = { keywordText.value = it },
                    label = { Text("Keyword") },
                    placeholder = { Text("Type to search…") },
                    singleLine = true,
                    leadingIcon = { Icon(Book_icon, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                MusicSourceDropdownMenu(
                    selectedOption = musicSourceSelect.value,
                    onSelect = {
                        musicSourceSelect.value = it
                        onMusicSourceChange()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = search,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Search")
                }
            }
        }

        SearchState(
            searchFlowState = searchFlowState,
            onSuccess = { searchSuccess() },
            onError = { error -> searchError(error) },
        )

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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSourceDropdownMenu(
    selectedOption: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = GlobalContext.loggedInMusicSources
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            label = { Text("Music Source") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
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
            when (it) {
                is Response.Loading -> {
                    Log.i("Search state", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Search state", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("Search state", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}