package com.example.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FoodBowlCanvas(modifier: Modifier = Modifier, color: Color = Color(0xFFFF5722)) {
    Canvas(modifier = modifier.size(80.dp)) {
        val w = size.width
        val h = size.height

        // Bowl base
        val bowlPath = Path().apply {
            moveTo(w * 0.15f, h * 0.45f)
            lineTo(w * 0.85f, h * 0.45f)
            quadraticTo(w * 0.80f, h * 0.85f, w * 0.50f, h * 0.85f)
            quadraticTo(w * 0.20f, h * 0.85f, w * 0.15f, h * 0.45f)
            close()
        }
        drawPath(path = bowlPath, color = color)

        // Bowl rim
        drawRoundRect(
            color = color.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.10f, h * 0.40f),
            size = Size(w * 0.80f, h * 0.08f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )

        // Steams
        val steamColor = Color.DarkGray.copy(alpha = 0.4f)
        val steamPath1 = Path().apply {
            moveTo(w * 0.35f, h * 0.32f)
            cubicTo(w * 0.30f, h * 0.25f, w * 0.40f, h * 0.18f, w * 0.35f, h * 0.10f)
        }
        val steamPath2 = Path().apply {
            moveTo(w * 0.50f, h * 0.32f)
            cubicTo(w * 0.45f, h * 0.25f, w * 0.55f, h * 0.18f, w * 0.50f, h * 0.10f)
        }
        val steamPath3 = Path().apply {
            moveTo(w * 0.65f, h * 0.32f)
            cubicTo(w * 0.60f, h * 0.25f, w * 0.70f, h * 0.18f, w * 0.65f, h * 0.10f)
        }

        drawPath(steamPath1, color = steamColor, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round))
        drawPath(steamPath2, color = steamColor, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round))
        drawPath(steamPath3, color = steamColor, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round))

        // Food contents peaking out
        drawCircle(
            color = Color(0xFFFFC107), // Golden egg yolk or soup content
            radius = w * 0.08f,
            center = Offset(w * 0.42f, h * 0.40f)
        )
        drawCircle(
            color = Color(0xFF4CAF50), // Veggie garnish
            radius = w * 0.05f,
            center = Offset(w * 0.58f, h * 0.41f)
        )
    }
}

@Composable
fun FoodPizzaCanvas(modifier: Modifier = Modifier, color: Color = Color(0xFFFFB300)) {
    Canvas(modifier = modifier.size(80.dp)) {
        val w = size.width
        val h = size.height

        // Crust Path (back triangle)
        val pathCrust = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.75f)
            quadraticTo(w * 0.50f, h * 0.85f, w * 0.15f, h * 0.75f)
            close()
        }
        drawPath(pathCrust, color = Color(0xFFD84315)) // Deep Orange/Brown crust

        // Cheese topping triangle (smaller)
        val pathCheese = Path().apply {
            moveTo(w * 0.50f, h * 0.23f)
            lineTo(w * 0.80f, h * 0.72f)
            quadraticTo(w * 0.50f, h * 0.80f, w * 0.20f, h * 0.72f)
            close()
        }
        drawPath(pathCheese, color = color) // Yellow Cheese

        // Pepperoni rounds
        drawCircle(color = Color(0xFFC2185B), radius = w * 0.05f, center = Offset(w * 0.50f, h * 0.45f))
        drawCircle(color = Color(0xFFC2185B), radius = w * 0.04f, center = Offset(w * 0.38f, h * 0.60f))
        drawCircle(color = Color(0xFFC2185B), radius = w * 0.04f, center = Offset(w * 0.62f, h * 0.58f))
        drawCircle(color = Color(0xFFC2185B), radius = w * 0.03f, center = Offset(w * 0.52f, h * 0.68f))
    }
}

