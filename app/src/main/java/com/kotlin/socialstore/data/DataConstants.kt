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
        val users = "users"
        val category = "category"
        val stock = "stock"
        val stores = "stores"
    }

    object FirebaseImageFolders {
        val users = "profileImages"
        val stock = "stockImages"
        val donations = "donations"
    }

    val mapProductCondition = mapOf("N" to R.string.product_state_n,
                                    "B" to R.string.product_state_b,
                                    "U" to R.string.product_state_u,
                                    "G" to R.string.product_state_g)

    val mapDonationStatus = mapOf("APR" to R.string.donation_approved,
                                  "PND" to R.string.donation_pending,
                                  "DEC" to R.string.donation_declined,
                                  "DON" to R.string.donation_done)

    val mapDonationStatusBackgroundColor = mapOf("APR" to Color(0xFFECFAF2),
                                                 "PND" to Color(0xFFFFF6E6),
                                                 "DEC" to Color(0xFFFFF5F5),
                                                 "DON" to Color(0xFFECFAF2))

    val mapDonationStatusTextColor = mapOf("APR" to Color(0xFF41C980),
                                           "PND" to Color(0xFFFFA800),
                                           "DEC" to Color(0xFFEB5757),
                                           "DON" to Color(0xFF41C980))

    val mapDonationStatusIcon = mapOf("APR" to Icons.Filled.Done,
                                      "PND" to Icons.Filled.AccessTime,
                                      "DEC" to Icons.Filled.Close,
                                      "DON" to Icons.Filled.DoneAll)
}