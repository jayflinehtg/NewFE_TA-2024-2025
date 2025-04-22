package com.example.myapplication

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DetailScreen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content(plantId: String, onBack: () -> Unit) {
        val context = LocalContext.current
        val backgroundColor = colorResource(id = R.color.soft_green)
        val darkGreen = colorResource(id = R.color.dark_green)

        println("DetailScreen: plantId diterima = $plantId")

        var comment by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Postingan", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(painterResource(R.drawable.baseline_arrow_back_24), contentDescription = "Back", tint = darkGreen )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle like */ }) {
                            Icon(Icons.Filled.FavoriteBorder, contentDescription = "Like", tint = Color.Red)
                        }
                    }
                )
            },
            bottomBar = {
                CommentInputSection(
                    comment = comment,
                    onCommentChange = { /* Handle comment change */ },
                    onSendComment = { /* Handle send comment */ }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Nama Pengguna dan Waktu Postingan
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nama Pengguna 1",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Waktu Postingan",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Tanaman
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Nama Tanaman:", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Text("Nama Tanaman", color = Color.Black, fontSize = 15.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Kandungan Tanaman:", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Kandungan Tanaman", color = Color.Black, fontSize = 15.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Manfaat Tanaman:", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Manfaat Tanaman", color = Color.Black, fontSize = 15.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cara Pengolahan:", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Cara pengolahan tanaman", color = Color.Black, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gambar Tanaman
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(R.drawable.jahe),
                        contentDescription = "PlantResponse Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Average Rating: 0.0", color = Color.Black, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Beri Rating:", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { /* Handle rating */ }) {
                                Icon(
                                    painterResource(com.example.myapplication.R.drawable.baseline_star_border_24),
                                    contentDescription = "Star Rating",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Review Pengguna
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Review Pengguna Lainnya:", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Nama Pengguna", color = Color.Black, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Komentar Pengguna", color = Color.Black, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Waktu Komentar", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CommentInputSection(
        comment: String,
        onCommentChange: (String) -> Unit,
        onSendComment: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorResource(id =R.color.soft_green),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { newValue ->
                        onCommentChange(newValue)
                    },
                    placeholder = { Text("Tambahkan Komentar Anda...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.soft_green),
                        unfocusedContainerColor = colorResource(id = R.color.soft_green),
                        disabledContainerColor = colorResource(id = R.color.soft_green)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSendComment() }),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent)
                )
                IconButton(onClick = onSendComment) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Comment")
                }
            }
        }
    }
}