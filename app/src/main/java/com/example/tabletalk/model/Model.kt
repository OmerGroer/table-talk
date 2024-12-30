package com.example.tabletalk.model

class Model {

    companion object {
        val shared = Model()
    }

    interface Listener<T> {
        fun onComplete(data: T)
    }

    fun getAllPosts(): List<Post> {
        return listOf(
            Post(
                "1",
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
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
            )
        )
    }

    fun getPostById(id: String): Post {
        return Post(
            "1",
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
                "",
                "Great",
                "1",
                null
            ),
            Comment(
                "2",
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

    fun getPostsByUserName(userName: String): List<Post> {
        return listOf(
            Post(
                "1",
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
            "",
            null
        )
    }

    fun updateUserProfile(user: User, listener: Listener<User>) {
        listener.onComplete(user)
    }

    fun getPostsByRestaurantName(restaurantName: String): List<Post> {
        return listOf(
            Post(
                "1",
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
                "",
                "Mcdonalds",
                "Great",
                "",
                5,
                null
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

    fun logOut() {
    }
}