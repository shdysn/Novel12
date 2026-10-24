package com.example.util

import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import com.example.data.model.BookPage
import com.example.data.model.FontStyleType

object TextPaginator {

    /**
     * Paginates chapter text into individual BookPage items using Android's native
     * StaticLayout measurement, ensuring zero clipped lines, exact character tracking,
     * natural Nastaliq shaping, and RTL paragraph wrapping.
     */
    fun paginate(
        text: String,
        widthPx: Int,
        heightPx: Int,
        textSizePx: Float,
        lineSpacingMultiplier: Float,
        paragraphSpacingPx: Float,
        fontStyle: FontStyleType,
        context: android.content.Context? = null
    ): List<BookPage> {
        val cleanText = text.replace(Regex(" {2,}"), " ").trim()
        if (cleanText.isEmpty() || widthPx <= 80 || heightPx <= 80) {
            return listOf(
                BookPage(
                    pageIndex = 0,
                    pageNumber = 1,
                    startCharOffset = 0,
                    endCharOffset = text.length,
                    text = text,
                    isFirstPageOfChapter = true,
                    isLastPageOfChapter = true
                )
            )
        }

        val fontResId = when (fontStyle) {
            FontStyleType.PDMS_JAUHAR -> com.example.R.font.pdms_jauhar
            FontStyleType.NASTALEEQ -> com.example.R.font.noto_nastaliq_urdu
            FontStyleType.MEHR_NASTALEEQ -> com.example.R.font.gulzar
            FontStyleType.NASKH -> com.example.R.font.amiri
            FontStyleType.LATEEF -> com.example.R.font.lateef
            FontStyleType.SCHEHERAZADE -> com.example.R.font.scheherazade_new
        }

        val loadedTypeface = try {
            context?.let { androidx.core.content.res.ResourcesCompat.getFont(it, fontResId) }
        } catch (e: Exception) {
            null
        } ?: Typeface.SERIF

        val textPaint = TextPaint().apply {
            isAntiAlias = true
            isSubpixelText = true
            textSize = textSizePx
            typeface = loadedTypeface
        }

        // Split text by paragraphs while recording exact character offsets
        val paragraphs = mutableListOf<ParagraphSpan>()
        var searchStart = 0
        while (searchStart < text.length) {
            val nextNewline = text.indexOf('\n', searchStart)
            if (nextNewline == -1) {
                paragraphs.add(ParagraphSpan(searchStart, text.length, text.substring(searchStart)))
                break
            } else {
                paragraphs.add(ParagraphSpan(searchStart, nextNewline, text.substring(searchStart, nextNewline)))
                searchStart = nextNewline + 1
            }
        }

        if (paragraphs.isEmpty()) {
            return listOf(
                BookPage(
                    pageIndex = 0,
                    pageNumber = 1,
                    startCharOffset = 0,
                    endCharOffset = text.length,
                    text = text,
                    isFirstPageOfChapter = true,
                    isLastPageOfChapter = true
                )
            )
        }

        val pages = mutableListOf<BookPage>()
        var currentPageStartOffset = 0
        var currentPageEndOffset = 0
        val currentPageBuffer = StringBuilder()

        // Strict discrete line limit calculation:
        // In Jetpack Compose, each line takes exactly `textSizePx * lineSpacingMultiplier`
        val effectiveLineHeight = (textSizePx * lineSpacingMultiplier).coerceAtLeast(1f)
        // Reserve at least 0.45 of a line for Nastaliq descenders & safety margin
        val maxLinesPerPage = ((heightPx - (effectiveLineHeight * 0.45f)) / effectiveLineHeight).toInt().coerceAtLeast(1)
        var currentLinesOnPage = 0

        fun finishCurrentPage() {
            if (currentPageBuffer.isNotEmpty()) {
                val pageIndex = pages.size
                pages.add(
                    BookPage(
                        pageIndex = pageIndex,
                        pageNumber = pageIndex + 1,
                        startCharOffset = currentPageStartOffset,
                        endCharOffset = currentPageEndOffset,
                        text = currentPageBuffer.toString().trimEnd('\n'),
                        isFirstPageOfChapter = pageIndex == 0,
                        isLastPageOfChapter = false
                    )
                )
                currentPageBuffer.clear()
                currentLinesOnPage = 0
            }
        }

        for (p in paragraphs) {
            val pText = p.text.trim()
            if (pText.isEmpty()) {
                // Skip empty lines to prevent giant blank gaps inside the printed page
                continue
            }

            // Create StaticLayout for this paragraph to get exact line breaks
            val layout = createStaticLayout(p.text, textPaint, widthPx, lineSpacingMultiplier)

            for (line in 0 until layout.lineCount) {
                val lineStart = layout.getLineStart(line)
                val lineEnd = layout.getLineEnd(line)
                val lineText = p.text.substring(lineStart, lineEnd)

                // Check if adding this line exceeds the discrete line limit for the page
                if (currentLinesOnPage >= maxLinesPerPage && currentPageBuffer.isNotEmpty()) {
                    finishCurrentPage()
                    currentPageStartOffset = p.startOffset + lineStart
                }

                if (currentPageBuffer.isEmpty()) {
                    currentPageStartOffset = p.startOffset + lineStart
                }

                currentPageBuffer.append(lineText)
                currentPageEndOffset = p.startOffset + lineEnd
                currentLinesOnPage++
            }

            // Paragraph ends with a clean newline for the next paragraph
            if (currentPageBuffer.isNotEmpty() && !currentPageBuffer.endsWith("\n")) {
                if (currentLinesOnPage >= maxLinesPerPage) {
                    // No room for an empty paragraph gap on this page; next paragraph begins on fresh page
                    finishCurrentPage()
                } else {
                    currentPageBuffer.append("\n")
                    currentLinesOnPage++
                }
            }
        }

        // Finish last remaining page
        if (currentPageBuffer.isNotEmpty()) {
            val pageIndex = pages.size
            pages.add(
                BookPage(
                    pageIndex = pageIndex,
                    pageNumber = pageIndex + 1,
                    startCharOffset = currentPageStartOffset,
                    endCharOffset = text.length,
                    text = currentPageBuffer.toString().trimEnd('\n'),
                    isFirstPageOfChapter = pageIndex == 0,
                    isLastPageOfChapter = true
                )
            )
        }

        // Mark the last page as lastPageOfChapter
        if (pages.isNotEmpty()) {
            val last = pages.last()
            pages[pages.size - 1] = last.copy(isLastPageOfChapter = true)
        }

        return if (pages.isEmpty()) {
            listOf(
                BookPage(
                    pageIndex = 0,
                    pageNumber = 1,
                    startCharOffset = 0,
                    endCharOffset = text.length,
                    text = text,
                    isFirstPageOfChapter = true,
                    isLastPageOfChapter = true
                )
            )
        } else {
            pages
        }
    }

