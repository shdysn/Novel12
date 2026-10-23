package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NovelCategory
import com.example.ui.theme.GoldAccent

fun getCategoryIconAndColors(category: NovelCategory): Triple<ImageVector, Color, Color> {
    return when (category) {
        NovelCategory.MOHABBAT -> Triple(Icons.Default.Favorite, Color(0xFFC2185B), Color(0xFF880E4F))
        NovelCategory.ROMANCE -> Triple(Icons.Default.Favorite, Color(0xFFAD1457), Color(0xFF4A148C))
        NovelCategory.SPY -> Triple(Icons.Default.Fingerprint, Color(0xFF283593), Color(0xFF0D1B2A))
        NovelCategory.SUSPENSE -> Triple(Icons.Default.Visibility, Color(0xFF455A64), Color(0xFF212121))
        NovelCategory.HISTORICAL -> Triple(Icons.Default.HistoryEdu, Color(0xFF5D4037), Color(0xFF3E2723))
        NovelCategory.ISLAHI -> Triple(Icons.Default.SelfImprovement, Color(0xFF2E7D32), Color(0xFF0F3B2E))
        NovelCategory.SOCIAL -> Triple(Icons.Default.Public, Color(0xFF00695C), Color(0xFF004D40))
        NovelCategory.SPIRITUAL -> Triple(Icons.Default.AutoAwesome, Color(0xFF1565C0), Color(0xFF0D47A1))
        NovelCategory.CLASSIC -> Triple(Icons.AutoMirrored.Filled.MenuBook, Color(0xFF827717), Color(0xFF33691E))
        NovelCategory.ALL -> Triple(Icons.Default.Book, Color(0xFF1B5E48), Color(0xFF0F3B2E))
    }
}

@Composable
fun CategoryCard(
    category: NovelCategory,
    novelsCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, startColor, endColor) = getCategoryIconAndColors(category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clickable(onClick = onClick)
            .testTag("category_card_${category.name}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(startColor.copy(alpha = 0.9f), endColor)
                    )
                )
                .padding(14.dp)
        ) {
            // Background faint icon decoration
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.BottomStart)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "$novelsCount ناولز",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = category.englishName,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = category.urduName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = category.englishName,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}
