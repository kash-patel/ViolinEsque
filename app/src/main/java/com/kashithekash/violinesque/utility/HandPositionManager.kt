package com.kashithekash.violinesque.utility

class HandPositionManager {

    private var pitchCentre: Float = -45f
    private var totalRange: Float = -90f
    private var normalizedPitch: Float = 0f
    private var normalizedHandPositionRange: Float = 0f
    private var index: Int = 0

    fun calculateCurrentHandPositionIndex (pitch: Float) : Int {

        if (Config.handPositionsList.count() == 1) return 0

        // Default: (0 + -90) / 2 = -45
        pitchCentre = Config.pitchCentre
        // Default: -90 - 0 = -90
        totalRange = Config.totalPitchRange

        // Pitch with respect to center as a fraction of total range
        normalizedPitch = (if (Config.invertPitch) -1 else 1) * ((pitch - pitchCentre) / totalRange).coerceIn(-0.5f, 0.5f)

        // Pitch range per hand position as a fraction of total range; only if number of hand positions exceeds one
        normalizedHandPositionRange = 1f / (Config.handPositionsList.count() - 1)

        index = ((0.5f + normalizedPitch) / normalizedHandPositionRange).toInt()

        return index
    }
}