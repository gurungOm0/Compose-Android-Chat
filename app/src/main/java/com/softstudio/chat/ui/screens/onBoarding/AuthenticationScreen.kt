package com.softstudio.chat.ui.screens.onBoarding

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.softstudio.chat.ui.theme.ChatTheme
import com.softstudio.chat.R
import com.softstudio.chat.navigation.HomeDes
import com.softstudio.chat.navigation.OnBoardingDes
import com.softstudio.chat.util.clearFocusOnTap

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AuthenticationUiPreview() {
    val state = AuthenticationUiState()
    ChatTheme {
        AuthenticationUi(
            state = state,
            emailSignIn = { email, password -> },
            emailSignUp = { email, password -> },
            googleSignIn = { },
            anonymousSignIn = { }
        )
    }
}

@Composable
fun AuthenticationScreen(
    navHostController: NavHostController,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val failureMessage = state.emailSignInFailureMessage
        ?: state.emailSignUpFailureMessage
        ?: state.googleSignInFailureMessage
        ?: state.imageUploadFailureMessage

    LaunchedEffect(failureMessage) {
        failureMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorMessageShown()
        }
    }
    AuthenticationUi(
        state = state,
        emailSignIn = { email, password ->
            viewModel.signInWithEmailAndPassword(
                email,
                password
            ) { navHostController.navigate(OnBoardingDes.route) }
        },
        emailSignUp = { email, password ->
            viewModel.signUpWithEmailAndPassword(
                email,
                password
            ) { navHostController.navigate(OnBoardingDes.route) }
        },
        googleSignIn = {
            viewModel.signInWihGoogle(context) { navHostController.navigate(HomeDes.route) }
        },
        anonymousSignIn = {
            viewModel.signInAnonymously { navHostController.navigate(OnBoardingDes.route) }
        }
    )
}

@Composable
fun AuthenticationUi(
    state: AuthenticationUiState,
    emailSignIn: (String, String) -> Unit,
    emailSignUp: (String, String) -> Unit,
    googleSignIn: () -> Unit,
    anonymousSignIn: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .clearFocusOnTap(focusManager),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.25f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "CHAT", modifier = Modifier, style = MaterialTheme.typography.titleLarge)
        }
        // Login & Signup Segmented Button
        val option = listOf("SignIn", "SignUp")
        var selectedIndex by remember { mutableIntStateOf(0) }
        SingleChoiceSegmentedButtonRow {
            option.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = option.size),
                    onClick = {
                        selectedIndex = index
                    },
                    selected = (index == selectedIndex),
                    label = {
                        Text(label, style = MaterialTheme.typography.labelMedium)
                    }
                )
            }
        }
        // Input Parameters
        Column(
            modifier = Modifier
                .padding(horizontal = 48.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Text(
                text = "E-mail",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp, start = 6.dp)
                    .align(Alignment.Start)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(24)
            )
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp, start = 6.dp)
                    .align(Alignment.Start)
            )
            var passwordVisibility by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(24),
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Visibility",
                        modifier = Modifier.clickable(onClick = {
                            passwordVisibility = !passwordVisibility
                        })
                    )
                }
            )
            // Button SignIn
            Button(
                onClick = {
                    if (selectedIndex == 0) {
                        emailSignIn(email, password)
                    } else {
                        emailSignUp(email, password)
                    }
                },
                enabled = state.signInButtonEnableState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp, horizontal = 36.dp)
            ) {
                Text(if (selectedIndex == 0) "Sign-In" else "Sign-Up")
            }
            Text(
                text = "Or\nSign-In through",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )
            if (state.googleSignInButtonLoaderState) {
                CircularProgressIndicator(modifier = Modifier.size(58.dp))
            } else {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        IconButton(
                            onClick = {
                                anonymousSignIn()
                            },
                            modifier = Modifier
                                .clipToBounds()
                                .border(
                                    color = MaterialTheme.colorScheme.surface,
                                    width = 1.dp,
                                    shape = CircleShape
                                )
                                .size(68.dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Anonymous Login",
                                modifier = Modifier
                                    .clipToBounds()
                                    .border(
                                        color = MaterialTheme.colorScheme.surface,
                                        width = 1.dp,
                                        shape = CircleShape
                                    )
                                    .size(68.dp)
                            )
                        }
                        Text("Anonymously", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(14.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                googleSignIn()
                            },
                            modifier = Modifier
                                .clipToBounds()
                                .border(
                                    color = MaterialTheme.colorScheme.surface,
                                    width = 1.dp,
                                    shape = CircleShape
                                )
                                .size(68.dp),
                            shape = CircleShape
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_google),
                                contentDescription = "Google Login",
                                modifier = Modifier.size(58.dp),
                                alignment = Alignment.Center
                            )
                        }
                        Text("Google", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}