package com.example.tabletalk.fragments.postForm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.Post
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.repositories.PostRepository
import com.example.tabletalk.data.repositories.RestaurantRepository
import com.example.tabletalk.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostFormViewModel : ViewModel() {
    val review = MutableLiveData("")
    val rating = MutableLiveData(0.0F)
    val imageUri = MutableLiveData("")
    val restaurantName = MutableLiveData("")

    var postId: String? = null
    var restaurantId: String = ""
    var restaurant: Restaurant? = null
    var oldRating: Int = 0

    val isReviewValid = MutableLiveData(true)
    val isRatingValid = MutableLiveData(true)
    val isImageUriValid = MutableLiveData(true)

    val isLoading = MutableLiveData(false)

    val isFormValid: Boolean get() = isReviewValid.value == true && isImageUriValid.value == true && isRatingValid.value == true

    fun initForm(postId: String) {
        isLoading.value = true
        this.postId = postId

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val post = PostRepository.getInstance().getById(postId) ?: throw Exception("Post not found")
                val restaurant = RestaurantRepository.getInstance().getById(post.restaurantId)?.value ?: throw Exception("Restaurant not found")
                restaurantId = post.restaurantId
                withContext(Dispatchers.Main) {
                    restaurantName.value = restaurant.name
                    review.value = post.review
                    rating.value = post.rating.toFloat()
                    oldRating = post.rating
                    imageUri.value = post.restaurantUrl
                }
            } catch (e: Exception) {
                Log.e("AddNewReview", "Error fetching post", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    fun initForm(restaurant: Restaurant) {
        this.restaurant = restaurant
        restaurantId = restaurant.id

        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
                val post = PostRepository.getInstance().getByRestaurantIdAndUserId(restaurantId, userId)

                if (post != null) {
                    postId = post.id
                    withContext(Dispatchers.Main) {
                        restaurantName.value = restaurant.name
                        review.value = post.review
                        rating.value = post.rating.toFloat()
                        oldRating = post.rating
                        imageUri.value = post.restaurantUrl
                    }
                }
            } catch (e: Exception) {
                Log.e("AddNewReview", "Error fetching post", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    fun submit(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val post = getPost()
                    PostRepository.getInstance().save(post)

                    if (postId != null) {
                        RestaurantRepository.getInstance().save(restaurantId, post.rating, oldRating)
                    } else {
                        val restaurant = restaurant
                        if (restaurant != null) {
                            RestaurantRepository.getInstance().save(restaurant, post.rating)
                        }
                    }

                    withContext(Dispatchers.Main) { onSuccess() }
                } catch (e: Exception) {
                    Log.e("Add Post", "Error adding post", e)
                    withContext(Dispatchers.Main) { onFailure(e) }
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        } catch (e: Exception) {
            Log.e("Add Post", "Error adding post", e)
            onFailure(e)
        }
    }

    private fun validateForm() {
        isReviewValid.value = review.value?.isNotEmpty() == true
        isRatingValid.value = rating.value != 0.0F
        isImageUriValid.value = imageUri.value?.isNotEmpty() == true
    }

    private fun getPost(): Post {
        val userId = UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
        val review = review.value ?: throw Exception("Review is required")
        val rating = rating.value ?: throw Exception("Rating is required")
        val imageUri = imageUri.value ?: throw Exception("Image is required")

        return Post(
            id = postId ?: "",
            userId = userId,
            restaurantId = restaurantId,
            review = review,
            rating = rating.toInt(),
            restaurantUrl = imageUri.let {
                if (!it.startsWith("file:///")) "file://$it" else it
            }
        )
    }
}