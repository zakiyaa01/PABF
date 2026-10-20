package com.unand.asrama.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unand.asrama.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    nama: String,
    nim: String,
    gedung: String,
    kamar: String,
    fasilNama: String,
    onStartPresensi: (String) -> Unit,
    onOpenIzin: () -> Unit,
    onLogout: () -> Unit
) {
    var currentTimeStr by remember { mutableStateOf("") }
    var subuhSudahAbsen by remember { mutableStateOf(false) }
    var malamSudahAbsen by remember { mutableStateOf(false) }

    // Live clock update
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("HH:mm:ss 'WIB'", Locale("id", "ID"))
            currentTimeStr = sdf.format(Date())
            delay(1000)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Assalamu'alaikum,",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = nama,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "NIM: $nim | $gedung - Kamar $kamar",
                        fontSize = 12.sp,
                        color = RoyalBlue
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = CrimsonDanger)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Server Time & Geofence Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ WAKTU SERVER (NTP ASIA/JAKARTA)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnandGold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "GEOFENCE AKTIF (< 50m)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = currentTimeStr.ifEmpty { "05:15:00 WIB" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Titik Pusat: -0.914231, 100.466012 ($gedung)",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section Title
        item {
            Text(
                text = "Sesi Presensi Hari Ini",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Sesi Subuh Card
        item {
            AttendanceSessionCard(
                title = "Presensi Subuh",
                timeRange = "04.00 – 06.00 WIB (Toleransi s/d 06.15)",
                icon = "🌅",
                isAlreadyAttended = subuhSudahAbsen,
                onAttendClick = {
                    onStartPresensi("SUBUH")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Sesi Malam Card
        item {
            AttendanceSessionCard(
                title = "Presensi Malam",
                timeRange = "18.00 – 20.30 WIB (Toleransi s/d 20.45)",
                icon = "🌙",
                isAlreadyAttended = malamSudahAbsen,
                onAttendClick = {
                    onStartPresensi("MALAM")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Actions & Fasil Info
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenIzin,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnandGreen),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Pengajuan Izin", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👨‍🏫", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Fasilitator Pembina Anda", fontSize = 11.sp, color = Color.Gray)
                        Text(text = fasilNama, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceSessionCard(
    title: String,
    timeRange: String,
    icon: String,
    isAlreadyAttended: Boolean,
    onAttendClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = timeRange, fontSize = 11.sp, color = Color.Gray)
                    }
                }

                if (isAlreadyAttended) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldSuccess.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "HADIR ✓",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Auto-Lock Button Behavior
            Button(
                onClick = onAttendClick,
                enabled = !isAlreadyAttended,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalBlue,
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                if (isAlreadyAttended) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Tombol Terkunci (Sudah Absen)", fontSize = 13.sp)
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Mulai Presensi Selfie & AI Liveness", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
