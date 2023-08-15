package com.droidcon.comicsworld.utils

import java.text.SimpleDateFormat
import java.util.*

val SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

fun String.parseStringToDate(): Date = SimpleDateFormat.parse(this)!!
fun Date.formatToString(): String = SimpleDateFormat.format(this)