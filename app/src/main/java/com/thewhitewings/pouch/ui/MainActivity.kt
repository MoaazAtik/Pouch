package com.thewhitewings.pouch.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.thewhitewings.pouch.PouchApp
import com.thewhitewings.pouch.ui.theme.PouchTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PouchTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PouchApp()
                }
            }
        }
    }
}
