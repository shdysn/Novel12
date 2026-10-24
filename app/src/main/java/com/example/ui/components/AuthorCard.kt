package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Author
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.UrduFontFamily

val AuthorGradients = listOf(
    listOf(Color(0xFF0F3B2E), Color(0xFF1B5E48)),
    listOf(Color(0xFF3E2723), Color(0xFF5D4037)),
    listOf(Color(0xFF1B4965), Color(0xFF2B2D42)),
    listOf(Color(0xFF512B58), Color(0xFF2C1E38)),
    listOf(Color(0xFF4A1521), Color(0xFF782839))
)

@Composable
fun AuthorCard(
    author: Author,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFollowing: Boolean = false,
    onFollowClick: () -> Unit = onClick
) {
    val gradientColors = AuthorGradients[author.avatarColorIndex % AuthorGradients.size]

    Column(
        modifier = modifier
            .width(84.dp)
            .clickable(onClick = onClick)
            .testTag("author_card_${author.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Portrait Avatar with Gold Border
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(gradientColors))
                .border(2.dp, if (isFollowing) GoldAccent else GoldAccent.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // First character or initials of author name in artistic Urdu calligraphy
            val initials = author.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
            Text(
                text = initials.ifBlank { "ع" },
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = author.name,
            fontFamily = UrduFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(3.dp))

        // "فالو" Pill Button matching reference UI screen 1
        androidx.compose.material3.Surface(
            shape = CircleShape,
            color = if (isFollowing) Color(0xFF2C1E38) else GoldAccent,
            border = if (isFollowing) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
            modifier = Modifier
                .clickable(onClick = onFollowClick)
                .testTag("follow_pill_${author.id}")
        ) {
            Text(
                text = if (isFollowing) "فالوڈ ✓" else "فالو",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isFollowing) GoldAccent else Color(0xFF1B1500),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
            )
        }
    }
}
