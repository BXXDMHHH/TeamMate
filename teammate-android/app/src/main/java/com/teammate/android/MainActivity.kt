package com.teammate.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teammate.android.data.ApiClient
import com.teammate.android.data.LoginRequest
import com.teammate.android.data.LoginResponse
import com.teammate.android.data.Message
import com.teammate.android.data.Project
import com.teammate.android.data.SendMessageRequest
import kotlinx.coroutines.launch

// 用一个 sealed interface 表示“当前正在显示哪个页面”。
// 这样我们暂时不用 Navigation 组件，也可以完成页面切换。
private sealed interface Screen {
    data object Login : Screen
    data object Projects : Screen
    data class Chat(val project: Project) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 启动后，从这里开始显示我们的 Compose UI。
        setContent { TeamMateApp() }
    }
}

@Composable
private fun TeamMateApp() {
    // screen：记录当前页面。
    var screen by remember { mutableStateOf<Screen>(Screen.Login) }

    // currentUser：登录成功后保存当前用户的信息。
    // 例如 userId、username、email。
    var currentUser by remember { mutableStateOf<LoginResponse?>(null) }

    MaterialTheme {
        // 根据当前 screen 决定显示哪个页面。
        when (val currentScreen = screen) {
            Screen.Login -> LoginScreen { user ->
                // 登录成功：保存用户，然后跳转到项目列表。
                currentUser = user
                screen = Screen.Projects
            }

            Screen.Projects -> ProjectScreen(
                user = currentUser!!,
                onProjectClick = { project ->
                    // 点击项目：进入聊天页面。
                    screen = Screen.Chat(project)
                }
            )

            is Screen.Chat -> ChatScreen(
                user = currentUser!!,
                project = currentScreen.project,
                onBack = {
                    // 点击返回：回到项目列表。
                    screen = Screen.Projects
                }
            )
        }
    }
}

@Composable
private fun LoginScreen(onLogin: (LoginResponse) -> Unit) {
    // Coroutine 用来执行网络请求。
    // 网络请求不能直接阻塞 Android UI 线程。
    val scope = rememberCoroutineScope()

    // 登录输入框的内容。
    // 这里给了 Demo 默认账号，方便我们测试。
    var email by remember { mutableStateOf("alice@test.com") }
    var password by remember { mutableStateOf("123456") }

    // loading=true 时，表示正在请求 Java 后端。
    var loading by remember { mutableStateOf(false) }

    // 保存登录失败时显示给用户的错误信息。
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("TeamMate") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("AI Team Assistant", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            // Email 输入框。
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Password 输入框。
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    // 点击 Login 后启动一个协程执行网络请求。
                    scope.launch {
                        loading = true
                        error = null

                        // ApiClient.api.login(...) 最终会调用：
                        // POST /api/auth/login
                        // 也就是我们之前写的 Java Spring Boot API。
                        runCatching {
                            ApiClient.api.login(LoginRequest(email, password))
                        }
                            .onSuccess { user ->
                                // 后端返回成功：把用户信息交给 TeamMateApp。
                                onLogin(user)
                            }
                            .onFailure { exception ->
                                // 后端返回错误：把错误显示在页面上。
                                error = exception.message ?: "Login failed"
                            }

                        loading = false
                    }
                },
                // 请求过程中禁止重复点击 Login。
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) {
                    CircularProgressIndicator()
                } else {
                    Text("Login")
                }
            }

            // 如果 error 不为空，就显示错误信息。
            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ProjectScreen(user: LoginResponse, onProjectClick: (Project) -> Unit) {
    // 用于执行网络请求。
    val scope = rememberCoroutineScope()

    // 保存后端返回的项目列表。
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect：进入项目页面时自动执行一次。
    androidx.compose.runtime.LaunchedEffect(user.userId) {
        // 调用：GET /api/projects?userId=xxx
        runCatching { ApiClient.api.getProjects(user.userId) }
            .onSuccess { projects = it }
            .onFailure { error = it.message ?: "Failed to load projects" }

        loading = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("My Projects") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Hi, ${user.username}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            when {
                loading -> CircularProgressIndicator()

                error != null -> Text(
                    error!!,
                    color = MaterialTheme.colorScheme.error
                )

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 把每一个 Project 显示成一个 Card。
                    items(projects) { project ->
                        Card(
                            onClick = { onProjectClick(project) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    project.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(project.description ?: "No description")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(user: LoginResponse, project: Project, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    // messages：当前项目的所有聊天消息。
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }

    // text：输入框当前输入的内容。
    var text by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }
    var sending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // 进入聊天页面时，加载历史消息。
    androidx.compose.runtime.LaunchedEffect(project.id) {
        // 调用：GET /api/projects/{projectId}/messages?userId=xxx
        runCatching { ApiClient.api.getMessages(project.id, user.userId) }
            .onSuccess { messages = it }
            .onFailure { error = it.message ?: "Failed to load messages" }

        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project.name) },
                navigationIcon = {
                    Button(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        },

        // bottomBar 放在屏幕最下面，用来输入和发送消息。
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    enabled = !sending
                )

                Button(
                    onClick = {
                        // 不允许发送空消息。
                        if (text.isBlank()) return@Button

                        // trim() 去掉用户输入前后的空格。
                        val content = text.trim()

                        scope.launch {
                            sending = true

                            // 调用：POST /api/projects/{projectId}/messages
                            runCatching {
                                ApiClient.api.sendMessage(
                                    project.id,
                                    SendMessageRequest(user.userId, content)
                                )
                            }.onSuccess { newMessage ->
                                // 后端保存成功后，把新消息追加到当前列表。
                                messages = messages + newMessage

                                // 清空输入框。
                                text = ""
                            }.onFailure { exception ->
                                error = exception.message ?: "Failed to send message"
                            }

                            sending = false
                        }
                    },
                    enabled = !sending && text.isNotBlank()
                ) {
                    Text("Send")
                }
            }
        }
    ) { padding ->
        if (loading) {
            // 第一次加载历史消息时显示 Loading。
            Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding)) {
                // 显示网络错误，但不影响页面继续显示。
                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // LazyColumn 类似 Android 传统的 RecyclerView，
                // 适合显示数量比较多的聊天消息。
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                // 消息发送者。
                                Text(
                                    message.username,
                                    style = MaterialTheme.typography.labelLarge
                                )

                                // 消息正文。
                                Text(message.content)
                            }
                        }
                    }
                }
            }
        }
    }
}