@Composable
fun FoodCoffeeCanvas(modifier: Modifier = Modifier, color: Color = Color(0xFF00796B)) {
    Canvas(modifier = modifier.size(80.dp)) {
        val w = size.width
        val h = size.height

        // Cup Body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.22f, h * 0.35f),
            size = Size(w * 0.56f, h * 0.45f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
        )

        // Cup Handle
        val handlePath = Path().apply {
            moveTo(w * 0.78f, h * 0.45f)
            quadraticTo(w * 0.95f, h * 0.50f, w * 0.92f, h * 0.58f)
            quadraticTo(w * 0.88f, h * 0.68f, w * 0.78f, h * 0.68f)
        }
        drawPath(
            handlePath,
            color = color,
            style = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
        )

        // Rising Aroma lines
        val linePath = Path().apply {
            moveTo(w * 0.38f, h * 0.28f)
            quadraticTo(w * 0.34f, h * 0.20f, w * 0.42f, h * 0.12f)
            moveTo(w * 0.50f, h * 0.28f)
            quadraticTo(w * 0.46f, h * 0.20f, w * 0.54f, h * 0.12f)
            moveTo(w * 0.62f, h * 0.28f)
            quadraticTo(w * 0.58f, h * 0.20f, w * 0.66f, h * 0.12f)
        }
        drawPath(
            linePath,
            color = Color.Gray.copy(alpha = 0.5f),
            style = Stroke(width = w * 0.03f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ScooterDeliveryCanvas(modifier: Modifier = Modifier, color: Color = Color(0xFFE65100)) {
    Canvas(modifier = modifier.size(120.dp)) {
        val w = size.width
        val h = size.height

        // Ground line
        drawLine(
            color = Color.LightGray,
            start = Offset(w * 0.05f, h * 0.80f),
            end = Offset(w * 0.95f, h * 0.80f),
            strokeWidth = w * 0.02f,
            cap = StrokeCap.Round
        )

        // Wheels
        drawCircle(
            color = Color.DarkGray,
            radius = w * 0.12f,
            center = Offset(w * 0.25f, h * 0.70f)
        )
        drawCircle(
            color = Color.LightGray,
            radius = w * 0.05f,
            center = Offset(w * 0.25f, h * 0.70f)
        )

        drawCircle(
            color = Color.DarkGray,
            radius = w * 0.12f,
            center = Offset(w * 0.75f, h * 0.70f)
        )
        drawCircle(
            color = Color.LightGray,
            radius = w * 0.05f,
            center = Offset(w * 0.75f, h * 0.70f)
        )

        // Scooter Chassis/Body
        val bodyPath = Path().apply {
            moveTo(w * 0.20f, h * 0.58f)
            lineTo(w * 0.45f, h * 0.58f)
            lineTo(w * 0.60f, h * 0.70f)
            lineTo(w * 0.75f, h * 0.70f)
            lineTo(w * 0.82f, h * 0.45f) // Front steering column
            lineTo(w * 0.78f, h * 0.45f)
        }
        drawPath(bodyPath, color = color, style = Stroke(width = w * 0.05f, cap = StrokeCap.Round))

        // Delivery Box (big square on the back)
        drawRoundRect(
            color = Color(0xFF3F51B5),
            topLeft = Offset(w * 0.15f, h * 0.32f),
            size = Size(w * 0.26f, h * 0.26f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
        )
        // White logo on box or stripes
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.20f, h * 0.42f),
            size = Size(w * 0.16f, h * 0.05f),
            cornerRadius = CornerRadius(w * 0.01f, w * 0.01f)
        )

        // Handlebars
        drawLine(
            color = Color.DarkGray,
            start = Offset(w * 0.82f, h * 0.42f),
            end = Offset(w * 0.72f, h * 0.42f),
            strokeWidth = w * 0.04f,
            cap = StrokeCap.Round
        )

        // Speed lines behind
        val speedPath = Path().apply {
            moveTo(w * 0.05f, h * 0.40f)
            lineTo(w * 0.12f, h * 0.40f)
            moveTo(w * 0.02f, h * 0.48f)
            lineTo(w * 0.10f, h * 0.48f)
            moveTo(w * 0.06f, h * 0.56f)
            lineTo(w * 0.14f, h * 0.56f)
        }
        drawPath(
            speedPath,
            color = Color.Gray.copy(alpha = 0.6f),
            style = Stroke(width = w * 0.018f, cap = StrokeCap.Round)
        )
    }
}
