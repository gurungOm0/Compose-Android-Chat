package com.softstudio.chat.ui.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatUiPreview(){

}

@Composable
fun Chat(
    navHostController: NavHostController,
    viewModel: ChatViewModel = hiltViewModel()
){

}

@Composable
fun ChatUi(){

}