package com.example.playlistmaker

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TrackList(tracks: List<Track>, onTrackClick: (trackId: Int) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(
                track.trackId,
                track.trackName,
                track.artistName,
                track.artworkUrl100,
                track.trackTimeMillis,
                onTrackClick)
        }
    }
}

@Composable
fun TrackItem(
    trackId: Int?,
    trackName: String?,
    trackArtist: String?,
    trackUrl: String?,
    trackDuration: String?,
    onTrackClick: (trackId: Int) -> Unit
) {
    val trackDuration = if (!trackDuration.isNullOrBlank()) {

        val parts = trackDuration.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            String.format("%02d:%02d", minutes, seconds)
        } else {
            stringResource(R.string.nothings_found)
        }
    } else {
        stringResource(R.string.nothings_found)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 12.dp)
            .clickable {
                if (trackId != null) onTrackClick(trackId)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TrackSmallCover(trackUrl)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            trackName?.let {
                Text(
                    text = it,
                    //style = AppTheme.typography.h4,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.Start) {
                trackArtist?.let {
                    Text(
                        text = it,
                        //style = AppTheme.typography.overline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ellipse_icon13),
                    contentDescription = "",
                    //tint = AppTheme.colors.colorOnTertiary
                )
                Text(
                    text = trackDuration,
                    //style = AppTheme.typography.overline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.arrow_forward_icon),
            contentDescription = null, // добавить
            //tint = AppTheme.colors.colorOnTertiary,
        )
    }
}

@Composable
fun TrackSmallCover(url: String?) {
    AsyncImage(
        model = url,
        contentDescription = stringResource(R.string.cover_ph_desc),
        placeholder = painterResource(R.drawable.cover_placeholder),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(2.dp)),
        error = painterResource(R.drawable.cover_placeholder),
    )
}

@Composable
fun TrackListHistory(
    tracks: List<ListTrackInfo>,
    onTrackClick: (trackId: Int) -> Unit,
    onButtonClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(
                track.trackId,
                track.trackName,
                track.artistName,
                track.artworkUrl,
                track.trackTime,
                onTrackClick)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(title = stringResource(R.string.search_history_clear), onClick = onButtonClick)
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ListOfListTrackInfo(tracks: List<ListTrackInfo>, onTrackClick: (trackId: Int) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(
                track.trackId,
                track.trackName,
                track.artistName,
                track.artworkUrl,
                track.trackTime,
                onTrackClick)
        }
    }
}