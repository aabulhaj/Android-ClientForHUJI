package com.aabulhaj.hujiapp

import android.content.Context


enum class Moed {
    A, B, C;


    companion object {

        fun getMoedString(moed: Moed?, context: Context): String {
            return if (moed == null)
                ""
            else if (moed == Moed.A)
                context.getString(R.string.moed_a)
            else if (moed == Moed.B)
                context.getString(R.string.moed_b)
            else
                context.getString(R.string.moed_c)

        }
    }
}