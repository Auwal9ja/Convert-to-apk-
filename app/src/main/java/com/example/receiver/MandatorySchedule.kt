package com.example.receiver

enum class MandatorySessionState {
    IDLE,
    SCHEDULED,
    TRIGGERED,
    STARTING,
    ACTIVE,
    COMPLETED,
    MISSED,
    CANCELLED,
    ERROR
}

data class MandatorySchedule(
    val id: String,
    val title: String,
    val category: String,
    val hour: Int,
    val minute: Int,
    val durationMinutes: Int,
    val enabled: Boolean,
    val repeatDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoPlayAudio: Boolean = false
)
