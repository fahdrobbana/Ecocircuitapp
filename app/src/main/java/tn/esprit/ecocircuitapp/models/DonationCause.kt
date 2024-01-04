package tn.esprit.ecocircuitapp.models

data class DonationCause(
    val success: Boolean,
    val message: String?,
    val data: List<DonationCause>,
    val title: String,
    val description: String,
    val image: String,
    val amount: Float,
    val createdAt: Long?
)
