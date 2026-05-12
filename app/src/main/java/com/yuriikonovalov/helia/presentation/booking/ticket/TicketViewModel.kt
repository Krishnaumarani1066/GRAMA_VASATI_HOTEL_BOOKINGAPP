package com.yuriikonovalov.helia.presentation.booking.ticket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.helia.domain.entities.BookedHotel
import com.yuriikonovalov.helia.domain.usecases.GetBookedHotelsUseCase
import com.yuriikonovalov.helia.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookedHotels: GetBookedHotelsUseCase
) : ViewModel() {

    private val hotelId: String = checkNotNull(savedStateHandle[TicketNavigation.hotelIdArg])

    private val _state = MutableStateFlow(TicketUiState())
    val state = _state.asStateFlow()

    init {
        loadTicket()
    }

    private fun loadTicket() {
        viewModelScope.launch {
            val result = getBookedHotels().first()
            if (result is Result.Success) {
                val bookedHotel = result.data.find { it.hotel.id == hotelId }
                _state.update { old ->
                    old.copy(
                        bookedHotel = bookedHotel,
                        bookingId = generateBookingId(hotelId),
                        checkInDate = "12 Jun 2025",
                        checkOutDate = "16 Jun 2025",
                        duration = "4 Nights",
                        guests = "2 Adults"
                    )
                }
            }
        }
    }

    private fun generateBookingId(hotelId: String): String {
        val hash = hotelId.hashCode().and(0xFFFFFF).toString(16).uppercase()
        return "HEL-$hash"
    }
}

data class TicketUiState(
    val bookedHotel: BookedHotel? = null,
    val bookingId: String = "",
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val duration: String = "",
    val guests: String = ""
)