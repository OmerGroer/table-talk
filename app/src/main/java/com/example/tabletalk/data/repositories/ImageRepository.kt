package com.example.tabletalk.data.repositories

import android.net.Uri
import com.bumptech.glide.Glide
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Image
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ImageRepository {
    companion object {
        const val IMAGES_REF = "images"
        private val imageRepository = ImageRepository()

        fun getInstance(): ImageRepository {
            return imageRepository
        }
    }

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun uploadImage(imageUri: Uri, imageId: String) {
        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")
        imageRef.putFile(imageUri).await()

        AppLocalDb.getInstance().imageDao().insertAll(Image(imageId, imageUri.toString()))
    }

    suspend fun getImageRemoteUri(imageId: String): Uri {
        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")

        return imageRef.downloadUrl.await()
    }

    fun downloadAndCacheImage(uri: Uri, imageId: String): String {
        val file = Glide.with(MyApplication.Globals.context!!)
            .asFile()
            .load(uri)
            .submit()
            .get()

        AppLocalDb.getInstance().imageDao().insertAll(Image(imageId, file.absolutePath))

        return file.absolutePath
    }

    fun getImageLocalUri(imageId: String): String {
        return AppLocalDb.getInstance().imageDao().getImageById(imageId).value?.uri ?: ""
    }

    suspend fun getImagePathById(imageId: String): String {
        val image = AppLocalDb.getInstance().imageDao().getImageById(imageId).value

        if (image != null) return image.uri

        val remoteUri = getImageRemoteUri(imageId)
        val localPath = downloadAndCacheImage(remoteUri, imageId)

        AppLocalDb.getInstance().imageDao().insertAll(Image(imageId, localPath))

        return localPath
    }

    suspend fun deleteImage(imageId: String) {
        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")
        imageRef.delete().await()

        deleteLocalImage(imageId)
    }

    private fun deleteLocalImage(imageId: String) {
        val image = AppLocalDb.getInstance().imageDao().getImageById(imageId).value
        image?.let {
            val file = Glide.with(MyApplication.Globals.context!!)
                .asFile()
                .load(it.uri)
                .submit()
                .get()

            if (file.exists()) {
                file.delete()
            }

            AppLocalDb.getInstance().imageDao().deleteImage(imageId)
        }
    }
}