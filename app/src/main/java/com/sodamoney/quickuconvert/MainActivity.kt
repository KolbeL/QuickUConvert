package com.sodamoney.quickuconvert

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sodamoney.quickuconvert.ui.theme.QuickUConvertTheme
import kotlinx.coroutines.delay
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

@Composable
fun Convert() {
    val context = LocalContext.current
    val repo = remember {
        SettingsRepository(
            context.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)
        )
    }
    var themeMode by remember { mutableStateOf(repo.themeMode) }
    var settingsOpen by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf(Category.LENGTH) }

    val maxCount = AllUnits.values.maxOf { it.size }
    val states = Array(maxCount) { rememberTextFieldState("") }

    LaunchedEffect(Unit) {
        resetValues(states, repo.visibleUnits(category))
    }

    QuickUConvertTheme(themeMode = themeMode) {
        if (settingsOpen) {
            SettingsScreen(
                repo = repo,
                onThemeChange = { themeMode = it },
                onBack = {
                    resetValues(states, repo.visibleUnits(category))
                    settingsOpen = false
                }
            )
        } else {
            MainContent(
                repo = repo,
                category = category,
                states = states,
                onCategoryChange = { newCat ->
                    resetValues(states, repo.visibleUnits(newCat))
                    category = newCat
                },
                onSettingsClick = { settingsOpen = true }
            )
        }
    }
}

@Composable
fun MainContent(
    repo: SettingsRepository,
    category: Category,
    states: Array<TextFieldState>,
    onCategoryChange: (Category) -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val visibleItems = repo.visibleUnits(category)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QuickConvert",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
            CategorySelect(
                categories = Category.entries,
                cur = category,
                onChange = onCategoryChange,
                valid = AllUnits.keys
            )
            Spacer(modifier = Modifier.size(12.dp))
            ConvertItem(
                items = visibleItems,
                states = states,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
fun ConvertItem(
    items: Array<out Units>,
    states: Array<TextFieldState>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        (items.indices step 2).forEach { i ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UnitCard(
                    state = states[i],
                    symbol = items[i].symbol,
                    onUpdate = { updateValues(i, items, states) },
                    modifier = Modifier.weight(1f)
                )
                if (i + 1 < items.size) {
                    UnitCard(
                        state = states[i + 1],
                        symbol = items[i + 1].symbol,
                        onUpdate = { updateValues(i + 1, items, states) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun UnitCard(
    state: TextFieldState,
    symbol: String,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var copied by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var savedText by remember { mutableStateOf("") }

    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(copied) {
        if (copied) {
            delay(1500)
            copied = false
        }
    }

    Card(
        modifier = modifier.shadow(
            elevation = if (isFocused) 6.dp else 2.dp,
            shape = RoundedCornerShape(12.dp),
            ambientColor = if (isFocused) primaryColor.copy(alpha = 0.5f) else Color.Black,
            spotColor = if (isFocused) primaryColor.copy(alpha = 0.9f) else Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp, top = 10.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BasicTextField(
                state = state,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    onUpdate()
                    keyboardController?.hide()
                },
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        val gaining = !isFocused && focusState.isFocused
                        val losing = isFocused && !focusState.isFocused
                        isFocused = focusState.isFocused
                        when {
                            gaining -> {
                                savedText = state.text.toString()
                                state.clearText()
                            }
                            losing -> {
                                if (state.text.isEmpty()) {
                                    state.setTextAndPlaceCursorAtEnd(savedText)
                                } else {
                                    onUpdate()
                                }
                            }
                        }
                    }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(state.text.toString()))
                        copied = true
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = if (copied) "Copied" else "Copy",
                        modifier = Modifier.size(16.dp),
                        tint = if (copied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySelect(
    categories: EnumEntries<Category>,
    cur: Category,
    onChange: (Category) -> Unit,
    valid: Set<Category>
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 1.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cur.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = arrow_drop_down,
                        contentDescription = "Drop down arrow",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = it.name.lowercase().replaceFirstChar { c -> c.uppercase() },
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
        if (ind == index) continue
        if (ind >= items.size) break
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
