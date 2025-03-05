package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
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
import com.google.firebase.auth.AuthResult
import com.ile.syrin_x.R
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.icon.Book_icon
import com.ile.syrin_x.ui.icon.Supervised_user_circle_icon
import com.ile.syrin_x.ui.navigation.NavigationGraph
import com.ile.syrin_x.ui.screen.common.MyAlertDialog
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
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

    fun registerUser(userName: String, fullName: String, email: String, password: String) {
        registerViewModel.register(userName, fullName, email, password)
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
                registerFlowState = registerViewModel.registerFlow,
                onNavigateToLogin = { navHostController.popBackStack() },
                onRegister = { userName, fullName, email, password -> registerUser(userName, fullName, email, password) },
                registerSuccess = { navHostController.navigate(NavigationGraph.HomeScreen.route) },
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
    onRegister: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    registerSuccess: () -> Unit,
    registerError: (error: String) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        val usernameText = remember {
            mutableStateOf("")
        }

        val fullNameText = remember {
            mutableStateOf("")
        }

        val emailText = remember {
            mutableStateOf("")
        }
        val passwordText = remember {
            mutableStateOf("")
        }

        val repeatedPasswordText = remember {
            mutableStateOf("")
        }

        var showFormInvalidDialog by remember {
            mutableStateOf(false)
        }

        val register = {
            val fields = mapOf(
                "Username" to usernameText.value,
                "Full Name" to fullNameText.value,
                "Email" to emailText.value,
                "Password" to passwordText.value,
                "Repeated Password" to repeatedPasswordText.value
            )

            val emptyField = fields.entries.find { it.value.isEmpty() || it.value.isBlank() }
            if (emptyField != null || passwordText.value != repeatedPasswordText.value) {
                showFormInvalidDialog = true
            } else {
                onRegister(usernameText.value, fullNameText.value, emailText.value, passwordText.value)
            }
        }

        if (showFormInvalidDialog) {
            MyAlertDialog(
                onDismissRequest = { showFormInvalidDialog = false },
                onConfirmation = { showFormInvalidDialog = false },
                title = "Invalid Form",
                text = "Please fill in all fields correctly and make sure your passwords match.",
                confirmButtonText = "Ok",
                dismissButtonText = "Cancel",
                cancelable = true
            )
        }

        Text(
            text = "Register",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp, top = 20.dp),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Please register in order to use this app.",
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
            value = usernameText.value,
            onValueChange = { text -> usernameText.value = text },
            label = { Text("Username") },
            singleLine = true,
            leadingIcon = { Icon(Book_icon, "username") },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            value = fullNameText.value,
            onValueChange = { text -> fullNameText.value = text },
            label = { Text("Full name") },
            singleLine = true,
            leadingIcon = { Icon(Supervised_user_circle_icon, "full_name") },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            value = emailText.value,
            onValueChange = { text -> emailText.value = text },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Filled.Email, "email") },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            value = passwordText.value,
            onValueChange = { text -> passwordText.value = text },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Lock, "password") },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            value = repeatedPasswordText.value,
            onValueChange = { text -> repeatedPasswordText.value = text },
            label = { Text("Repeated password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Lock, "password") },
        )
        Button(
            onClick = { register() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            content = { Text(text = "Register") },
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = buildAnnotatedString {
                append("Already have an account?")
                withStyle(style = SpanStyle(MaterialTheme.colorScheme.primary)) { append(" Login") }
            },
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally)
                .padding(20.dp)
                .clickable { onNavigateToLogin() }
        )
    }



    RegisterState(
        registerFlowState = registerFlowState,
        onSuccess = { registerSuccess() },
        onError = { errorMessage -> registerError(errorMessage) }
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
                    Log.i("Register state -> ", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Register state -> ", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("Register state -> ", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}