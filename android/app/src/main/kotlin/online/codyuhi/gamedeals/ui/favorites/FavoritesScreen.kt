package online.codyuhi.gamedeals.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import online.codyuhi.gamedeals.domain.FavoriteGroup
import online.codyuhi.gamedeals.domain.FavoriteItem
import online.codyuhi.gamedeals.ui.MainUiState
import online.codyuhi.gamedeals.ui.components.CedarButton
import online.codyuhi.gamedeals.ui.components.CedarCard
import online.codyuhi.gamedeals.ui.components.CedarOutlinedButton
import online.codyuhi.gamedeals.ui.components.CedarPill
import online.codyuhi.gamedeals.ui.components.CedarTextField
import online.codyuhi.gamedeals.ui.components.Lucide
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.Eyebrow

@Composable
fun FavoritesScreen(
    state: MainUiState,
    onAddGroup: (String) -> Unit,
    onRenameGroup: (String, String) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    var groupToRename by remember { mutableStateOf<String?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var groupToDelete by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        // Header with title and New Group button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Cedar.colors.ink,
                )
                Text(
                    text = "Grouped the way you like them.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cedar.colors.ink4,
                )
            }

            CedarButton(
                onClick = {
                    newGroupName = ""
                    showCreateGroupDialog = true
                },
                text = "New Group",
                leadingIcon = Lucide.FolderPlus,
            )
        }

        Spacer(Modifier.height(12.dp))

        if (state.favoriteGroups.isEmpty() || state.favoriteGroups.all { it.items.isEmpty() }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Lucide.Heart,
                        contentDescription = null,
                        tint = Cedar.colors.ink4,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No favorites yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = Cedar.colors.ink2,
                    )
                    Text(
                        "Tap the heart on any deal to save it to your wishlist groups.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cedar.colors.ink4,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.favoriteGroups, key = { it.nickname }) { group ->
                    FavoriteGroupSection(
                        group = group,
                        onRename = {
                            groupToRename = group.nickname
                            renameInput = group.nickname
                        },
                        onDelete = { groupToDelete = group.nickname },
                        onRemoveItem = onRemoveItem,
                        onGameClick = onGameClick,
                    )
                }
                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }

    // Create Group Dialog
    if (showCreateGroupDialog) {
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = { Text("Create New Group", style = MaterialTheme.typography.titleLarge) },
            text = {
                CedarTextField(
                    value = newGroupName,
                    onValueChange = { newGroupName = it },
                    placeholder = "Group name (e.g. Steam Deck, Backlog)",
                    singleLine = true,
                )
            },
            confirmButton = {
                CedarButton(
                    onClick = {
                        if (newGroupName.isNotBlank()) {
                            onAddGroup(newGroupName.trim())
                            showCreateGroupDialog = false
                        }
                    },
                    text = "Create",
                )
            },
            dismissButton = {
                CedarOutlinedButton(
                    onClick = { showCreateGroupDialog = false },
                    text = "Cancel",
                )
            },
            containerColor = Cedar.colors.surface,
        )
    }

    // Rename Group Dialog
    if (groupToRename != null) {
        AlertDialog(
            onDismissRequest = { groupToRename = null },
            title = { Text("Rename Group", style = MaterialTheme.typography.titleLarge) },
            text = {
                CedarTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    singleLine = true,
                )
            },
            confirmButton = {
                CedarButton(
                    onClick = {
                        val old = groupToRename!!
                        val new = renameInput.trim()
                        if (new.isNotBlank() && new != old) {
                            onRenameGroup(old, new)
                        }
                        groupToRename = null
                    },
                    text = "Save",
                )
            },
            dismissButton = {
                CedarOutlinedButton(
                    onClick = { groupToRename = null },
                    text = "Cancel",
                )
            },
            containerColor = Cedar.colors.surface,
        )
    }

    // Delete Group Dialog
    if (groupToDelete != null) {
        AlertDialog(
            onDismissRequest = { groupToDelete = null },
            title = { Text("Delete Group?", style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    "Are you sure you want to delete the \"${groupToDelete}\" group and all saved favorites inside it?",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                CedarButton(
                    onClick = {
                        onDeleteGroup(groupToDelete!!)
                        groupToDelete = null
                    },
                    text = "Delete",
                )
            },
            dismissButton = {
                CedarOutlinedButton(
                    onClick = { groupToDelete = null },
                    text = "Cancel",
                )
            },
            containerColor = Cedar.colors.surface,
        )
    }
}

@Composable
private fun FavoriteGroupSection(
    group: FavoriteGroup,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onRemoveItem: (Long) -> Unit,
    onGameClick: (String) -> Unit,
) {
    CedarCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Group Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.nickname,
                        style = MaterialTheme.typography.titleLarge,
                        color = Cedar.colors.ink,
                    )
                    Spacer(Modifier.width(8.dp))
                    CedarPill(
                        text = "${group.items.size}",
                        bgColor = Cedar.colors.inset,
                        textColor = Cedar.colors.ink3,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onRename, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Lucide.Edit2,
                            contentDescription = "Rename group",
                            tint = Cedar.colors.ink3,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Lucide.Trash2,
                            contentDescription = "Delete group",
                            tint = Cedar.colors.rust,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (group.items.isEmpty()) {
                Text(
                    text = "No games in this group yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Cedar.colors.ink4,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    group.items.forEach { item ->
                        FavoriteItemRow(
                            item = item,
                            onClick = { onGameClick(item.gameID) },
                            onRemove = { onRemoveItem(item.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteItemRow(
    item: FavoriteItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Lucide.Gamepad2,
                contentDescription = null,
                tint = Cedar.colors.brand,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Cedar.colors.ink,
            )
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = Lucide.X,
                contentDescription = "Remove from favorites",
                tint = Cedar.colors.ink4,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
