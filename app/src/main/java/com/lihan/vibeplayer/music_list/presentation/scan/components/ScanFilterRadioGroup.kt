package com.lihan.vibeplayer.music_list.presentation.scan.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.design_system.buttons.VPRadioButton
import com.lihan.vibeplayer.ui.theme.ButtonPrimary30
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun ScanFilterRadioGroup(
    title: String,
    selected: String,
    radioSelects: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = TextSecondary
        )
        Row(
            modifier = Modifier.selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            radioSelects.forEach{ text ->
                val isSelected = selected == text
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .border(
                            width = 1.dp,
                            color = if (isSelected){
                                ButtonPrimary30
                            }else{
                                TextDisabled
                            },
                            shape = RoundedCornerShape(100)
                        )
                        .weight(1f)
                        .clickable{
                            onSelect(text)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    VPRadioButton(
                        selected = isSelected,
                        onClick = {
                            onSelect(text)
                        }
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                    )
                }
            }
        }
    }

}

@Preview
@Composable
private fun ScanFilterRadioGroupPreview() {
    VibePlayerTheme {
        ScanFilterRadioGroup(
            title = stringResource(R.string.scan_music_filter_ignore_duration_less_than),
            selected = "30s",
            radioSelects = listOf(
                "30s","60s"
            ),
            onSelect = {}
        )

    }
}