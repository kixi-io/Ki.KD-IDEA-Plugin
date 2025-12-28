package io.kixi.kd.intellij

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * Icons used by the Ki Data (KD) plugin.
 */
object KDIcons {

    /**
     * The icon for KD files, displayed in the project tree and editor tabs.
     */
    @JvmField
    val FILE: Icon = IconLoader.getIcon("/icons/kd_file.svg", KDIcons::class.java)
}
