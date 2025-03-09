package com.example.audiovideoplayer.ui.audio

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.audiovideoplayer.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioItem(audio: AudioModel, navController: NavController, audioList: List<AudioModel>) {
    val musicImages = listOf(
        R.drawable.image_1, R.drawable.image_2, R.drawable.image_3,
        R.drawable.image_4, R.drawable.image_5, R.drawable.image_6,
        R.drawable.image_7, R.drawable.image_8
    )

    val index = remember { audioList.indexOf(audio) }
    val imageRes = musicImages[index % musicImages.size]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    val encodedPath = Uri.encode(audio.path)
                    navController.navigate("music_player/$index/$encodedPath")
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageRes,
            contentDescription = "Music Icon",
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = audio.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.White,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (audio.artist.isNullOrBlank() || audio.artist == "<unknown>") "Unknown Artist" else audio.artist,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}
