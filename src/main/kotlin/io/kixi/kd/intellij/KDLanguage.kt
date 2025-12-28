package io.kixi.kd.intellij

import com.intellij.lang.Language

/**
 * Language definition for Ki Data (KD).
 *
 * KD is a modern document format for structured data with a rich type system
 * supporting strings, numbers, dates, durations, versions, quantities, and more.
 *
 * See [KD Docs](https://github.com/kixi-io/Ki.Docs/wiki/Ki-Data-(KD)) for details.
 */
object KDLanguage : Language("KD") {

    private fun readResolve(): Any = KDLanguage

    override fun getDisplayName(): String = "Ki Data"

    override fun isCaseSensitive(): Boolean = true
}
