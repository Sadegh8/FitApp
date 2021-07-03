package com.panda.app.fitapp.Util

class SitData {
    data class sitLevel(
        val sportType: String,
        val userLevel : Int,
        val rep: List<Int>)


    companion object{
        val sitLevels: MutableList<sitLevel> = mutableListOf(
            sitLevel(
                sportType = "sitUps",
                userLevel = 6,
                rep = listOf(5, 4, 3, 4)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 10,
                rep = listOf(8, 6, 8, 5)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 20,
                rep = listOf(15, 12, 10, 12)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 25,
                rep = listOf(20, 17, 15, 18)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 35,
                rep = listOf(25, 22, 20, 23)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 50,
                rep = listOf(35, 30, 25, 22)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 60,
                rep = listOf(45, 30, 25, 25)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 70,
                rep = listOf(50, 35, 40, 33)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 80,
                rep = listOf(65, 45, 35, 50)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 90,
                rep = listOf(75, 50, 40, 53)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 100,
                rep = listOf(85, 60, 45, 58)
            ),
            sitLevel(
                sportType = "sitUps",
                userLevel = 120,
                rep = listOf(100, 65, 45, 60)
            )
        )
    }


}