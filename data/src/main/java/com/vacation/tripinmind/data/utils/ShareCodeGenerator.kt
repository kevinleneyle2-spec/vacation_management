package com.vacation.tripinmind.data.utils

import java.security.SecureRandom

object ShareCodeGenerator {
    private val secureRandom = SecureRandom()
    private const val CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    fun generate(groups: Int = 4, charsPerGroup: Int = 3): String {
        return (1..groups).joinToString("-") {
            buildString(charsPerGroup) {
                repeat(charsPerGroup) {
                    append(CHARS[secureRandom.nextInt(CHARS.length)])
                }
            }
        }
    }
}