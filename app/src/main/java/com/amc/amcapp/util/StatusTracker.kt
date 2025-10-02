import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StatusTracker(
    steps: List<String>,
    currentStep: Int
) {
    val circleSize = 20.dp
    val lineHeight = 4.dp
    val labelHeight = 24.dp
    val completedColor = Color(0xFF4CAF50)
    val remainingColor = Color.LightGray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Box for circles + line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(circleSize + lineHeight) // extra space for line
        ) {
            // Draw progress line behind the circles
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(lineHeight)
                    .align(Alignment.Center)
            ) {
                val y = size.height / 2
                val totalSteps = steps.size
                val segmentWidth = size.width / (totalSteps - 1)

                // Completed portion
                drawLine(
                    color = completedColor,
                    start = Offset(0f, y),
                    end = Offset(segmentWidth * currentStep, y),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )

                // Remaining portion
                drawLine(
                    color = remainingColor,
                    start = Offset(segmentWidth * currentStep, y),
                    end = Offset(size.width, y),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }

            // Circles on top of the line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                steps.forEachIndexed { index, _ ->
                    val circleColor = when {
                        index < currentStep -> completedColor
                        index == currentStep -> Color(0xFFFFC107)
                        else -> remainingColor
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(circleSize)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(circleSize)
                                .clip(CircleShape)
                                .background(circleColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index < currentStep) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Step labels below circles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            steps.forEachIndexed { index, step ->
                Text(
                    text = step,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (index == currentStep) Color.Black else Color.DarkGray
                    )
                )
            }
        }
    }
}










