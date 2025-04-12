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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R // Pastikan import R Anda benar

@Composable
fun Home(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    // Sample data
    val samplePlants = listOf(
        Plant("1", "Jahe", 4.5),
        Plant("2", "Kunyit", 4.2),
        Plant("3", "Temulawak", 4.0)
    )

    var filteredPlants by remember { mutableStateOf(samplePlants) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF4E9))
            .padding(16.dp)
    ) {
        // HEADER WITH LOGO
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

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                filteredPlants = samplePlants.filter { plant ->
                    plant.name.contains(it, ignoreCase = true)
                }
            },
            placeholder = { Text("Cari Nama Tanaman...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {})
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PLANT LIST
        LazyColumn {
            items(filteredPlants) { plant ->
                PlantCard(plant, navController)
            }
        }
    }
}

@Composable
fun PlantCard(plant: Plant, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
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
                    color = Color(0xFF498553),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                StarRating(rating = plant.rating)
            }

            TextButton(onClick = { /* navController.navigate("detail/${plant.id}") */ }) {
                Text("Detail", color = Color(0xFF498553))
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

data class Plant(
    val id: String,
    val name: String,
    val rating: Double
)