package com.upn.catatlari.utils

import android.view.View



fun View.setClickAnimation(action: () -> Unit) {
    this.setOnClickListener {


        it.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                it.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()

                action()
                isClickable = true
            }
            .start()
    }
}