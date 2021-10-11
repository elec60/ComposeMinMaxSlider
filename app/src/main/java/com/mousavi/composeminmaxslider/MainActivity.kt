package com.mousavi.composeminmaxslider

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mousavi.composeminmaxslider.ui.theme.ComposeMinMaxSliderTheme
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMinMaxSliderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        Slider()
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun Slider(
    title: String = "Price",
    min: Int = 0,
    max: Int = 100,
    unit: String = "$",
    barHeight: Dp = 10.dp,
    circleRadius: Dp = 12.dp,
    initialSecondValue: Int = max / 2,
    callback: ((Int, Int) -> Unit)? = null
) {

    val circleRadiusPx = with(LocalDensity.current) { circleRadius.toPx() }

    var first by remember {
        mutableStateOf(0f)
    }

    var second by remember {
        mutableStateOf(0.5f)
    }

    var w by remember {
        mutableStateOf(0f)
    }

    var firstDragged by remember {
        mutableStateOf(false)
    }

    var secondDragged by remember {
        mutableStateOf(false)
    }

    var xForText1 by remember {
        mutableStateOf(0f)
    }

    var xForText2 by remember {
        mutableStateOf(0f)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .offset(x = 0.dp, y = 20.dp)
                    .matchParentSize()
                    .onGloballyPositioned {
                        w = it.boundsInWindow().width
                        if (second == 0.5f) {
                            second = initialSecondValue / max.toFloat()
                        }
                    }
                    .pointerInteropFilter {
                        val x = it.x

                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                if (x > second * w - 2 * circleRadiusPx && x < second * w + 2 * circleRadiusPx) {
                                    secondDragged = true
                                    firstDragged = false
                                } else if (x > first * w - 2 * circleRadiusPx && x < first * w + 2 * circleRadiusPx) {
                                    firstDragged = true
                                    secondDragged = false
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                firstDragged = false
                                secondDragged = false
                            }
                            MotionEvent.ACTION_MOVE -> {
                                if (firstDragged) {
                                    first = (x / w)
                                        .coerceAtLeast(0f)
                                        .coerceAtMost(second - 2 * circleRadiusPx / w)
                                }

                                if (secondDragged) {
                                    second = (x / w)
                                        .coerceAtLeast(first + 2 * circleRadiusPx / w)
                                        .coerceAtMost(1f)
                                }
                            }
                        }

                        true
                    }
            ) {

                drawRoundRect(
                    color = Color(0xFFB6B6B6),
                    cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
                    size = Size(width = size.width, height = barHeight.toPx())
                )


                xForText1 = first * (size.width - 2 * circleRadius.toPx()) + circleRadius.toPx()
                drawCircle(
                    color = Color.Black,
                    radius = circleRadius.toPx(),
                    center = Offset(
                        x = xForText1,
                        y = barHeight.toPx() / 2
                    )
                )
                drawCircle(
                    color = Color.White,
                    radius = circleRadius.toPx() * 0.6f,
                    center = Offset(
                        x = xForText1,
                        y = barHeight.toPx() / 2
                    )
                )


                xForText2 = second * (size.width - 2 * circleRadius.toPx()) + circleRadius.toPx()
                drawCircle(
                    color = Color.Black,
                    radius = circleRadius.toPx(),
                    center = Offset(
                        x = xForText2,
                        y = barHeight.toPx() / 2
                    )
                )
                drawCircle(
                    color = Color.White,
                    radius = circleRadius.toPx() * 0.6f,
                    center = Offset(
                        x = xForText2,
                        y = barHeight.toPx() / 2
                    )
                )

                drawRect(
                    color = Color.Black,
                    topLeft = Offset(
                        x = xForText1 + circleRadiusPx - 2,
                        y = 0f
                    ),
                    size = Size(
                        width = xForText2 - xForText1 - circleRadiusPx * 2 + 4,
                        height = barHeight.toPx()
                    )
                )

            }

            var textWidth1 by remember {
                mutableStateOf(0)
            }
            var textWidth2 by remember {
                mutableStateOf(0)
            }
            Text(
                text = "$unit${(first * max).roundToInt()}",
                modifier = Modifier
                    .offset(
                        x = with(LocalDensity.current) { (xForText1 - textWidth1 / 2).toDp() },
                        y = circleRadius * 2 + 30.dp
                    )
                    .onGloballyPositioned {
                        textWidth1 = it.size.width
                    }
            )

            Text(
                text = "$unit${(second * max).roundToInt()}",
                modifier = Modifier
                    .offset(
                        x = with(LocalDensity.current) { (xForText2 - textWidth2 / 2).toDp() },
                        y = circleRadius * 2 + 30.dp
                    )
                    .onGloballyPositioned {
                        textWidth2 = it.size.width
                    }
            )
        }
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeMinMaxSliderTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.padding(20.dp)) {
            Slider()
        }
    }
}