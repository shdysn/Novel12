package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NovelCategory
import com.example.ui.components.CategoryCard
import com.example.ui.components.NovelCoverCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.NovelViewModel

@Composable
fun CategoriesScreen(
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    val allNovels by viewModel.allNovels.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredNovels by viewModel.filteredNovels.collectAsState()

    val categoriesList = listOf(
        NovelCategory.MOHABBAT,
        NovelCategory.ROMANCE,
        NovelCategory.SPY,
        NovelCategory.SUSPENSE,
        NovelCategory.HISTORICAL,
        NovelCategory.ISLAHI,
        NovelCategory.SOCIAL,
        NovelCategory.SPIRITUAL,
        NovelCategory.CLASSIC
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmeraldDark)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedCategory != NovelCategory.ALL) {
                        IconButton(
                            onClick = { viewModel.selectCategory(NovelCategory.ALL) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to all categories",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column {
                        Text(
                            text = if (selectedCategory == NovelCategory.ALL) "اصنافِ کتب (Categories)" else selectedCategory.urduName,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (selectedCategory == NovelCategory.ALL) "ہر ذوق اور پسند کے لیے اردو ادب کا خزانہ" else "${filteredNovels.size} ناولز دستیاب ہیں",
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                if (selectedCategory != NovelCategory.ALL) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAccent.copy(alpha = 0.2f),
                        modifier = Modifier.clickable { viewModel.selectCategory(NovelCategory.ALL) }
                    ) {
                        Text(
                            text = "تمام دیکھیں",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = GoldAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        if (selectedCategory == NovelCategory.ALL) {
            // Grid of All Genres
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categoriesList, key = { it.name }) { category ->
                    val count = allNovels.count {
                        it.category.contains(category.urduName, ignoreCase = true) ||
                        (category == NovelCategory.MOHABBAT && it.category.contains("محبت")) ||
                        (category == NovelCategory.ROMANCE && it.category.contains("رومانوی")) ||
                        (category == NovelCategory.SPY && it.category.contains("جاسوسی")) ||
                        (category == NovelCategory.SUSPENSE && it.category.contains("سسپنس")) ||
                        (category == NovelCategory.HISTORICAL && it.category.contains("تاریخی")) ||
                        (category == NovelCategory.ISLAHI && it.category.contains("اصلاحی")) ||
                        (category == NovelCategory.SOCIAL && it.category.contains("معاشرتی")) ||
                        (category == NovelCategory.SPIRITUAL && it.category.contains("روحانی")) ||
                        (category == NovelCategory.CLASSIC && it.category.contains("کلاسیک"))
                    }

                    CategoryCard(
                        category = category,
                        novelsCount = maxOf(count, 1),
                        onClick = { viewModel.selectCategory(category) }
                    )
                }
            }
        } else {
            // Category Filtered Novels List
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredNovels.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "اس صنف میں فی الحال کوئی ناول دستیاب نہیں",
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    items(filteredNovels, key = { it.id }) { novel ->
                        NovelCoverCard(
                            novel = novel,
                            onClick = { viewModel.navigateToDetail(novel.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(novel.id) }
                        )
                    }
                }
            }
        }
    }
}
