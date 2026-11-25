package com.cespedesagro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cespedesagro.app.ui.screens.HomeScreen
import com.cespedesagro.app.ui.theme.CespedesAgroTheme
import com.cespedesagro.app.ui.theme.CespedesColors
import com.cespedesagro.app.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CespedesAgroTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = CespedesColors.Primario) {
                    HomeScreen(vm = vm)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.btManager.disconnect()
    }
}
