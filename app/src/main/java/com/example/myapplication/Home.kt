package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.PlantResponse
import com.example.myapplication.PlantViewModel
import com.example.myapplication.R

@Composable
fun Home(
    navController: NavController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    // States from ViewModel
    val plantList by viewModel.plantList
    val currentPage by viewModel.currentPage
    val totalPlants by viewModel.totalPlants
    val pageSize = 10
    val totalPages = if (totalPlants == 0) 1 else (totalPlants + pageSize - 1) / pageSize

    var searchQuery by remember { mutableStateOf("") }
    val filteredPlants = remember(plantList, searchQuery) {
        if (searchQuery.isBlank()) plantList
        else plantList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Text state for manual page input
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    val focusManager = LocalFocusManager.current

    // Fetch first page on entry
    LaunchedEffect(Unit) {
        viewModel.fetchPlantsByPage(1)
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

        // Pagination Controls with manual input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = pageInput,
                onValueChange = { pageInput = it },
                label = { Text("Page") },
                singleLine = true,
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
                modifier = Modifier.width(100.dp)
            )
            Text(
                text = "of $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val page = pageInput.toIntOrNull() ?: 1
                    viewModel.fetchPlantsByPage(page.coerceIn(1, totalPages))
                }
            ) {
                Text("Go")
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari Nama Tanaman...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Plant List
        LazyColumn {
            items(filteredPlants) { plant ->
                PlantCard(plant, navController)
            }
        }
    }
}

@Composable
fun PlantCard(plant: PlantResponse, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .height(90.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Park,
                contentDescription = "Plant Icon",
                tint = Color(0xFF498553),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name,
                    color = colorResource(id = R.color.dark_green),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                val rating = plant.ratingCount.toIntOrNull()?.let { count ->
                    val total = plant.ratingTotal.toDoubleOrNull() ?: 0.0
                    if (count > 0) total / count else 0.0
                } ?: 0.0
                StarRating(rating = rating)
            }
            TextButton(onClick = {
                navController.navigate("detail/${plant.id}")
            }) {
                Text("Detail", color = colorResource(id = R.color.dark_green))
            }
        }
    }
}

@Composable
fun StarRating(rating: Double) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating.toInt()) Color(0xFFFFD700) else Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
