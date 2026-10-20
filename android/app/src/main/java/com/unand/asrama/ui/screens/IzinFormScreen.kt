package com.unand.asrama.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unand.asrama.ui.theme.RoyalBlue
import com.unand.asrama.ui.theme.UnandGreen
import com.unand.asrama.util.ImageUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IzinFormScreen(
    onBackClick: () -> Unit,
    onSubmitIzin: (String, String, String, String, String?) -> Unit,
    isSubmitting: Boolean
) {
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var tglMulai by remember { mutableStateOf(today) }
    var tglSelesai by remember { mutableStateOf(today) }
    var kategori by remember { mutableStateOf("Sakit") }
    var alasan by remember { mutableStateOf("") }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var compressedBase64 by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Sakit", "PulangKampung", "Dispensasi", "DaruratKeluarga", "Lainnya")
    var expandedDropdown by remember { mutableStateOf(false) }

    // Smart document picker with client-side compression
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            compressedBase64 = ImageUtils.compressBitmapToBase64(bitmap, maxDimension = 1080, quality = 75)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengajuan Izin Digital", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Kategori Izin",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = kategori,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    kategori = item
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = tglMulai,
                        onValueChange = { tglMulai = it },
                        label = { Text("Tgl Mulai (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tglSelesai,
                        onValueChange = { tglSelesai = it },
                        label = { Text("Tgl Selesai (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = alasan,
                    onValueChange = { alasan = it },
                    label = { Text("Alasan Pengajuan Izin") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Smart Document Uploader
            item {
                Text(
                    text = "Foto Bukti (Surat Dokter / Surat Tugas)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedButton(
                    onClick = { photoPickerLauncher.launch(null) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedBitmap != null) "Ganti Foto Bukti (Telah Dikompres)" else "Ambil / Unggah Foto Bukti")
                }

                if (selectedBitmap != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    ) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "Preview Bukti",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onSubmitIzin(tglMulai, tglSelesai, kategori, alasan, compressedBase64)
                    },
                    enabled = !isSubmitting && alasan.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnandGreen),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(text = "Kirim Pengajuan Izin", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
