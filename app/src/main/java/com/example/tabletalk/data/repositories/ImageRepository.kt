package com.example.tabletalk.data.repositories

import android.net.Uri
import com.bumptech.glide.Glide
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Image
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ImageRepository(private val folder: String) {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun upload(imageUri: Uri, imageId: String) {
        val imageRef = storage.reference.child("$folder/$imageId")
        imageRef.putFile(imageUri).await()

        AppLocalDb.getInstance().imageDao().insertAll(Image(imageId, imageUri.toString()))
    }

    suspend fun getImageRemoteUri(imageId: String): Uri {
        val imageRef = storage.reference.child("$folder/$imageId")

        return imageRef.downloadUrl.await()
    }

    fun downloadAndCacheImage(uri: Uri, imageId: String): String {
        val file = Glide.with(MyApplication.context)
            .asFile()
            .load(uri)
            .submit()
            .get()

        AppLocalDb.getInstance().imageDao().insertAll(Image(imageId, file.absolutePath))

        return file.absolutePath
    }

    fun getImageLocalUri(imageId: String): String {
        return AppLocalDb.getInstance().imageDao().getById(imageId).value?.uri ?: ""
    }

    suspend fun getImagePathById(imageId: String): String {
        val image = AppLocalDb.getInstance().imageDao().getById(imageId).value

        if (image != null) return image.uri

        val remoteUri = getImageRemoteUri(imageId)
        val localPath = downloadAndCacheImage(remoteUri, imageId)

        AppLocalDb.getInstance().imageDao().insertAll(Image(imageId, localPath))

        return localPath
    }

    suspend fun deleteImage(imageId: String) {
        val imageRef = storage.reference.child("$folder/$imageId")
        imageRef.delete().await()

        deleteLocalImage(imageId)
    }

    private fun deleteLocalImage(imageId: String) {
        val image = AppLocalDb.getInstance().imageDao().getById(imageId).value
        image?.let {
            val file = Glide.with(MyApplication.context)
                .asFile()
                .load(it.uri)
                .submit()
                .get()

            if (file.exists()) {
                file.delete()
            }

            AppLocalDb.getInstance().imageDao().delete(imageId)
        }
    }
}