    /**
     * Finds the page index that contains the given character offset.
     * Ensures reading position is preserved across font size, margin, and pagination recalculations.
     */
    fun findPageIndexForCharOffset(pages: List<BookPage>, charOffset: Int): Int {
        if (pages.isEmpty() || charOffset <= 0) return 0
        val foundIndex = pages.indexOfFirst { page ->
            charOffset >= page.startCharOffset && charOffset < page.endCharOffset
        }
        return if (foundIndex != -1) {
            foundIndex
        } else if (charOffset >= (pages.lastOrNull()?.endCharOffset ?: 0)) {
            pages.size - 1
        } else {
            0
        }
    }

    private data class ParagraphSpan(val startOffset: Int, val endOffset: Int, val text: String)

    private fun createStaticLayout(
        text: CharSequence,
        paint: TextPaint,
        width: Int,
        lineSpacingMultiplier: Float
    ): StaticLayout {
        val safeWidth = width.coerceAtLeast(50)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, safeWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setTextDirection(TextDirectionHeuristics.RTL)
                .setLineSpacing(0f, lineSpacingMultiplier)
                .setIncludePad(true)
                .setBreakStrategy(android.graphics.text.LineBreaker.BREAK_STRATEGY_HIGH_QUALITY)
                .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                text,
                paint,
                safeWidth,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                0f,
                true
            )
        }
    }
}
