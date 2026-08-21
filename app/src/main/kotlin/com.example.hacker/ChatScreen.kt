package com.example.hacker

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.basicTextField
import androidx.compose.ui.text.input.keyboardActions
import androidx.compose.ui.text.input.submitKey

@Composable
fun ChatScreen() {
    var message by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf<List<String>>() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chat messages
            if (chatHistory.isNotEmpty()) {
                androidx.compose.foundation.layout.fillMaxWeight()
                androidx.compose.ui.semantics.semantics {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.ui.unit.dp(8.dp),
                        items = chatHistory.mapIndexed { index, text ->
                            ChatMessageRow(text = text, isUser = true)
                        }
                    )
                }
            }

            // Input area
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                basicTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Type a message") },
                    keyboardActions = keyboardActions {
                        lastKey = submitKey
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                androidx.compose.material3.IconButton(
                    onClick = {
                        // Send message
                        if (message.isNotEmpty()) {
                            chatHistory = chatHistory + message
                            message = ""
                        }
                    }
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material3.icons.Default.Send
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageRow(text: String, isUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        val content = if (isUser) text else "HACKER: " + text
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isUser) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary
        )
    }
}