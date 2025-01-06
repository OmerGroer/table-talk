package com.example.tabletalk.data.model

class Model {

    companion object {
        val shared = Model()
    }

    interface Listener<T> {
        fun onComplete(data: T)
    }

    fun getAllPostsWithoutUser(userId: String): List<Post> {
        return listOf(
            Post(
                "1",
                "Gal5",
                "Gal",
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
            ),
            Post(
                "2",
                "Gal7",
                "Gal",
                "",
                "Mcdonalds",
                "Great",
                "",
                3,
                null
            )
        )
    }

    fun getPostById(id: String): Post {
        return Post(
            "1",
            "Gal",
            "Gal",
            "",
            "Mcdonalds",
            "Great",
            "",
            5,
            null
        )
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
        return listOf(
            Post(
                "1",
                "Gal",
                "Gal",
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
            ),
            Post(
                "2",
                "Gal",
                "Gal",
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
            )
        )
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
        return mutableListOf(
            Post(
                "1",
                "Gal",
                "Gal",
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
            )
        )
    }

    fun getPostsByRestaurantName(restaurantName: String): List<Post> {
        return mutableListOf(
            Post(
                "1",
                "Gal3",
                "Gal",
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
            ),
            Post(
                "2",
                "Gal2",
                "Gal",
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
            )
        )
    }

    fun getRestaurantByName(name: String): Restaurant {
        return Restaurant(
            1,
            "Mcdonalds",
            4.9,
            "Fast Food",
            "Tel Aviv"
        )
    }

    fun getAllRestaurants(name: String): List<Restaurant> {
        return listOf(
            Restaurant(
                1,
                "Mcdonalds",
                5.0,
                "Fast Food",
                "Tel Aviv"
            ),
            Restaurant(
                2,
                "Burgranch",
                1.0,
                "Fast Food",
                "Yavne"
            )
        )
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