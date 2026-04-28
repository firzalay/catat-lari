package com.upn.catatlari.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Run(val runDate: String,
               val runDistance: Int,
               val runDuration: Int) : Parcelable
