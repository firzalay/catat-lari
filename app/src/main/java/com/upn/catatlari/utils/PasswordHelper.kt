package com.upn.catatlari.utils

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHelper {

    fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verify(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
    }
}