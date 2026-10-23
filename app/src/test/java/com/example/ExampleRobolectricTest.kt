package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("اردو ناولز", appName)
  }

  @Test
  fun `test TextPaginator paginate in Android environment`() {
    val sampleText = "امامہ ہاشم نے کھڑکی سے باہر دیکھا جہاں بارش کے قطرے شیشے سے ٹکرا رہے تھے۔\nزندگی کے فیصلے انسان کو کس موڑ پر لے آتے ہیں، یہ کوئی نہیں جانتا۔"
    val pages = com.example.util.TextPaginator.paginate(
        text = sampleText,
        widthPx = 800,
        heightPx = 1200,
        textSizePx = 40f,
        lineSpacingMultiplier = 1.8f,
        paragraphSpacingPx = 20f,
        fontStyle = com.example.data.model.FontStyleType.NASTALEEQ
    )
    org.junit.Assert.assertTrue(pages.isNotEmpty())
    assertEquals(1, pages.first().pageNumber)
    org.junit.Assert.assertTrue(pages.first().isFirstPageOfChapter)
    org.junit.Assert.assertTrue(pages.last().isLastPageOfChapter)
  }

  @Test
  fun `test Room Database inserts and queries novel entity`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    val novel = com.example.data.local.entities.NovelEntity(
        id = "test_peer_kamil",
        titleUrdu = "پیرِ کامل (ص)",
        authorName = "عمیرہ احمد",
        categoryName = "روحانی",
        description = "شاہکار اردو ناول",
        isDownloaded = true,
        downloadStatus = com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE.name
    )

    db.novelDao().insertNovel(novel)
    val fetched = db.novelDao().getNovelById("test_peer_kamil")
    org.junit.Assert.assertNotNull(fetched)
    assertEquals("پیرِ کامل (ص)", fetched?.titleUrdu)
    assertEquals("عمیرہ احمد", fetched?.authorName)
    org.junit.Assert.assertTrue(fetched!!.isDownloaded)
    db.close()
  }
}
