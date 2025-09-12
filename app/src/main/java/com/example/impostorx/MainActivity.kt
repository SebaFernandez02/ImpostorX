package com.example.impostorx


import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.impostorx.ui.theme.ImpostorxTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.impostorx.ui.screens.ImpostorHome
import com.example.impostorx.ui.screens.PreviewImpostorHome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                com.example.impostorx.ui.screens.ImpostorApp()
            }
        }
    }

@Preview(
    name = "Home - Dark",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MainContentPreviewable() {
    ImpostorxTheme {
        ImpostorHome(onContinue = {})
    }}}


