package info.nukoneko.cuc.android.kidspos.manager

import info.nukoneko.cuc.android.kidspos.api.APIAdapter
import info.nukoneko.cuc.android.kidspos.api.APIService

abstract class BaseManager(private val apiAdapter: APIAdapter) {
    internal val apiService: APIService
        get() = apiAdapter.apiService
}