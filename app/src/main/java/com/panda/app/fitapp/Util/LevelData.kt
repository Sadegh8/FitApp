package com.panda.app.fitapp.Util

class LevelData {
    data class psuhLevel(
        val sportType: String,
        val userLevel : Int,
        val rep: List<Int>)


    companion object{
        val pushLevels: MutableList<psuhLevel> = mutableListOf(
            psuhLevel(
                sportType = "push",
                userLevel = 6,
                rep = listOf(2, 3, 2, 2)
            ),
            psuhLevel(
                sportType = "push",
                userLevel = 10,
                rep = listOf(5, 6, 4, 3)
            ),
            psuhLevel(
                sportType = "push",
                userLevel = 20,
                rep = listOf(10, 12, 7, 8)
            ),
            psuhLevel(
                sportType = "push",
                userLevel = 25,
                rep = listOf(12, 17, 12, 13)
            ),
            psuhLevel(
                sportType = "push",
                userLevel = 35,
                rep = listOf(14, 19, 15, 13)
            ),
            psuhLevel(
                sportType = "push",
                userLevel = 50,
                rep = listOf(25, 30, 20, 25)
            )
        )
    }


}