package com.vacation.tripinmind.home.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import com.vacation.tripinmind.util.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrcodeBottomSheet(
    onDismiss: () -> Unit,
    qrcode: String,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null,
        shape = RectangleShape,
        tonalElevation = 0.dp,
        contentWindowInsets = { WindowInsets(0.dp) }
    ) {
        val qrBitmap = rememberQrBitmap(content = qrcode)
        Surface(
            color = Color.White,
            shape = BottomSheetDefaults.ExpandedShape,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = BottomSheetDefaults.ExpandedShape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                BottomSheetDefaults.DragHandle(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.homescreen_dialog_qrcode_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth()
                    )

                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(250.dp)
                                .padding(bottom = 24.dp)
                        )
                    }

                    Text(
                        text = qrcode,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun rememberQrBitmap(content: String, size: Int = 512): Bitmap? {
    return remember(content, size) {
        QRCodeGenerator.generate(content, size)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrcodeBottomSheetPreview() {
    MVIAppTheme {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        QrcodeBottomSheet(
            onDismiss = {},
            qrcode = "AAA-222-EEE-344",
            sheetState = sheetState
        )
    }
}
