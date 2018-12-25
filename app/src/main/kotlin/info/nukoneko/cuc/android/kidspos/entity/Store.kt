package info.nukoneko.cuc.android.kidspos.entity

data class Store(val id: Int, val name: String) {
    val isValid: Boolean
        get() = id > -1 && name.isNotEmpty()
}
