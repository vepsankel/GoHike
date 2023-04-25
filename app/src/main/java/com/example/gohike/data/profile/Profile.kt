package com.example.gohike.data.profile

import java.sql.Timestamp

class Profile {
    var uid : String? = null

    var firstName : String? = null
    var lastName : String? = null
    var birthDate : String? = null
    var phoneNumber : String? = null
    var gender : String? = null

    fun fromMutableMap(hashMap: MutableMap<String, Any>?) {
        if (hashMap == null)
            return

        firstName = hashMap["firstName"] as String?
        lastName = hashMap["lastName"] as String?
        birthDate = hashMap["birthDate"] as String?
        phoneNumber = hashMap["phoneNumber"] as String?
        gender = hashMap["gender"] as String?
    }

    fun toMutableMap(): HashMap<String, Any> {
        var map = hashMapOf<String, Any>()

        val firstName = firstName
        val lastName = lastName
        val birthDate = birthDate
        val phoneNumber = phoneNumber
        val gender = gender

        firstName?.let { map["firstName"] = firstName }
        lastName?.let { map["lastName"] = lastName }
        phoneNumber?.let { map["phoneNumber"] = phoneNumber }
        birthDate?.let { map["birthDate"] = birthDate }
        gender?.let { map["gender"] = gender }

        return map
    }
    override fun toString(): String {
        return "Profile(uid='$uid', firstName='$firstName', lastName='$lastName', birthDate=$birthDate, phoneNumber='$phoneNumber')"
    }
}