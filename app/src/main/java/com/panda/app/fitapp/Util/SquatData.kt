package com.panda.app.fitapp.Util

class SQuatData {
    data class squatLevel(
        val sportType: String,
        val userLevel : Int,
        val rep: List<Int>)


    companion object{
        val squatLevels: MutableList<squatLevel> = mutableListOf(
            squatLevel(
                sportType = "Squat",
                userLevel = 6,
                rep = listOf(2, 3, 2, 2)
            ),
            squatLevel(
                sportType = "Squat",
                userLevel = 10,
                rep = listOf(5, 4, 4, 3)
            ),
            squatLevel(
                sportType = "Squat",
                userLevel = 20,
                rep = listOf(12, 10, 8, 10)
            ),
            squatLevel(
                sportType = "Squat",
                userLevel = 25,
                rep = listOf(14, 16, 12, 14)
            ),
            squatLevel(
                sportType = "Squat",
                userLevel = 35,
                rep = listOf(18, 14, 16, 13)
            ),
            squatLevel(
                sportType = "Squat",
                userLevel = 45,
                rep = listOf(30, 25, 20, 22)
            ),
            squatLevel(
                sportType = "Squat",
                userLevel = 55,
                rep = listOf(35, 28, 22, 24)
            ),
            squatLevel(
                sportType = "Squat",
                userLevel = 65,
                rep = listOf(40, 30, 24, 27)
            )
        )
    }


}