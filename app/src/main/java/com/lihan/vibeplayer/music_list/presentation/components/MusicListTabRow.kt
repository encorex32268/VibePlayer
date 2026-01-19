package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.music_list.presentation.util.tabIndicatorOffset
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary

const val SONGS = 0
const val PLAYLIST = 1


@Composable
fun MusicListTabRow(
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
) {
    val density = LocalDensity.current
    
    val tabsWidth = remember {
        mutableStateMapOf<Int, Dp>()
    }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(
                        currentTabPosition = it[selectedTabIndex],
                        currentTabWidth = tabsWidth[selectedTabIndex]?:0.dp
                    ),
                color = TextPrimary
            )
        },
        divider = {
            HorizontalDivider(color = SurfaceOutline)
        },
        containerColor = SurfaceBG,
        contentColor = TextPrimary,
        tabs = {
            Tab(
                selected = selectedTabIndex == SONGS,
                onClick = {
                    if (SONGS == selectedTabIndex){
                        return@Tab
                    }
                    onTabClick(SONGS)
                }
            ){
                Text(
                    modifier = Modifier
                        .padding(12.dp)
                        .onSizeChanged{
                            tabsWidth[0] = with(density){ it.width.toDp() }
                        },
                    text = "Songs",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedTabIndex == SONGS) TextPrimary else TextSecondary
                )
            }
            Tab(
                selected = selectedTabIndex == PLAYLIST,
                onClick = {
                    if (PLAYLIST == selectedTabIndex){
                        return@Tab
                    }
                    onTabClick(PLAYLIST)
                }
            ){
                Text(
                    modifier = Modifier.onSizeChanged{
                        tabsWidth[1] = with(density){ it.width.toDp() }
                    },
                    text = "Playlist",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedTabIndex == PLAYLIST) TextPrimary else TextSecondary
                )
            }
        }
    )
}