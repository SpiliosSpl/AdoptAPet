package com.spilios.adoptapet

//import android.provider.ContactsContract.CommonDataKinds.Email

data class Pet(
    val id: Int,
    val name: String,
    val age: Int,
    val breed: String,
    val description: String,
    val sex: Boolean,
    val imageResId: Int,
    val health : String
)

/*data class OwnerParam(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: Email,
    val city: String,
)*/