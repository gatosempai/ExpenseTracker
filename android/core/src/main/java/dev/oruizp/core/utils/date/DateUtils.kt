package dev.oruizp.core.utils.date

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Formats a timestamp into:
 * - "Today h:mm a" (e.g., "Today 8:56 pm") for the current calendar day
 * - "EEEE d MMMM yyyy" (e.g., "Monday 25 April 2026") for other dates
 *
 * @param timestampMillis System.currentTimeMillis() value
 * @param zoneId timezone to use (defaults to system default)
 * @return formatted string
 */
@RequiresApi(Build.VERSION_CODES.O)
fun formatPurchaseTime(timestampMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
    val dateTime = Instant.ofEpochMilli(timestampMillis).atZone(zoneId).toLocalDateTime()
    val today = LocalDate.now(zoneId)

    return if (dateTime.toLocalDate() == today) {
        // Format time as "h:mm a" e.g., "8:56 pm"
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        val timePart = dateTime.format(timeFormatter).lowercase() // convert AM/PM to am/pm
        "Today $timePart"
    } else {
        // Format date as "Monday 25 April 2026"
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.getDefault())
        dateTime.format(dateFormatter)
    }
}

fun formatPurchaseTimeLegacy(timestampMillis: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
    val today = Calendar.getInstance()

    return if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    ) {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        "Today ${timeFormat.format(Date(timestampMillis)).lowercase()}"
    } else {
        val dateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault())
        dateFormat.format(Date(timestampMillis))
    }
}

fun formatTime(timestampMillis: Long): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        formatPurchaseTime(timestampMillis)
    } else {
        formatPurchaseTimeLegacy(timestampMillis)
    }
}