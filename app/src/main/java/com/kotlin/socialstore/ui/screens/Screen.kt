package com.kotlin.socialstore.ui.screens

import android.provider.Settings.Global.getString
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.buttonElement
import com.kotlin.socialstore.ui.elements.outlinedTextfieldElement

@Preview( showSystemUi = true, showBackground = true )
@Composable
fun loginPage(modifier: Modifier = Modifier){

    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.social_store_image),
            contentDescription = "Image"
        )
        Text(stringResource(R.string.app_name))
        Spacer(Modifier.size(30.dp))
        outlinedTextfieldElement(email, stringResource(R.string.email_textfield))
        outlinedTextfieldElement(password,stringResource(R.string.password_textfield))
        Spacer(Modifier.size(20.dp))
        //buttonElement() {  }
        Button( onClick = {}, //Submits login
                modifier = Modifier.width(290.dp)
        ) {
            Text(stringResource(R.string.login_button))
        }
        Spacer(Modifier.size(30.dp))
    }
}