package com.capstone.sweettrack.data.remote.response

data class HistoryResponse(
    val statusCode: Int,
    val error: Boolean,
    val message: String,
    val data: List<History>
)

data class History(
    val id_hasil: Int,
    val user_id: Int,
    val nama_makanan: String,
    val gula: Double,
    val protein: Double,
    val lemak: Double,
    val gambar_analisa_makanan: String,
    val kalori: Double,
)