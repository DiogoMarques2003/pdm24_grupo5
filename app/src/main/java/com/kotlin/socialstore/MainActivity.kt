package com.kotlin.socialstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.ui.screens.AppNavigation
import com.kotlin.socialstore.ui.theme.SocialStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //FirebaseStart
        FirebaseObj.startFirebase()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialStoreTheme {
                AppNavigation()
            }
        }
    }
}