package com.example.playlistmaker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.playlistmaker.media.domain.models.Playlist

@Composable
fun Playlists(playlists: List<Playlist>, onClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(top = 12.dp, bottom = 60.dp)
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        items(playlists) { playlist ->
            Playlist(playlist, onClick)
        }
    }
}

@Composable
fun Playlist(playlist: Playlist, onClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable {
                onClick(playlist.id)
            }
    ) {
        PlaylistCover(
            url = playlist.coverPath.toString(),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .aspectRatio(1f),
        )
        playlist.title?.let {
            Text(
                text = it,
                //style = AppTheme.typography.caption,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
//        Text(
//            text = pluralStringResource(
//                R.plurals.track_plurals,
//                playlist.tracksCount,
//                playlist.tracksCount
//            ),
//            //style = AppTheme.typography.caption,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
    }
}

@Composable
fun PlaylistCover(url: String, modifier: Modifier) {
    AsyncImage(
        model = url,
        contentDescription = stringResource(R.string.playlist_desc),
        placeholder = painterResource(R.drawable.cover_placeholder),
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        error = painterResource(R.drawable.cover_placeholder)
    )
}

@Composable
fun TrackPluralsText(tracksCount: Int) {

    val oneTrack = stringResource(R.string.tracks_count_single)
    val fewTracks = stringResource(R.string.tracks_count_few)
    val manyTracks = stringResource(R.string.tracks_count_many)

    val trackCountString = remember {
        when {
            (tracksCount % 10 == 1) ->
                "$tracksCount $oneTrack"

            (tracksCount % 10 in 2..4) ->
                "$tracksCount $fewTracks"

            else ->
                "$tracksCount $manyTracks"
        }
    }

    Text(
        text = trackCountString,
        //style = AppTheme.typography.caption,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}