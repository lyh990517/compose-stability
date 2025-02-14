package com.yunho.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

data class TestD(
    val state: MutableState<Int>
)

data class TestE(
    val data: MutableList<Int>
)

@Composable
fun ComposableFromOtherModule(
    testD: TestD,
    testE: TestE
) {
    val state by remember { testD.state }

    Text(text = "$state ${testE.data}")
}