package com.example.sg_safety_mobile.Logic


import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 *Firebase Object
 *
 * @since 2022-4-15
 */
object FirebaseObject {

    /**
     *Manager of the Firebase Firestore
     */
    val db = Firebase.firestore

}