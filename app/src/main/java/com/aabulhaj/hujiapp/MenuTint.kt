package com.aabulhaj.hujiapp


import android.graphics.Color
import android.graphics.PorterDuff
import android.view.Menu

object MenuTint {

    fun tint(menu: Menu) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)

            val icon = item.icon ?: continue

            val color = Color.rgb(100, 100, 100)

            icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}