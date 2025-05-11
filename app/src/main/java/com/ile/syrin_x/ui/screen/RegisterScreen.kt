package com.ile.syrin_x.ui.screen

import android.content.res.Resources.Theme
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.auth.AuthResult
import com.ile.syrin_x.R
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.navigation.NavigationGraph
import com.ile.syrin_x.ui.screen.common.MyAlertDialog
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.utils.getUriFromDrawable
import com.ile.syrin_x.viewModel.RegisterViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    fun registerUser(
        userName: String,
        fullName: String,
        email: String,
        password: String,
        imageUri: Uri?
    ) {
        val finalUri = imageUri ?: getUriFromDrawable(context, R.drawable.default_profile_picture)
        registerViewModel.register(userName, fullName, email, password, finalUri, context)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Content(
            paddingValues = paddingValues,
            registerFlowState = registerViewModel.registerFlow,
            onNavigateToLogin = { navHostController.popBackStack() },
            onRegister = ::registerUser,
            registerSuccess = {
                navHostController.navigate(NavigationGraph.HomeScreen.route)
            },
            registerError = { errorMessage ->
                scope.launch {
                    hostState.showSnackbar(errorMessage)
                }
            }
        )
    }
}


@Composable
fun Content(
    paddingValues: PaddingValues,
    registerFlowState: MutableSharedFlow<Response<AuthResult>>,
    onRegister: (String, String, String, String, Uri?) -> Unit,
    onNavigateToLogin: () -> Unit,
    registerSuccess: () -> Unit,
    registerError: (error: String) -> Unit
) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri.value = uri }

    val username = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatedPassword = remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val register = {
        val fields = listOf(username, fullName, email, password, repeatedPassword)
        if (fields.any { it.value.isBlank() } || password.value != repeatedPassword.value) {
            showDialog = true
        } else {
            onRegister(username.value, fullName.value, email.value, password.value, imageUri.value)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") }
                ) {
                    val painter = rememberAsyncImagePainter(
                        imageUri.value ?: R.drawable.default_profile_picture
                    )
                    Image(
                        painter = painter,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = {
                        Text("Username", style = MaterialTheme.typography.labelMedium)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                TextField(
                    value = fullName.value,
                    onValueChange = { fullName.value = it },
                    label = {
                        Text("Full Name", style = MaterialTheme.typography.labelMedium)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = {
                        Text("Email", style = MaterialTheme.typography.labelMedium)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = {
                        Text("Password", style = MaterialTheme.typography.labelMedium)
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                TextField(
                    value = repeatedPassword.value,
                    onValueChange = { repeatedPassword.value = it },
                    label = {
                        Text("Repeat Password", style = MaterialTheme.typography.labelMedium)
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = register,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Register", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Text(
            text = buildAnnotatedString {
                append("Already have an account?")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(" Login now")
                }
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { onNavigateToLogin() }
                .padding(bottom = 24.dp)
        )

        if (showDialog) {
            MyAlertDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = { showDialog = false },
                title = "Invalid Form",
                text = "Please complete all fields and ensure passwords match.",
                confirmButtonText = "OK",
                dismissButtonText = "Cancel",
                cancelable = true
            )
        }
    }

    RegisterState(
        registerFlowState = registerFlowState,
        onSuccess = registerSuccess,
        onError = registerError
    )
}

@Composable
fun RegisterState(
    registerFlowState: MutableSharedFlow<Response<AuthResult>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        registerFlowState.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("Register state", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Register state", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("Register state", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}