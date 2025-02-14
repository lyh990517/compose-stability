package com.yunho.plugin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yunho.plugin.ui.theme.PluginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PluginTheme {
                Hello(
                    testA = TestA(1),
                    testB = TestB(2),
                    testC = TestC(listOf(3))
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

data class TestC(
    val data: List<Int>
)

@Composable
fun Hello(
    testA: TestA,
    testB: TestB,
    testC: TestC
) {
    Text("${testA.data} ${testB.data} ${testC.data.first()}")
}