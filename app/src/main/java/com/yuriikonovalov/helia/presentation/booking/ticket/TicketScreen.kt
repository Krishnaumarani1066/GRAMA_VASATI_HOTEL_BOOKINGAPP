package com.yuriikonovalov.helia.presentation.booking.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.yuriikonovalov.helia.R
import com.yuriikonovalov.helia.designsystem.clickableWithoutIndication
import com.yuriikonovalov.helia.designsystem.components.Divider
import com.yuriikonovalov.helia.designsystem.components.PrimaryButton
import com.yuriikonovalov.helia.designsystem.theme.HeliaTheme
import com.yuriikonovalov.helia.domain.entities.BookedHotel

@Composable
fun TicketScreen(
    hotelId: String,
    onNavigateClick: () -> Unit,
    viewModel: TicketViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TicketScreenContent(
        state = state,
        onNavigateClick = onNavigateClick
    )
}

@Composable
private fun TicketScreenContent(
    state: TicketUiState,
    onNavigateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HeliaTheme.backgroundColor)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (HeliaTheme.theme.isDark) HeliaTheme.colors.dark2
                        else HeliaTheme.colors.greyscale100
                    )
                    .clickableWithoutIndication(onNavigateClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = null,
                    tint = HeliaTheme.primaryTextColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "My Ticket",
                style = HeliaTheme.typography.heading4,
                color = HeliaTheme.primaryTextColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (state.bookedHotel != null) {
                TicketCard(
                    bookedHotel = state.bookedHotel,
                    bookingId = state.bookingId,
                    checkInDate = state.checkInDate,
                    checkOutDate = state.checkOutDate,
                    duration = state.duration,
                    guests = state.guests
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Download Ticket",
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading ticket...",
                        style = HeliaTheme.typography.bodyLargeRegular,
                        color = HeliaTheme.secondaryTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketCard(
    bookedHotel: BookedHotel,
    bookingId: String,
    checkInDate: String,
    checkOutDate: String,
    duration: String,
    guests: String
) {
    val hotel = bookedHotel.hotel

    AsyncImage(
        model = hotel.imageUrl,
        contentDescription = hotel.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    )

    Spacer(modifier = Modifier.height(20.dp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (HeliaTheme.theme.isDark) HeliaTheme.colors.dark2 else HeliaTheme.colors.white,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = hotel.name,
                style = HeliaTheme.typography.heading5,
                color = HeliaTheme.primaryTextColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    tint = HeliaTheme.colors.primary500,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${hotel.city}, ${hotel.country}",
                    style = HeliaTheme.typography.bodyMediumRegular,
                    color = HeliaTheme.secondaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TicketDetailItem(label = "Check In", value = checkInDate, alignEnd = false)
                TicketDetailItem(label = "Check Out", value = checkOutDate, alignEnd = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TicketDetailItem(label = "Duration", value = duration, alignEnd = false)
                TicketDetailItem(label = "Guests", value = guests, alignEnd = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TicketDetailItem(label = "Status", value = bookedHotel.status.name, alignEnd = false)
                TicketDetailItem(label = "Price / Night", value = "$${hotel.price}", alignEnd = true)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booking ID",
                    style = HeliaTheme.typography.bodyMediumRegular,
                    color = HeliaTheme.secondaryTextColor
                )
                Text(
                    text = bookingId,
                    style = HeliaTheme.typography.bodyMediumBold,
                    color = HeliaTheme.primaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(HeliaTheme.colors.greyscale100),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bookingId,
                    style = HeliaTheme.typography.bodySmallRegular,
                    color = HeliaTheme.colors.greyscale500,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TicketDetailItem(
    label: String,
    value: String,
    alignEnd: Boolean
) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(
            text = label,
            style = HeliaTheme.typography.bodySmallRegular,
            color = HeliaTheme.secondaryTextColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = HeliaTheme.typography.bodyMediumBold,
            color = HeliaTheme.primaryTextColor
        )
    }
}