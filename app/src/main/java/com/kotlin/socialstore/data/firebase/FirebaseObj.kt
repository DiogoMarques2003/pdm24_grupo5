package com.kotlin.socialstore.data.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await


object FirebaseObj {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firestorage: FirebaseStorage
    private lateinit var storagerefence: StorageReference

    fun startFirebase() {
        auth = Firebase.auth
        firestore = Firebase.firestore
        firestorage = FirebaseStorage.getInstance()
        storagerefence = firestorage.reference
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun createAccount(email: String, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid // Devolver o id do user
        } catch (e: Exception) {
            Log.w(TAG, "createUserWithEmail:failure", e)
            null // Retorna null caso não consiga criar conta
        }
    }

    suspend fun loginAccount(email: String, password: String): String? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: Exception) {
            Log.w(TAG, "signInWithEmail:failure", e)
            null // Retorna null caso não consiga fazer login
        }
    }

    fun sendPasswordResetEmail(email: String, baseContext: Context) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email enviado")
                    Toast.makeText(
                        baseContext,
                        "Email enviado",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Falha ao enviar email",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    // Método para inserir dados
    suspend fun insertData(
        collection: String,
        data: Map<String, Any?>,
        documentId: String? = null
    ): String? {
        return try {
            val documentReference = if (documentId != null) {
                firestore.collection(collection).document(documentId).set(data).await()
                firestore.collection(collection)
                    .document(documentId) // Retorna a referência do documento criado com ID fornecido
            } else {
                val result = firestore.collection(collection).add(data).await()
                result // Retorna a referência do documento criado com ID automático
            }
            Log.d(TAG, "Documento inserido com sucesso, ID: ${documentReference.id}")
            documentReference.id // Retorna o ID do documento
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao inserir documento", e)
            null // Retorna null em caso de erro
        }
    }

    // Método para obter dados
    suspend fun getData(collection: String, documentId: String? = null): List<Map<String, Any>>? {
        return try {
            if (documentId != null) {
                // Obter um único documento e retornar como um Map
                val snapshot = firestore.collection(collection).document(documentId).get().await()
                if (snapshot.exists()) {
                    val data = snapshot.data ?: emptyMap()
                    listOf(data + ("id" to snapshot.id)) // Adicionar o id ao objeto
                } else {
                    Log.w(TAG, "Documento não encontrado")
                    null
                }
            } else {
                // Obter todos os documentos da coleção e retornar como uma lista de Maps
                val snapshot = firestore.collection(collection).get().await()
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    data + ("id" to doc.id) // Adiciona o id ao objeto
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao obter dados", e)
            null
        }
    }

    fun listenToData(
        collection: String,
        documentId: String? = null,
        onDataChanged: (List<Map<String, Any>>?) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return if (documentId != null) {
            // Listener para um único documento
            firestore.collection(collection).document(documentId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed for document.", e)
                        onError(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val data = snapshot.data ?: emptyMap()
                        onDataChanged(listOf(data + ("id" to snapshot.id))) // Retorna o documento como uma lista
                    } else {
                        Log.d(TAG, "Document data: null")
                        onDataChanged(null) // Documento não encontrado
                    }
                }
        } else {
            // Listener para todos os documentos de uma coleção
            firestore.collection(collection)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed for collection.", e)
                        onError(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val documents = snapshot.documents.mapNotNull { doc ->
                            val data = doc.data ?: return@mapNotNull null
                            Log.d(TAG, "Documento - id: ${doc.id} - ${doc.data}")
                            data + ("id" to doc.id) // Adiciona o id a cada documento
                        }
                        onDataChanged(documents) // Retorna todos os documentos
                    } else {
                        Log.d(TAG, "Collection data: null")
                        onDataChanged(null) // Nenhum dado encontrado
                    }
                }
        }
    }

    // Método para atualizar dados
    suspend fun updateData(
        collection: String,
        documentId: String,
        updatedData: Map<String, Any?>
    ): Boolean {
        return try {
            firestore.collection(collection).document(documentId).update(updatedData).await()
            Log.d(TAG, "Documento atualizado com sucesso")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao atualizar documento", e)
            false
        }
    }

    // Método para apagar dados
    suspend fun deleteData(collection: String, documentId: String): Boolean {
        return try {
            firestore.collection(collection).document(documentId).delete().await()
            Log.d(TAG, "Documento deletado com sucesso")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao deletar documento", e)
            false
        }
    }

    fun downloadImage(path: String) {
        // Create a reference with an initial file path and name and Download image
        var imageRef = storagerefence.child(path)

        var gsUrl = "gs://${firestorage.app.options.storageBucket}/${storagerefence.path}"

        Log.d(TAG,"Path downloadUrl: $imageRef")

        // Create a reference to a file from a Google Cloud Storage URI
    }
}