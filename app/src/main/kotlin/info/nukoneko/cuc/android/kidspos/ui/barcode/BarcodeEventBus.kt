package info.nukoneko.cuc.android.kidspos.ui.barcode

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<BarcodeInput>(extraBufferCapacity = 1)
    val events: SharedFlow<BarcodeInput> = _events.asSharedFlow()

    fun emit(input: BarcodeInput) {
        _events.tryEmit(input)
    }
}
