package com.sodamoney.quickuconvert

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.DpOffset
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.vector.ImageVector
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
    var showIntro by remember { mutableStateOf(!repo.hasSeenIntro) }
    var category by rememberSaveable { mutableStateOf(Category.LENGTH) }

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
        if (showIntro) {
            IntroDialog(onDismiss = {
                repo.hasSeenIntro = true
                showIntro = false
            })
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
    BoxWithConstraints(modifier = modifier) {
        val targetCardWidth = 150.dp
        val columns = ((maxWidth + 10.dp) / (targetCardWidth + 10.dp)).toInt().coerceAtLeast(2)
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            (items.indices step columns).forEach { i ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (j in 0 until columns) {
                        val idx = i + j
                        if (idx < items.size) {
                            UnitCard(
                                state = states[idx],
                                symbol = items[idx].symbol,
                                onUpdate = { updateValues(idx, items, states) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
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
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString("${state.text} $symbol"))
                        copied = true
                    }
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
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow"
    )
    var anchorWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { anchorWidth = it.width },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 1.dp
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
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
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.rotate(arrowRotation)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp),
                offset = DpOffset(x = 4.dp, y = 0.dp),
                modifier = Modifier.width(with(density) { anchorWidth.toDp() } - 8.dp)
            ) {
                categories.chunked(2).forEach { pair ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        pair.forEach { cat ->
                            val isSelected = cat == cur
                            val isValid = cat in valid
                            DropdownMenuItem(
                                modifier = Modifier.weight(1f),
                                text = {
                                    Text(
                                        text = cat.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        textDecoration = if (!isValid) TextDecoration.LineThrough else null,
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            !isValid   -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            else       -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                },
                                trailingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else null,
                                onClick = {
                                    if (!isSelected && isValid) onChange(cat)
                                    expanded = false
                                }
                            )
                        }
                        if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
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

@Composable
fun IntroDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Welcome to QuickConvert") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IntroTip(
                    icon = Icons.Default.Edit,
                    text = "Tap any unit box and type a number — all other units update instantly."
                )
                IntroTip(
                    icon = Icons.Default.ContentCopy,
                    text = "The copy icon on each card puts that value on your clipboard."
                )
                IntroTip(
                    icon = Icons.Default.Settings,
                    text = "Open Settings to choose a theme or reorder and show/hide units."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

@Composable
private fun IntroTip(icon: ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
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
