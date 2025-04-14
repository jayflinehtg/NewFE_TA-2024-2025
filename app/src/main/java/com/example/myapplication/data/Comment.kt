package com.example.myapplication.data

data class Comment(
    val user: String,     // Address pengguna yang memberikan komentar
    val comment: String,  // Isi komentar
    val timestamp: String // Waktu komentar diberikan
)
