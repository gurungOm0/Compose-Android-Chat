package com.softstudio.chat.ui.screens.onBoarding


import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.softstudio.chat.navigation.HomeDes
import com.softstudio.chat.ui.theme.ChatTheme
import com.softstudio.chat.util.clearFocusOnTap

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnBoardingPreview() {
    val state = AuthenticationUiState()
    ChatTheme {
        OnBoardingUi(
            state = state,
            updateUri = { },
            updateName = { },
            uploadImage = { },
            saveInfo = { }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OnBoarding(navHostController: NavHostController, viewModel: AuthenticationViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val failureMessage = state.emailSignInFailureMessage
        ?: state.imageUploadFailureMessage
        ?: state.saveInfoFailureMessage

    LaunchedEffect(failureMessage) {
        failureMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorMessageShown()
        }
    }
    OnBoardingUi(
        state = state,
        updateUri = { uri -> viewModel.updateUri(uri) },
        updateName = { name -> viewModel.updateName(name) },
        uploadImage = { viewModel.uploadProfileImage() },
        saveInfo = { viewModel.saveInfo { navHostController.navigate(HomeDes.route) } }
    )
}

@Composable
fun OnBoardingUi(
    state: AuthenticationUiState,
    updateUri: (Uri) -> Unit,
    updateName: (String) -> Unit,
    uploadImage: () -> Unit,
    saveInfo: () -> Unit
) {
    val context = LocalContext.current
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
                .fillMaxWidth()
                .padding(28.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "Save Your Info",
                modifier = Modifier,
                style = MaterialTheme.typography.titleMedium
            )
        }

        val photoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    updateUri(uri)
                }
            }
        )
        IconButton(
            onClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.size(168.dp),
            shape = RoundedCornerShape(168.dp / 2)
        ) {
            val imageUri = state.profileImageUri
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Select Profile Image",
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Select Profile Image",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(168.dp)
                )
            }
        }
        Button(
            onClick = {
                if (state.profileImageUri != null) {
                    uploadImage()
                } else {
                    Toast.makeText(
                        context,
                        "Please select a profile image first",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier
        ) {
            Text("Upload Image", style = MaterialTheme.typography.labelMedium)
        }
        var name by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .padding(horizontal = 48.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Name",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp, start = 6.dp)
                    .align(Alignment.Start)
            )
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    updateName(it)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(24),
            )
        }


        Button(
            onClick = {
                if (name.isNotBlank()) {
                    saveInfo()
                } else {
                    Toast.makeText(
                        context,
                        "Please enter a name first",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp, horizontal = 36.dp),
            /*border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onPrimary),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSecondary)*/
        ) {
            if (state.saveInfoLoaderState) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Save Info")
            }
        }
    }
}