package com.upn.catatlari.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatRunDate(date: Date): String {
    val now = Calendar.getInstance()
    val input = Calendar.getInstance().apply { time = date }

    val isToday =
        now.get(Calendar.YEAR) == input.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == input.get(Calendar.DAY_OF_YEAR)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    return if (isToday) {
        "Today at ${timeFormat.format(date)}"
    } else {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        "${dateFormat.format(date)} at ${timeFormat.format(date)}"
    }
}