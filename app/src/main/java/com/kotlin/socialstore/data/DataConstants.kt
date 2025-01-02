package com.kotlin.socialstore.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.ui.graphics.Color
import com.kotlin.socialstore.R

object DataConstants {
    object AccountType {
        val admin = "ADM"
        val benefiaryy = "BEN"
        val volunteer = "VOL"
    }

    object FirebaseCollections {
        val donations = "donations"
        val donationsItems = "donationsItems"
        val donationSchedule = "donationSchedule"
        val users = "users"
        val category = "category"
        val stock = "stock"
        val stores = "stores"
        val familyHouseholdVisits = "familyHouseholdVisits"
        val takenItems = "takenItems"
    }

    object FirebaseImageFolders {
        val users = "profileImages"
        val stock = "stockImages"
        val donations = "donations"
    }

    val mapProductCondition = mapOf(
        "N" to R.string.product_state_n,
        "B" to R.string.product_state_b,
        "U" to R.string.product_state_u,
        "G" to R.string.product_state_g
    )

    object DashboardOptions {
        val beneficiariesPieChart = "Beneficiaries nationalities"
        val donationsBarChart = "All-time Donations"
    }

    val DashboardOptionsList = listOf(
        DataConstants.DashboardOptions.beneficiariesPieChart,
        DataConstants.DashboardOptions.donationsBarChart
    )

    val donationInitialStatus = "PND"
    val donationAppprovedStatuus = "APR"
    val donationDeclinedStatus = "DEC"
    val donationDoneStatus = "DON"

    val mapDonationStatus = mapOf(
        donationAppprovedStatuus to R.string.donation_approved,
        donationInitialStatus to R.string.donation_pending,
        donationDeclinedStatus to R.string.donation_declined,
        donationDoneStatus to R.string.donation_done
    )

    val mapDonationStatusBackgroundColor = mapOf(
        donationAppprovedStatuus to Color(0xFFECFAF2),
        donationInitialStatus to Color(0xFFFFF6E6),
        donationDeclinedStatus to Color(0xFFFFF5F5),
        donationDoneStatus to Color(0xFFECFAF2)
    )

    val mapDonationStatusTextColor = mapOf(
        donationAppprovedStatuus to Color(0xFF41C980),
        donationInitialStatus to Color(0xFFFFA800),
        donationDeclinedStatus to Color(0xFFEB5757),
        donationDoneStatus to Color(0xFF41C980)
    )

    val mapDonationStatusIcon = mapOf(
        donationAppprovedStatuus to Icons.Filled.Done,
        donationInitialStatus to Icons.Filled.AccessTime,
        donationDeclinedStatus to Icons.Filled.Close,
        donationDoneStatus to Icons.Filled.DoneAll
    )
}