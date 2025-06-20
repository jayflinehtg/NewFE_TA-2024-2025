package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.PlantResponse
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    val ratedPlantList by viewModel.ratedPlantList
    val currentPage by viewModel.currentPage
    val totalPlants by viewModel.totalPlants
    val pageSize = 10
    val totalPages = if (totalPlants == 0) 1 else (totalPlants + pageSize - 1) / pageSize

    var searchQuery by remember { mutableStateOf("") }
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.fetchPlantsByPage(currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF4E9))
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plant),
                contentDescription = "Logo Tanaman",
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 8.dp)
            )
            Column {
                Text(
                    text = "Jelajahi Informasi",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF498553)
                )
                Text(
                    text = "Tanaman Herbal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF498553)
                )
            }
        }

        // Pagination
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            // Tombol Panah Kiri
            IconButton(
                onClick = {
                    if (currentPage > 1) {
                        val newPage = currentPage - 1
                        pageInput = newPage.toString()
                        viewModel.fetchPlantsByPage(newPage)
                    }
                },
                enabled = currentPage > 1
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Halaman Sebelumnya",
                    tint = if (currentPage > 1) Color(0xFF4CAF50) else Color.Gray)
            }
            OutlinedTextField(
                value = pageInput,
                onValueChange = { pageInput = it },
                label = { Text("Page", color = Color.Black) },
                singleLine = true,
                textStyle = TextStyle(color = Color.Black),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    val page = pageInput.toIntOrNull() ?: 1
                    val validPage = page.coerceIn(1, totalPages)
                    pageInput = validPage.toString()
                    viewModel.fetchPlantsByPage(validPage)
                }),
                modifier = Modifier.width(80.dp)
            )
            Text(
                text = "of $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp),
                color = Color.Black
            )
            // Tombol Panah Kanan
            IconButton(
                onClick = {
                    if (currentPage < totalPages) {
                        val newPage = currentPage + 1
                        pageInput = newPage.toString()
                        viewModel.fetchPlantsByPage(newPage)
                    }
                },
                enabled = currentPage < totalPages
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Halaman Berikutnya",
                    tint = if (currentPage < totalPages) Color(0xFF4CAF50) else Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val page = pageInput.toIntOrNull() ?: 1
                    viewModel.fetchPlantsByPage(page.coerceIn(1, totalPages))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Go", color = Color.White, fontSize = 14.sp)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari Info Tanaman", color = Color.Gray) },
            textStyle = TextStyle(color = Color.Black),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
                cursorColor = Color.Black,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    if (searchQuery.isBlank()) {
                        viewModel.fetchPlantsByPage(1)
                    } else {
                        viewModel.searchPlants(
                            name = searchQuery,
                            namaLatin = searchQuery,
                            komposisi = searchQuery,
                            kegunaan = searchQuery
                        )
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (ratedPlantList.isEmpty()) {
            Text(
                "Tanaman tidak ditemukan.",
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn {
                items(ratedPlantList) { ratedPlant ->
                    PlantCard(ratedPlant.plant, ratedPlant.averageRating, ratedPlant.plant.likeCount.toInt(), navController)
                }
            }
        }
    }
}

@Composable
fun PlantCard(plant: PlantResponse, averageRating: Double, likesCount: Int, navController: NavController) {
    val viewModel: PlantViewModel = hiltViewModel()
    var actualRating by remember { mutableStateOf(averageRating) }

    // Ambil rating dari API
    LaunchedEffect(plant.id) {
        try {
            val ratingResponse = viewModel.apiServiceInstance.getAverageRating(plant.id)
            val freshRating = ratingResponse.averageRating
            actualRating = freshRating

            // Debug logging
            Log.d("CardRating", "Plant: ${plant.name}, Fresh rating: $freshRating")
        } catch (e: Exception) {
            Log.e("CardRating", "Error fetching fresh rating: ${e.message}")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name,
                    color = Color(0xFF2E7D32),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Row untuk Rating dan Likes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarRating(rating = actualRating)
                    Spacer(modifier = Modifier.width(16.dp))
                    LikesDisplay(likesCount = likesCount)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.navigate("detail/${plant.id}") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Detail", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun StarRating(rating: Double) {
    val validRating = rating.takeIf { !it.isNaN() && !it.isInfinite() } ?: 0.0

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Box {
                // Bintang kosong
                Icon(
                    imageVector = Icons.Default.StarBorder,
                    contentDescription = "Star Rating Background",
                    tint = Color(0xFFE0E0E0),
                    modifier = Modifier.size(20.dp)
                )

                // Bintang terisi
                val fillPercentage = when {
                    validRating >= index + 1 -> 1f // Penuh
                    validRating > index -> (validRating - index).toFloat() // Sebagian
                    else -> 0f // Kosong
                }

                if (fillPercentage > 0) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Rating Filled",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .size(20.dp)
                            .clipToBounds()
                            .drawWithContent {
                                clipRect(right = size.width * fillPercentage) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "(${String.format("%.1f", validRating)})",
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun LikesDisplay(likesCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Likes Count",
            tint = Color(0xFFE53935),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = likesCount.toString(),
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}