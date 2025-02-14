package com.yunho.plugin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.yunho.plugin.ui.theme.PluginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PluginTheme {
                Hello(
                    testA = TestA(1),
                    testB = TestB(2)
                )
            }
        }
    }
}

data class TestA(
    var data: Int
)

data class TestB(
    val data: Int
)

@Composable
fun Hello(
    testA: TestA,
    testB: TestB,
) {
    Text("${testA.data} ${testB.data}")
}