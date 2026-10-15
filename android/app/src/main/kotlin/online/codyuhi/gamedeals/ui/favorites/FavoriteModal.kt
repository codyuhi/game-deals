package online.codyuhi.gamedeals.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import online.codyuhi.gamedeals.ui.components.CedarButton
import online.codyuhi.gamedeals.ui.components.CedarOutlinedButton
import online.codyuhi.gamedeals.ui.components.CedarTextField
import online.codyuhi.gamedeals.ui.theme.Cedar

@Composable
fun FavoriteModal(
    gameID: String,
    gameTitle: String,
    groups: List<FavoriteGroup>,
    onSave: (groupNickname: String, gameID: String, name: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedGroup by remember {
        mutableStateOf(groups.firstOrNull()?.nickname ?: "Favorites")
    }
    var isCreatingNewGroup by remember { mutableStateOf(groups.isEmpty()) }
    var newGroupName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add to Favorites",
                style = MaterialTheme.typography.titleLarge,
                color = Cedar.colors.ink,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = gameTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Cedar.colors.ink2,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Select group:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Cedar.colors.ink3,
                )

                Spacer(Modifier.height(6.dp))

                groups.forEach { group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isCreatingNewGroup = false
                                selectedGroup = group.nickname
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = !isCreatingNewGroup && selectedGroup == group.nickname,
                            onClick = {
                                isCreatingNewGroup = false
                                selectedGroup = group.nickname
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Cedar.colors.spruce),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = group.nickname,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Cedar.colors.ink,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCreatingNewGroup = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isCreatingNewGroup,
                        onClick = { isCreatingNewGroup = true },
                        colors = RadioButtonDefaults.colors(selectedColor = Cedar.colors.spruce),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Create new group…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cedar.colors.ink,
                    )
                }

                if (isCreatingNewGroup) {
                    Spacer(Modifier.height(8.dp))
                    CedarTextField(
                        value = newGroupName,
                        onValueChange = {
                            newGroupName = it
                            errorMessage = null
                        },
                        placeholder = "New group nickname",
                        singleLine = true,
                    )
                }

                if (errorMessage != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = errorMessage!!,
                        color = Cedar.colors.rust,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            CedarButton(
                onClick = {
                    val finalGroup = if (isCreatingNewGroup) newGroupName.trim() else selectedGroup
                    if (finalGroup.isBlank()) {
                        errorMessage = "Group name cannot be blank."
                        return@CedarButton
                    }
                    onSave(finalGroup, gameID, gameTitle)
                },
                text = "Save",
            )
        },
        dismissButton = {
            CedarOutlinedButton(
                onClick = onDismiss,
                text = "Cancel",
            )
        },
        containerColor = Cedar.colors.surface,
    )
}
