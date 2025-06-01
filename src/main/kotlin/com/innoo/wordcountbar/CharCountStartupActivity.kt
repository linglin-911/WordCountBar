package com.innoo.wordcountbar

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.WindowManager

class CharCountStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        val widget = CharCountWidget(project)
        val statusBar = WindowManager.getInstance().getStatusBar(project)
        // 用 Widget 自己作为 Disposable 传入
        statusBar?.addWidget(widget, "after Position", widget)
    }
}
