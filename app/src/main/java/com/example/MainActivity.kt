package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NovelDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PublisherScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.UrduNovelReaderTheme
import com.example.ui.viewmodel.NovelViewModel
import com.example.ui.viewmodel.ScreenState

class MainActivity : ComponentActivity() {

    private val viewModel: NovelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UrduNovelReaderTheme {
                // Apply Right-To-Left (RTL) Layout Direction globally for Urdu UI
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainAppScaffold(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: NovelViewModel) {
    val screenState by viewModel.screenState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showPublisherSheet by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Only show bottom navigation when on Main screen (tabs), hide during NovelDetail or Reader
            if (screenState is ScreenState.Main) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    // 1. Home (ہوم)
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "ہوم"
                            )
                        },
                        label = {
                            Text(
                                text = "ہوم",
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    // 2. Categories (کیٹیگریز)
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.Category else Icons.Outlined.Category,
                                contentDescription = "کیٹیگریز"
                            )
                        },
                        label = {
                            Text(
                                text = "کیٹیگریز",
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_categories")
                    )

                    // 3. Search (سرچ)
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "سرچ"
                            )
                        },
                        label = {
                            Text(
                                text = "سرچ",
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_search")
                    )

                    // 4. Library (میری لائبریری)
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Filled.LocalLibrary else Icons.Outlined.LocalLibrary,
                                contentDescription = "لائبریری"
                            )
                        },
                        label = {
                            Text(
                                text = "میری لائبریری",
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_library")
                    )

                    // 5. Profile (پروفائل)
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { viewModel.selectTab(4) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 4) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "پروفائل"
                            )
                        },
                        label = {
                            Text(
                                text = "پروفائل",
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = screenState, label = "screen_transition") { state ->
                when (state) {
                    is ScreenState.Main -> {
                        when (selectedTab) {
                            0 -> HomeScreen(viewModel = viewModel)
                            1 -> CategoriesScreen(viewModel = viewModel)
                            2 -> SearchScreen(viewModel = viewModel)
                            3 -> LibraryScreen(viewModel = viewModel)
                            4 -> ProfileScreen(
                                viewModel = viewModel,
                                onOpenPublisher = { showPublisherSheet = true }
                            )
                        }
                    }
                    is ScreenState.NovelDetail -> {
                        NovelDetailScreen(novelId = state.novelId, viewModel = viewModel)
                    }
                    is ScreenState.Reader -> {
                        ReaderScreen(
                            novelId = state.novelId,
                            chapterId = state.chapterId,
                            initialCharOffset = state.initialCharOffset,
                            viewModel = viewModel
                        )
                    }
                    is ScreenState.AuthorDetail -> {
                        // Handled via author filtered novels
                        CategoriesScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Publisher & Cloud Modal Sheet
    if (showPublisherSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = { showPublisherSheet = false },
            sheetState = sheetState
        ) {
            PublisherScreen(viewModel = viewModel)
        }
    }
}
