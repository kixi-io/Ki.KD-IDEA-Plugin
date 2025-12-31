package io.kixi.kd.intellij

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Factory for creating KD syntax highlighters.
 *
 * This factory is registered in plugin.xml and is called by IntelliJ
 * when a KD file is opened to provide syntax highlighting.
 */
class KDSyntaxHighlighterFactory : SyntaxHighlighterFactory() {

    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return KDSyntaxHighlighter()
    }
}
