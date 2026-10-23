package com.example.data.model

data class SupabaseConfig(
    val url: String = "",
    val anonKey: String = "",
    val isConnected: Boolean = false,
    val lastSyncMessage: String = "کنکشن کا انتظار ہے",
    val lastSyncTime: Long = 0L,
    val totalSyncedNovels: Int = 0
) {
    val isConfigured: Boolean
        get() = url.isNotBlank() && (url.startsWith("https://") || url.startsWith("http://")) && anonKey.isNotBlank()
}
