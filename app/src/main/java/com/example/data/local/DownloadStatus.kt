package com.example.data.local

enum class DownloadStatus(val urduLabel: String, val englishLabel: String) {
    NOT_DOWNLOADED("ڈاؤن لوڈ کریں", "Not Downloaded"),
    DOWNLOADING("ڈاؤن لوڈ ہو رہا ہے...", "Downloading..."),
    AVAILABLE_OFFLINE("آف لائن دستیاب", "Available Offline"),
    FAILED("ڈاؤن لوڈ ناکام", "Download Failed"),
    UPDATE_AVAILABLE("نیا ایڈیشن دستیاب", "Update Available");

    companion object {
        fun fromString(value: String?): DownloadStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NOT_DOWNLOADED
        }
    }
}
