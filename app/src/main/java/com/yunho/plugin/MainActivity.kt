package com.yunho.plugin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import com.yunho.compose.ComposableFromOtherModule
import com.yunho.compose.TestD
import com.yunho.compose.TestE
import com.yunho.plugin.ui.theme.PluginTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PluginTheme {
                Column {
                    ComposableFromAppModule(
                        testA = TestA(1, 2, 3, 4),
                        testB = TestB(2),
                        testC = TestC(listOf(3))
                    )

                    ComposableFromOtherModule(
                        testD = TestD(mutableIntStateOf(4)),
                        testE = TestE(mutableListOf(5))
                    )
                }

            }
        }
    }
}

data class TestA(
    var data: Int,
    val data1: Int,
    val data2: Int,
    val data3: Int
)

data class TestB(
    val data: Int
)

data class TestC(
    val data: List<Int>
)

@Composable
fun ComposableFromAppModule(
    testA: TestA,
    testB: TestB,
    testC: TestC
) {
    Text("${testA.data} ${testB.data} ${testC.data.first()}")
}