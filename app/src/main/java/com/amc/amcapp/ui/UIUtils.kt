package com.amc.amcapp.ui

import android.widget.CheckBox
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.EquipmentType
import com.amc.amcapp.equipments.AddEquipmentState
import com.amc.amcapp.equipments.AddEquipmentViewModel
import com.amc.amcapp.equipments.ComplaintUiState
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.viewmodel.AddUserState
import com.amc.amcapp.viewmodel.AddUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    minLines: Int = 1,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        minLines = minLines,
        enabled = enabled
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun AppProgressBar(boxScope: BoxScope) {
    boxScope.apply {
        BubbleProgressBar(
            count = 5,
            dotSize = 16.dp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            animationDurationMs = 400
        )
    }
}

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    minLines: Int = 1,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
        minLines = minLines,
        enabled = enabled
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun AppLoadingBar(scope: BoxScope) {
    scope.apply {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
fun AppError(errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp
        )
    }
}

fun showSnackBar(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    snackBarDuration: SnackbarDuration = SnackbarDuration.Long,
    onActionClicked: () -> Unit = {}
) {
    scope.launch {
        snackBarHostState.showSnackbar(
            message = message, duration = snackBarDuration, actionLabel = actionLabel
        ).let { result ->
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    onActionClicked()
                }

                SnackbarResult.Dismissed -> {

                }
            }
        }
    }
}

@Composable
fun EmailField(text: String, onValueChange: (String) -> Unit, enabled: Boolean = true) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text("Email") },
        textStyle = TextStyle(
            fontSize = LocalDimens.current.textMedium.sp
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email, contentDescription = "Email Icon"
            )
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )
}

@Composable
fun PasswordField(
    label: String = "Password",
    text: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val hideIcon = remember { mutableStateOf(true) }
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(label) },
        textStyle = TextStyle(
            fontSize = LocalDimens.current.textMedium.sp
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Password, contentDescription = "Password Icon"
            )
        },
        trailingIcon = {
            Icon(
                imageVector = if (hideIcon.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = "Password Icon",
                modifier = Modifier.clickable {
                    hideIcon.value = !hideIcon.value
                })
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (hideIcon.value) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )
}

@Composable
fun RoundedTextGradient(text: String) {
    val brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
        )
    )
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(brush)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 24.sp
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedSectionCard(
    title: String,
    icon: ImageVector,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.large,
        onClick = { expanded = !expanded }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse"
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(400)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(400)) + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun RoleSelectionSection(
    userState: AddUserState, viewModel: AddUserViewModel, isEditEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEditEnabled) 1f else 0.6f) // dim when read-only
    ) {
        Text(
            "I am a:",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
        )

        UserType.entries.filter { it != UserType.ADMIN }.forEach { role ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isEditEnabled) { viewModel.onRoleChanged(role) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (userState.userType == role),
                    onClick = { viewModel.onRoleChanged(role) },
                    enabled = isEditEnabled
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = role.label, fontSize = LocalDimens.current.textLarge.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (role == UserType.GYM_OWNER && userState.userType == UserType.GYM_OWNER) {
                    Checkbox(
                        checked = userState.isAmcEnabled, onCheckedChange = viewModel::onAmcEnabled
                    )
                    Text(
                        text = "Is AMC Enabled?", fontSize = LocalDimens.current.textMedium.sp
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopErrorBanner(
    errorMessage: String?, modifier: Modifier = Modifier, fontSize: Float = 16f
) {
    Box(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = !errorMessage.isNullOrEmpty(), enter = slideInVertically(
                initialOffsetY = { -it }, animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(300)), exit = slideOutVertically(
                targetOffsetY = { -it }, animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(300)), modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = fontSize.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}


@Composable
fun ComplaintItem(
    complaintUiState: ComplaintUiState, onCheckedChange: (Boolean) -> Unit
) {
    ListItem(headlineContent = {
        Text(text = complaintUiState.complaint.name)
    }, trailingContent = {
        Checkbox(
            checked = complaintUiState.isSelected, onCheckedChange = onCheckedChange
        )
    }, modifier = Modifier.clickable {
        onCheckedChange(!complaintUiState.isSelected)
    })
}

