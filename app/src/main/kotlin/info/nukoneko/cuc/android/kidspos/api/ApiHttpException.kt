package info.nukoneko.cuc.android.kidspos.api

import java.io.IOException

class ApiHttpException(val code: Int) : IOException("HTTP $code")
