package com.kotlin.socialstore.data

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
}