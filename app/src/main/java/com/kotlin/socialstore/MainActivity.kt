package com.kotlin.socialstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.kotlin.socialstore.ui.screens.loginPage
import com.kotlin.socialstore.ui.theme.SocialStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    loginPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}