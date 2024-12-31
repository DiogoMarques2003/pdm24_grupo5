package com.kotlin.socialstore.data

import com.kotlin.socialstore.R

object DataConstants {
    object AccountType {
        val admin = "ADM"
        val benefiaryy = "BEN"
        val volunteer = "VOL"
    }

    object FirebaseCollections {
        val users = "users"
        val category = "category"
        val stock = "stock"
        val stores = "stores"
    }

    val mapProductCondition = mapOf("N" to R.string.product_state_n,
                                    "B" to R.string.product_state_b,
                                    "U" to R.string.product_state_u,
                                    "G" to R.string.product_state_g)
}