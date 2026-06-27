package com.sodamoney.quickuconvert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sodamoney.quickuconvert.ui.theme.QuickUConvertTheme
import java.text.DecimalFormat
import kotlin.enums.EnumEntries

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
fun ConvertItem(
    items: Array<out Units>,
    states: Array<TextFieldState>,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        (items.indices step 2).forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UnitsField(states[it], items[it].symbol, { updateValues(it, items, states) })
                if (it < items.size - 1) {
                    Spacer(modifier = Modifier.size(30.dp))
                    UnitsField(
                        states[it + 1],
                        items[it + 1].symbol,
                        { updateValues(it + 1, items, states) })
                }
            }
        }
    }
}

@Composable
fun UnitsField(
    state: TextFieldState,
    symbol: String,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    TextField(
        state = state,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        onKeyboardAction = { onUpdate() },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier
            .width(inputWidth)
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    state.clearText()
                }
            }
    )
    Text(
        text = symbol,
        modifier = modifier
            .width(unitWidth)
            .padding(start = 5.dp)
    )
}

@Composable
fun CategorySelect(
    categories: EnumEntries<Category>,
    cur: Category,
    onChange: (Category) -> Unit,
    valid: Set<Category>
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(5.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = stringResource(cur.resource()))
            Icon(imageVector = arrow_drop_down, stringResource(R.string.drop_down_arrow))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false })
        {
            categories.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(it.resource()),
                            style = if (it in valid) {
                                TextStyle.Default
                            } else {
                                TextStyle(textDecoration = TextDecoration.LineThrough)
                            }
                        )
                    },
                    onClick = {
                        if (it != cur && it in valid) {
                            onChange(it)
                        }
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun Convert() {
    QuickUConvertTheme {
        Scaffold(modifier = Modifier) { innerPadding ->
            var category by remember { mutableStateOf(Category.LENGTH) }
            var onOpen by remember { mutableStateOf(true) }

            val allItems = mapOf(
                Category.TEMPERATURE to Temperatures,
                Category.LENGTH to Lengths
            )
            val maxCount = allItems.values.maxOf { it.size }
            val states = Array(maxCount) { rememberTextFieldState("") }
            if (onOpen) {
                resetValues(states, allItems[category]!!)
            }
            onOpen = false
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.size(50.dp))
                CategorySelect(Category.entries, category, {
                    resetValues(states, allItems[it]!!)
                    category = it
                }, allItems.keys)
                Spacer(modifier = Modifier.size(5.dp))
                ConvertItem(
                    items = allItems[category]!!,
                    states = states,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }
    }
}

fun resetValues(states: Array<TextFieldState>, items: Array<out Units>) {
    states.withIndex().forEach {
        val value = if (it.index == 0) {
            "1"
        } else if (it.index < items.size) {
            convertedOrInvalid(1.0, items[0], items[it.index])
        } else {
            ""
        }
        it.value.setTextAndPlaceCursorAtEnd(value)
    }
}

fun updateValues(index: Int, items: Array<out Units>, states: Array<TextFieldState>) {
    val value = states[index].text.toString().toDoubleOrNull() ?: 0.0
    for ((ind, state) in states.withIndex()) {
        if (ind == index) {
            continue
        }
        state.setTextAndPlaceCursorAtEnd(convertedOrInvalid(value, items[index], items[ind]))

    }

}

fun convertedOrInvalid(value: Double, from: Units, to: Units): String {
    return try {
        val converted = from.convertTo(value, to)
        if (converted < 10_000.0 && converted > 0.00001) {
            DecimalFormat("#,###.####").format(converted)
        } else {
            DecimalFormat("#.####E0").format(converted)
        }
    } catch (_: IllegalConversionException) {
        "Invalid"
    }
}