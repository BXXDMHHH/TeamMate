package com.teammate.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teammate.android.data.AIAskRequest
import com.teammate.android.data.ApiClient
import com.teammate.android.data.LoginResponse
import com.teammate.android.data.Project
import kotlinx.coroutines.launch

@Composable
fun AIAssistantScreen(
    user: LoginResponse,
    project: Project,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var result by remember { mutableStateOf<String?>(null) }
    var question by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun runAI(action: suspend () -> com.teammate.android.data.Message) {
        scope.launch {
            loading = true
            error = null
            result = null
            runCatching { action() }
                .onSuccess { result = it.content }
                .onFailure { error = it.message ?: "AI request failed" }
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                navigationIcon = {
                    Button(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Project: ${project.name}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("让 AI 理解最近的项目聊天记录。")

            Button(
                onClick = {
                    runAI { ApiClient.api.summarize(project.id, user.userId) }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📝 总结讨论")
            }

            Button(
                onClick = {
                    runAI { ApiClient.api.extractTasks(project.id, user.userId) }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✓ 提取任务")
            }

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("向 AI 提问") },
                placeholder = { Text("例如：谁负责登录接口？") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            )

            Button(
                onClick = {
                    runAI {
                        ApiClient.api.askAI(
                            project.id,
                            user.userId,
                            AIAskRequest(question.trim())
                        )
                    }
                },
                enabled = !loading && question.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💬 Ask AI")
            }

            if (loading) {
                CircularProgressIndicator()
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            result?.let {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("🤖 AI", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(it)
                    }
                }
            }
        }
    }
}
