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

private sealed interface Screen {
    data object Login : Screen
    data object Projects : Screen
    data class Chat(val project: Project) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TeamMateApp() }
    }
}

@Composable
private fun TeamMateApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Login) }
    var currentUser by remember { mutableStateOf<LoginResponse?>(null) }

    MaterialTheme {
        when (val currentScreen = screen) {
            Screen.Login -> LoginScreen { user ->
                currentUser = user
                screen = Screen.Projects
            }
            Screen.Projects -> ProjectScreen(
                user = currentUser!!,
                onProjectClick = { screen = Screen.Chat(it) }
            )
            is Screen.Chat -> ChatScreen(
                user = currentUser!!,
                project = currentScreen.project,
                onBack = { screen = Screen.Projects }
            )
        }
    }
}

@Composable
private fun LoginScreen(onLogin: (LoginResponse) -> Unit) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("alice@test.com") }
    var password by remember { mutableStateOf("123456") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("TeamMate") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("AI Team Assistant", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        error = null
                        runCatching { ApiClient.api.login(LoginRequest(email, password)) }
                            .onSuccess(onLogin)
                            .onFailure { error = it.message ?: "Login failed" }
                        loading = false
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) CircularProgressIndicator() else Text("Login")
            }
            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ProjectScreen(user: LoginResponse, onProjectClick: (Project) -> Unit) {
    val scope = rememberCoroutineScope()
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(user.userId) {
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
                error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(projects) { project ->
                        Card(onClick = { onProjectClick(project) }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(project.name, style = MaterialTheme.typography.titleMedium)
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
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var text by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var sending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(project.id) {
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
                    Button(onClick = onBack) { Text("Back") }
                }
            )
        },
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
                        if (text.isBlank()) return@Button
                        val content = text.trim()
                        scope.launch {
                            sending = true
                            runCatching {
                                ApiClient.api.sendMessage(
                                    project.id,
                                    SendMessageRequest(user.userId, content)
                                )
                            }.onSuccess {
                                messages = messages + it
                                text = ""
                            }.onFailure { error = it.message ?: "Failed to send message" }
                            sending = false
                        }
                    },
                    enabled = !sending && text.isNotBlank()
                ) { Text("Send") }
            }
        }
    ) { padding ->
        if (loading) {
            Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().padding(padding)) {
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(message.username, style = MaterialTheme.typography.labelLarge)
                                Text(message.content)
                            }
                        }
                    }
                }
            }
        }
    }
}
