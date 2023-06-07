package com.kashithekash.violinesque.utility

import kotlin.math.PI

enum class ViolinString {
    G, D, A, E
}

val G_STRING_NOTES: Array<String> =
    arrayOf("G 3", "G# 3", "A 3", "A# 3", "B 3", "C 4", "C# 4", "D 4", "D# 4", "E 4", "F 4", "F# 4", "G 4", "G# 4", "A 4", "A# 4", "B 4", "C 5", "C# 5", "D 5", "D# 5", "E 5", "F 5", "F# 5", "G 5", "G# 5", "A 5", "A# 5", "B 5", "C 5")
val D_STRING_NOTES: Array<String> =
    arrayOf("D 4", "D# 4", "E 4", "F 4", "F# 4", "G 4", "G# 4", "A 4", "A# 4", "B 4", "C 5", "C# 5", "D 5", "D# 5", "E 5", "F 5", "F# 5", "G 5", "G# 5", "A 5", "A# 5", "B 5", "C 6", "C# 6", "D 6", "D# 6", "E 6", "F 6", "F# 6", "G 6")
val A_STRING_NOTES: Array<String> =
    arrayOf("A 4", "A# 4", "B 4", "C 5", "C# 5", "D 5", "D# 5", "E 5", "F 5", "F# 5", "G 5", "G# 5", "A 5", "A# 5", "B 5", "C 6", "C# 6", "D 6", "D# 6", "E 6", "F 6", "F# 6", "G 6", "G# 6", "A 6", "A# 6", "B 6", "C 6", "C# 6", "D 6")
val E_STRING_NOTES: Array<String> =
    arrayOf("E 5", "F 5", "F# 5", "G 5", "G# 5", "A 5", "A# 5", "B 5", "C 6", "C# 6", "D 6", "D# 6", "E 6", "F 6", "F# 6", "G 6", "G# 6", "A 6", "A# 6", "B 6", "C 7", "C# 7", "D 7", "D# 7", "E 7", "F 7", "F# 7", "G 7", "G# 7", "A 7")

val handPostionStartIndices: Array<Int> = arrayOf(0, 0, 1, 3, 5, 6, 8, 10, 11, 13, 15, 16, 18, 20, 21)
//val handPostionStartIndices: Array<Int> = arrayOf(0, 0, 1, 3, 5, 6, 8, 10, 11, 13, 15, 16, 18, 20, 21)
const val Pi: Float = PI.toFloat()