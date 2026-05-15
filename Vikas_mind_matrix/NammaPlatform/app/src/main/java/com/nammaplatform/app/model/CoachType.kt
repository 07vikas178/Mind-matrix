package com.nammaplatform.app.model

/**
 * Type of railway coach. Used to render the correct colour / icon
 * on the horizontal coach strip and to highlight General / Ladies
 * coaches (a key requirement for rural passengers).
 */
enum class CoachType(val labelEn: String, val labelKn: String) {
    ENGINE("Engine",  "ಎಂಜಿನ್"),
    SLR   ("SLR",     "SLR"),         // Seating-cum-Luggage Rake (often unreserved)
    GEN   ("General", "ಸಾಮಾನ್ಯ"),
    LADIES("Ladies",  "ಮಹಿಳೆಯರ"),
    SLEEPER("Sleeper","ಸ್ಲೀಪರ್"),     // S1, S2 …
    AC3  ("3A",       "3A"),          // B1, B2 …
    AC2  ("2A",       "2A"),          // A1, A2 …
    AC1  ("1A",       "1A"),
    PANTRY("Pantry",  "ಪ್ಯಾಂಟ್ರಿ"),
    UNKNOWN("?",      "?");

    companion object {
        /**
         * Maps a raw coach code from JSON (e.g. "S1", "B2", "GEN")
         * to a CoachType. Indian Railways uses prefix conventions:
         *   S = Sleeper, B = AC-3, A = AC-2, H = AC-1.
         */
        fun fromCode(code: String): CoachType = when {
            code.equals("ENGINE", true)  -> ENGINE
            code.equals("SLR",    true)  -> SLR
            code.equals("GEN",    true)  -> GEN
            code.equals("LADIES", true)  -> LADIES
            code.equals("PANTRY", true)  -> PANTRY
            code.startsWith("S", true)   -> SLEEPER
            code.startsWith("B", true)   -> AC3
            code.startsWith("A", true)   -> AC2
            code.startsWith("H", true)   -> AC1
            else -> UNKNOWN
        }
    }
}
