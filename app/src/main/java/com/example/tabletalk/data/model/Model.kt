package com.example.tabletalk.data.model

class Model {

    companion object {
        val shared = Model()
    }

    interface Listener<T> {
        fun onComplete(data: T)
    }

    fun getAllPostsWithoutUser(userId: String): List<Post> {
        return emptyList()
    }

    fun getCommentsByPostId(postId: String): List<Comment> {
        return listOf(
            Comment(
                "1",
                "Gal",
                "Gal",
                "",
                "Great",
                "1",
                null
            ),
            Comment(
                "2",
                "Gal",
                "Gal",
                "",
                "Great",
                "1",
                null
            )
        )
    }

    fun addPost(post: Post, listener: Listener<Post>) {
        listener.onComplete(post)
    }

    fun addComment(comment: Comment, listener: Listener<Comment>) {
        listener.onComplete(comment)
    }

    fun updatePost(post: Post, listener: Listener<Post>) {
        listener.onComplete(post)
    }

    fun updateComment(comment: Comment, listener: Listener<Comment>) {
        listener.onComplete(comment)
    }

    fun deletePost(post: Post, listener: Listener<Post>) {
        listener.onComplete(post)
    }

    fun deleteComment(comment: Comment, listener: Listener<Comment>) {
        listener.onComplete(comment)
    }

    fun signUp(user: User, password: String, listener: Listener<User>) {
        listener.onComplete(user)
    }

    fun logIn(user: User, password: String, listener: Listener<User>) {
        listener.onComplete(user)
    }

    fun getPostsByUserId(userId: String): List<Post> {
        return emptyList()
    }

    fun getLoggedInUser(): User {
        return User(
            "1",
            "Gal",
            "Gal",
            ""
        )
    }

    fun updateUserProfile(user: User, listener: Listener<User>) {
        listener.onComplete(user)
    }

    fun getPostsByRestaurantNameAndUserId(restaurantName: String, userId: String): List<Post> {
        return emptyList()
    }

    fun getPostsByRestaurantName(restaurantName: String): List<Post> {
        return emptyList()
    }

    fun getRestaurantById(id: String): Restaurant {
        return Restaurant(
            "1",
            "Mcdonalds",
            4.9,
            1,
            "Tel Aviv",
            address = "",
            priceTypes = "",
        )
    }

    fun getAllRestaurants(name: String): List<Restaurant> {
        return emptyList()
    }

    fun getAllUsers(name: String): MutableList<User> {
        return mutableListOf(
            User(
                "1",
                "1",
                "Gal",
                ""
            ),
            User(
                "2",
                "2",
                "Gal",
                ""
            )
        )
    }

    fun isUserNameAvailable(userName: String, listener: Listener<Boolean>) {
        listener.onComplete(true)
    }

    fun isEmailAvailable(email: String, listener: Listener<Boolean>) {
        listener.onComplete(true)
    }

    fun isLoggedIn(listener: Listener<Boolean>) {
        listener.onComplete(true)
    }

    fun logout() {
    }
}