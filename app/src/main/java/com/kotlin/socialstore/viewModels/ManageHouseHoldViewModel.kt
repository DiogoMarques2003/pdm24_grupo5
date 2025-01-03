package com.kotlin.socialstore.viewModels

import android.util.Log
import com.google.firebase.storage.FirebaseStorage

class ManageHouseHoldViewModel {

    fun fetchProfilePicture(userId: String, onComplete: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val profilePicPath = "profileImages/$userId.jpg" // Caminho no Storage
        val imageRef = storageRef.child(profilePicPath)

        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onComplete(uri.toString()) // Retorna o URL de download
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Erro ao obter URL da imagem", exception)
                onComplete(null) // Retorna null se falhar
            }
    }

}