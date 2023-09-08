package com.adikul.hrmaa


data class SignInResult(
    val data: UserData?,
    val errMsg: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePicUrl: String?
)
