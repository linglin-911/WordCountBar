package com.innoo.wordcountbar

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.Disposable

// 让 CharCountWidget 实现 Disposable
class CharCountWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation, Disposable {
    private var text: String = "字符数: 0"
    private var statusBar: StatusBar? = null
    private val disposable = Disposer.newDisposable()

    override fun ID(): String = "CharCountWidget"

    override fun getText(): String = text

    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        updateCount()
        // 监听文件切换
        FileEditorManager.getInstance(project)
            .addFileEditorManagerListener(object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    updateCount()
                }
            })
        // 监听内容变化
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(
            object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    updateCount()
                }
            },
            disposable
        )
    }

    private fun updateCount() {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        val textContent = editor?.document?.text ?: ""
        // 只统计非空白字符（即 \S 匹配的）
        val count = Regex("\\S").findAll(textContent).count()
        text = "字数: $count"
        statusBar?.updateWidget(ID())
    }

    override fun dispose() {
        Disposer.dispose(disposable)
    }

    override fun getTooltipText(): String = "当前文件的字数（不含空白字符）"
    override fun getClickConsumer(): Consumer<MouseEvent>? = null
    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

}
