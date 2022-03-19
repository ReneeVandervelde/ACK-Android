package com.inkapplications.ack.android.capture.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData

@Composable
fun MessageScreen(
    screenState: State<MessageScreenState>,
    controller: MessageScreenController,
    bottomContentProtection: Dp,
) = AckScreen {
    when (val state = screenState.value) {
        is MessageScreenState.MessageList -> MessageList(state)
        is MessageScreenState.Empty -> EmptyPlaceholder()
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = bottomContentProtection),
        contentAlignment = Alignment.BottomEnd,
    ) {
        FloatingActionButton(
            onClick = controller::onCreateMessageClick,
            backgroundColor = AckTheme.colors.surface,
            contentColor = contentColorFor(AckTheme.colors.surface),
            modifier = Modifier.padding(AckTheme.dimensions.gutter)
        ) {
            Icon(Icons.Default.Message, "Compose Message")
        }
    }
}

@Composable
private fun EmptyPlaceholder() = Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize()
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            tint = AckTheme.colors.foregroundInactive,
            modifier = Modifier.size(AckTheme.dimensions.placeholderIcon),
        )
        Text("No messages received")
    }
}

@Composable
private fun MessageList(state: MessageScreenState.MessageList) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = AckTheme.dimensions.navigationProtection)
    ) {
        items(state.messages) { message ->
            MessageItem(message)
        }
    }
}

@Composable
private fun MessageItem(packet: CapturedPacket) {
    Card(modifier = Modifier.padding(horizontal = AckTheme.dimensions.gutter, vertical = AckTheme.dimensions.singleItem)) {
        Column(modifier = Modifier.padding(AckTheme.dimensions.content).fillMaxWidth()) {
            Text(packet.parsed.route.source.toString(), style = AckTheme.typography.h2)
            val data = packet.parsed.data as? PacketData.Message ?: run {
                Text("Unsupported data type: ${packet.parsed.data.javaClass.simpleName}")
                return@Card
            }
            Text(data.message)
            if (data.messageNumber != null) {
                Text("#${data.messageNumber}")
            }
        }
    }
}
