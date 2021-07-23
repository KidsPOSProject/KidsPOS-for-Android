package info.nukoneko.cuc.android.kidspos.api

sealed class RequestStatus<T> {
    class IDLE<T> : RequestStatus<T>()
    class REQUESTING<T> : RequestStatus<T>()
    data class SUCCEEDED<T>(val body: T) : RequestStatus<T>()
    data class FAILED<T>(val error: Throwable) : RequestStatus<T>()
}
