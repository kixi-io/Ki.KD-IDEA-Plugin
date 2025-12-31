package io.kixi.kd.intellij

import com.intellij.psi.tree.IElementType

/**
 * Element type for KD language tokens.
 */
class KDElementType(debugName: String) : IElementType(debugName, KDLanguage)
