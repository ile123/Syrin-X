package com.ile.syrin_x.ui.screen

import android.Manifest
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
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.AuthResult
import com.ile.syrin_x.R
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.navigation.NavigationGraph
import com.ile.syrin_x.ui.screen.common.MyAlertDialog
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.LoginViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navHostController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {

    val hostState = remember {
        SnackbarHostState()
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
                hostState = hostState,
                paddingValues = paddingValues,
                signInStateFlow = loginViewModel.loginFlow,
                resetPasswordStateFlow = loginViewModel.resetPasswordFlow,
                onRegisterNow = { navHostController.navigate(NavigationGraph.RegisterScreen.route) },
                onForgotPassword = { email -> loginViewModel.resetPassword(email) },
                onLogin = { email, password -> loginViewModel.login(email, password) },
                loginSuccess = { navHostController.navigate(NavigationGraph.HomeScreen.route) { popUpTo(0) } }
            )
        }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    signInStateFlow: MutableSharedFlow<Response<AuthResult>>,
    resetPasswordStateFlow: MutableSharedFlow<Response<Void?>>,
    onRegisterNow: () -> Unit,
    onForgotPassword: (String) -> Unit,
    onLogin: (String, String) -> Unit,
    loginSuccess: () -> Unit,
    hostState: SnackbarHostState
) {
    val emailText = remember {
        mutableStateOf("")
    }
    val passwordText = remember {
        mutableStateOf("")
    }
    var showForgotPasswordDialog by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    if (showForgotPasswordDialog)
        MyAlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            onConfirmation = {
                if (emailText.value != "") {
                    onForgotPassword(emailText.value)
                    showForgotPasswordDialog = false
                } else {
                    scope.launch {
                        hostState.showSnackbar("Please enter email address")
                    }
                }
            },
            title = "Forgot Password?",
            text = "Send a password reset email to entered email address.",
            confirmButtonText = "Send",
            dismissButtonText = "Cancel",
            cancelable = true
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            text = "Login",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp, top = 20.dp),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Please sign in to continue.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp, top = 5.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 40.dp),
            singleLine = true,
            value = emailText.value,
            onValueChange = { text -> emailText.value = text },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Filled.Email, "email") }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            singleLine = true,
            value = passwordText.value,
            onValueChange = { text -> passwordText.value = text },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Filled.Lock, "password") }
        )
        Button(
            onClick = {
                onLogin(emailText.value, passwordText.value)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            content = { Text(text = "Login") }
        )
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = "Forgot Password?",
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                .clickable { showForgotPasswordDialog = true },
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = buildAnnotatedString {
                append("Don't have an account?")
                withStyle(style = SpanStyle(MaterialTheme.colorScheme.primary)) { append(" Register now") }
            },
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally)
                .padding(20.dp)
                .clickable { onRegisterNow() },
            style = MaterialTheme.typography.titleMedium
        )
    }
    LoginInState(
        flow = signInStateFlow,
        onSuccess = { loginSuccess() },
        onError = { scope.launch { hostState.showSnackbar("The email address or password is incorrect") } }
    )
    ResetPasswordState(
        flow = resetPasswordStateFlow,
        onSuccess = { scope.launch { hostState.showSnackbar("Email sent successfully, check your inbox") } },
        onError = { scope.launch { hostState.showSnackbar("Oops! something went wrong, try again") } }
    )
}

@Composable
fun LoginInState(
    flow: MutableSharedFlow<Response<AuthResult>>,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        flow.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("Login state -> ", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Login state -> ", it.message)
                    isLoading.value = false
                    onError()
                }

                is Response.Success -> {
                    Log.i("Login state -> ", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}

@Composable
fun ResetPasswordState(
    flow: MutableSharedFlow<Response<Void?>>,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        flow.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("Reset password state -> ", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Reset password state -> ", it.message)
                    isLoading.value = false
                    onError()
                }

                is Response.Success -> {
                    Log.i("Reset password state -> ", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}