package com.ile.syrin_x.ui.screen

import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.navigation.NavigationGraph
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
        Content(
            hostState = hostState,
            paddingValues = paddingValues,
            signInStateFlow = loginViewModel.loginFlow,
            resetPasswordStateFlow = loginViewModel.resetPasswordFlow,
            onRegisterNow = { navHostController.navigate(NavigationGraph.RegisterScreen.route) },
            onLogin = { email, password -> loginViewModel.login(email, password) },
            loginSuccess = {
                navHostController.navigate(NavigationGraph.HomeScreen.route) {
                    popUpTo(
                        0
                    )
                }
            }
        )
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    signInStateFlow: MutableSharedFlow<Response<AuthResult>>,
    resetPasswordStateFlow: MutableSharedFlow<Response<Void?>>,
    onRegisterNow: () -> Unit,
    onLogin: (String, String) -> Unit,
    loginSuccess: () -> Unit,
    hostState: SnackbarHostState
) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

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
                TextField(
                    value = emailText,
                    onValueChange = { emailText = it },
                    label = {
                        Text(
                            "Email",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    placeholder = { Text("you@example.com") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                TextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    label = {
                        Text(
                            "Password",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = { onLogin(emailText, passwordText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Text(
            text = buildAnnotatedString {
                append("Don't have an account?")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(" Register now")
                }
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { onRegisterNow() }
                .padding(bottom = 24.dp)
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
                    Log.i("Login state", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Login state", it.message)
                    isLoading.value = false
                    onError()
                }

                is Response.Success -> {
                    Log.i("Login state", "Success")
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
                    Log.i("Reset Password State", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Reset Password State", it.message)
                    isLoading.value = false
                    onError()
                }

                is Response.Success -> {
                    Log.i("Reset Password State", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}