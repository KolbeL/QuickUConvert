package com.sodamoney.quickuconvert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sodamoney.quickuconvert.ui.theme.QuickUConvertTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Convert()
        }
    }
}

val inputWidth = 130.dp
val unitWidth = 50.dp


@Composable
fun ConvertItem(items: Array<out Units>, modifier: Modifier = Modifier) {
    val states = Array(items.size) { rememberTextFieldState(initialText = "") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        (items.indices step 2).forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UnitsField(it, items, states)
                if (it < items.size - 1)  {
                    Spacer(modifier = Modifier.size(30.dp))
                    UnitsField(it + 1, items, states)
                }
            }
        }
    }
}

@Composable
fun UnitsField(index: Int, items: Array<out Units>, states: Array<TextFieldState>, modifier: Modifier = Modifier) {
    val focusRequester = remember {FocusRequester()}
    TextField(
        state = states[index],
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        onKeyboardAction = { UpdateValues(index, items, states) },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier
            .width(inputWidth)
            .focusRequester(focusRequester)
            .onFocusChanged( {
                if (it.isFocused) {
                    states[index].clearText()
                }
            })
    )
    Text(
        text = items[index].symbol,
        modifier = modifier
            .width(unitWidth)
            .padding(start = 5.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun Convert() {
    QuickUConvertTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            var index by remember { mutableStateOf(Category.LENGTH) }
            val allItems = mapOf(
                Category.TEMPERATURE to Temperatures,
                Category.LENGTH to Lengths
            )
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.size(50.dp))
                Text(text = stringResource(index.resource()), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.size(5.dp))
                ConvertItem(
                    items = allItems[index]!!,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }
    }
}

fun UpdateValues(index: Int, items: Array<out Units>, states: Array<TextFieldState>) {
    val value = states[index].text.toString().toDoubleOrNull() ?: 0.0
    for ((ind, state) in states.withIndex()) {
        if (ind == index) {
            continue
        }
        state.setTextAndPlaceCursorAtEnd(ConvertedOrInvalid(value, items[index], items[ind]))

    }

}
fun ConvertedOrInvalid(value: Double, from: Units, to: Units): String {
    return try {
        val converted = from.convertTo(value, to)
        if (converted < 10_000.0 && converted > 0.00001 ) {
            DecimalFormat("#,###.####").format(converted)
        } else {
            DecimalFormat("#.####E0").format(converted)
        }
    } catch (e: IllegalConversionException) {
        e.message ?: "Invalid"
    }
}