package ru.itmo.fpin.belov.calculator

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itmo.fpin.belov.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        CalculatorApp(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("0") }
    val mathWorker = MathWorker()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    fun onClickOnDigit(button: String) {
        if (input == "Infinity" || input == "NaN" || input == "0") {
            input = button
            return
        }
        if ((button == "+" || button == "-" || button == "*" || button == "/") &&
            (input.endsWith("+") || input.endsWith("-") || input.endsWith("*") || input.endsWith("/"))
        ) {
            input = input.dropLast(1) + button
            return
        }
        input += button
    }

    fun onClickOnErase() {
        if (input == "Infinity" || input == "NaN") {
            input = "0"
            return
        }
        if (input.isNotEmpty()) {
            input = input.dropLast(1)
        }
        if (input.isEmpty()) {
            input = "0"
        }
        if (input.endsWith(".")) {
            input = input.dropLast(1)
        }
        if (input.endsWith("+") || input.endsWith("-") || input.endsWith("*") || input.endsWith("/")) {
            input = input.dropLast(1)
        }
    }

    fun evaluate() {
        input = mathWorker.evaluate(input).toString()
    }

    if (isLandscape) {
        LandscapeCalculatorLayout(
            input = input,
            onClickOnErase = { onClickOnErase() },
            onClick = { button -> onClickOnDigit(button) },
            onEvaluate = { evaluate() },
            modifier = modifier
        )
    } else {
        PortraitCalculatorLayout(
            input = input,
            onClickOnErase = { onClickOnErase() },
            onClick = { button -> onClickOnDigit(button) },
            onEvaluate = { evaluate() },
            modifier = modifier
        )
    }
}

@Composable
fun PortraitCalculatorLayout(
    input: String,
    onClickOnErase: () -> Unit,
    onClick: (String) -> Unit,
    onEvaluate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        DisplayRow(
            text = input,
            onClickOnErase = onClickOnErase,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Keyboard(
            onClick = onClick,
            onEvaluate = onEvaluate
        )
    }
}

@Composable
fun LandscapeCalculatorLayout(
    input: String,
    onClickOnErase: () -> Unit,
    onClick: (String) -> Unit,
    onEvaluate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        DisplayRow(
            text = input,
            onClickOnErase = onClickOnErase,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        Keyboard(
            onClick = onClick,
            onEvaluate = onEvaluate,
            modifier = Modifier.weight(1.4f).fillMaxHeight(),
            contentPadding = 0.dp,
            spacing = 6.dp
        )
    }
}

@Composable
fun DisplayRow(text: String, onClickOnErase: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        InputTextField(
            text = text,
            modifier = Modifier.weight(1f)
        )
        Button(onClick = onClickOnErase) {
            Text(text = "C", fontSize = 24.sp)
        }
    }
}

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    onEvaluate: () -> Unit = {},
    contentPadding: androidx.compose.ui.unit.Dp = 16.dp,
    spacing: androidx.compose.ui.unit.Dp = 8.dp
) {
    val keyboard = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "=", "+")
    )

    return LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items(keyboard.flatten().size) { index ->
            val buttonText = keyboard.flatten()[index]
            Button(
                onClick = {
                    if (buttonText == "=") {
                        onEvaluate()
                    }
                    else {
                        onClick(buttonText)
                    }
                          },
                shape = CircleShape,
                modifier = Modifier.aspectRatio(1f)
            ) {
                Text(text = buttonText, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun InputTextField(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 32.sp,
        modifier = modifier.padding(16.dp),
        textAlign = TextAlign.End
    )
}