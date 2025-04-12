package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.EventSink

@Composable
fun WalletComponent(
    isConnecting: Boolean,
    balance: String?,
    eventSink: (EventSink) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFE6F1E9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Akses Tanaman Herbal",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Hubungkan dompet Anda untuk berkontribusi & menjelajahi dunia tanaman herbal!",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    if (isConnecting && balance != null) {
                        Text(
                            text = "Saldo: $balance",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { eventSink(EventSink.Disconnect) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Keluar", fontSize = 14.sp, color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { eventSink(EventSink.Connect) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Connect Wallet", fontSize = 14.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("or", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = { eventSink(EventSink.GuestLogin) }) {
                            Text(
                                text = "As Guest",
                                fontSize = 14.sp,
                                color = Color(0xFF388E3C)
                            )
                        }
                    }
                }
            }
        }
    }
}
