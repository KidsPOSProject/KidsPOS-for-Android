package info.nukoneko.cuc.android.kidspos.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barcode(val value: String) : Parcelable
