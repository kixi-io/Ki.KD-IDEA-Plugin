package io.kixi.kd.intellij

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * File type for Ki Data (KD) files with .kd extension.
 *
 * KD files contain structured data using the Ki Data format, supporting
 * a rich type system with tags, values, attributes, and hierarchical structure.
 */
object KDFileType : LanguageFileType(KDLanguage) {

    override fun getName(): String = "KD File"

    override fun getDescription(): String = "Ki Data file"

    override fun getDefaultExtension(): String = "kd"

    override fun getIcon(): Icon = KDIcons.FILE

    private fun readResolve(): Any = KDFileType
}
