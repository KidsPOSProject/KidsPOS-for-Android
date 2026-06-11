package info.nukoneko.cuc.android.kidspos.util

enum class Mode(val modeName: String) {
    PRODUCTION("本番"), PRACTICE("練習");

    fun toggle(): Mode = when (this) {
        PRODUCTION -> PRACTICE
        PRACTICE -> PRODUCTION
    }

    companion object {
        fun nameOf(name: String?): Mode {
            return values().singleOrNull { it.name == name } ?: PRACTICE
        }
    }
}