package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.MyAlertDialog
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.ui.theme.AppTheme
import com.ile.syrin_x.viewModel.SettingsViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val hostState = remember { SnackbarHostState() }
    val isUserPremium by settingsViewModel.isUserPremium.collectAsState()
    val currentThemeDisplay by settingsViewModel.currentTheme
        .map { it.displayName }
        .collectAsState(initial = AppTheme.SystemDefault.displayName)

    var showRestartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.checkIfUserIsPremium()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isUserPremium) {
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
                        Text("Please choose your theme:")
                        Spacer(Modifier.height(8.dp))
                        ThemeDropdownMenu(
                            selectedOption = currentThemeDisplay,
                            options = settingsViewModel.themeList,
                            onSelect = { settingsViewModel.changeAppTheme(it) }
                        )
                    }
                }
            } else {
                Text(
                    "Become a premium user in order to change app themes.",
                    Modifier.padding(16.dp)
                )
            }
        }

        if (showRestartDialog) {
            MyAlertDialog(
                onDismissRequest = { showRestartDialog = false },
                onConfirmation = { showRestartDialog = false },
                title = "Restart Needed",
                text = "Please restart the app in order for changes to take effect.",
                confirmButtonText = "Ok",
                dismissButtonText = "Cancel",
                cancelable = true
            )
        }

        SettingsState(
            flow = settingsViewModel.settingsFlow,
            onSuccess = { showRestartDialog = true },
            onError = { }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDropdownMenu(
    selectedOption: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("App Theme") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            colors = textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsState(
    flow: SharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        flow.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("Settings State", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Settings State", it.message)
                    isLoading.value = false
                    onError()
                }

                is Response.Success -> {
                    Log.i("Settings State", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}