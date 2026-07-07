package app.aaps.wear.tile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BGTileMockup(
    sgv: String,
    trend: String,
    delta: String,
    iob: String,
    cob: String,
    timeAgo: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(192.dp)
            .background(Color.Black, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Text(
                text = sgv,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = trend, fontSize = 22.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = delta, fontSize = 22.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "IOB: $iob", fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "COB: $cob", fontSize = 14.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = timeAgo,
                fontSize = 12.sp,
                color = Color(0xFFB0BEC5)
            )
        }
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
fun PreviewBGTileNormal() {
    BGTileMockup(
        sgv = "5.5",
        trend = "→",
        delta = "+0.1",
        iob = "1.2U",
        cob = "25g",
        timeAgo = "3 min ago",
        color = Color(0xFF81C784)
    )
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
fun PreviewBGTileHigh() {
    BGTileMockup(
        sgv = "14.2",
        trend = "↗",
        delta = "+0.8",
        iob = "0.5U",
        cob = "60g",
        timeAgo = "1 min ago",
        color = Color(0xFFFFD600)
    )
}
