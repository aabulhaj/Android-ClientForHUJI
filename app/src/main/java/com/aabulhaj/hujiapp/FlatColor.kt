package com.aabulhaj.hujiapp

import android.graphics.Color
import java.util.*


val FLAT_RED = Color.parseColor("#E74C3C")
val FLAT_DARK_RED = Color.parseColor("#C0392B")
val FLAT_GREEN = Color.parseColor("#2ECC71")
val FLAT_DARK_GREEN = Color.parseColor("#27AE60")
val FLAT_BLUE = Color.parseColor("#3498DB")
val FLAT_DARK_BLUE = Color.parseColor("#2980B9")
val FLAT_TEAL = Color.parseColor("#A1BC9C")
val FLAT_DARK_TEAL = Color.parseColor("#16A085")
val FLAT_PURPLE = Color.parseColor("#9B59B6")
val FLAT_DARK_PURPLE = Color.parseColor("#8E44AD")
val FLAT_YELLOW = Color.parseColor("#F1C40F")
val FLAT_DARK_YELLOW = Color.parseColor("#F39C12")
val FLAT_ORANGE = Color.parseColor("#E67E22")
val FLAT_DARK_ORANGE = Color.parseColor("#D35400")
val FLAT_GRAY = Color.parseColor("#95A5A6")
val FLAT_DARK_GRAY = Color.parseColor("#7F8C8D")
val FLAT_WHITE = Color.parseColor("#ECF0F1")
val FLAT_DARK_WHITE = Color.parseColor("#BDC3C7")
val FLAT_BLACK = Color.parseColor("#34495E")
val FLAT_DARK_BLACK = Color.parseColor("#2C3E50")

fun randomFlatColor(): Int {
    return randomFlatColorIncluding(true, true)
}

fun randomFlatLightColor(): Int {
    return randomFlatColorIncluding(true, false)
}

fun randomFlatDarkColor(): Int {
    return randomFlatColorIncluding(false, true)
}

fun randomFlatColorIncluding(lightShades: Boolean, darkShades: Boolean): Int {
    val numberOfLightColors = 10
    val numberOfDarkColors = 10

    if (!lightShades && !darkShades) {
        throw IllegalArgumentException("Must use random color using at least light or dark shades")
    }

    var numberOfColors = 0
    if (lightShades) {
        numberOfColors += numberOfLightColors
    }
    if (darkShades) {
        numberOfColors += numberOfDarkColors
    }

    var chosenColor = Random().nextInt(numberOfColors)
    if (!lightShades) {
        chosenColor += numberOfLightColors
    }

    when (chosenColor) {
        0 -> return FLAT_RED
        1 -> return FLAT_GREEN
        2 -> return FLAT_BLUE
        3 -> return FLAT_TEAL
        4 -> return FLAT_PURPLE
        5 -> return FLAT_YELLOW
        6 -> return FLAT_ORANGE
        7 -> return FLAT_GRAY
        8 -> return FLAT_WHITE
        9 -> return FLAT_BLACK
        10 -> return FLAT_DARK_RED
        11 -> return FLAT_DARK_GREEN
        12 -> return FLAT_DARK_BLUE
        13 -> return FLAT_DARK_TEAL
        14 -> return FLAT_DARK_PURPLE
        15 -> return FLAT_DARK_YELLOW
        16 -> return FLAT_DARK_ORANGE
        17 -> return FLAT_DARK_GRAY
        18 -> return FLAT_DARK_WHITE
        19 -> return FLAT_DARK_BLACK
        else -> throw IllegalArgumentException("unrecognized color selected as random color")
    }
}