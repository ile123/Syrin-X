package com.ile.syrin_x.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ile.syrin_x.R
import com.ile.syrin_x.composition.LocalNavController
import com.ile.syrin_x.ui.theme.SyrinXTheme

@Composable
fun LoginScreen() {

    val navController = LocalNavController.current

    SyrinXTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_image_1),
                    contentDescription = "Background image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Login", fontSize = 30.sp, modifier = Modifier
                        .padding(bottom = 10.dp))
                    TextField(
                        value = "",
                        onValueChange = { },
                        label = { Text("Email") },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    TextField(
                        value = "",
                        onValueChange = { },
                        label = { Text("Password") },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Row {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(text = "Submit")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Register here",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .clickable { navController.navigate("RegisterScreen") }
                        )
                    }

                }
            }
        }
    }
}


