package com.amc.amcapp.ui.screens.amc

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.RoundedTextGradient
import com.amc.amcapp.ui.theme.LocalDimens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserItem(
    user: User,
    onClick: (() -> Unit)? = null,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier
                    .padding(LocalDimens.current.spacingLarge.dp)
                    .align(Alignment.TopEnd),
                imageVector =
                    when (user.userType) {
                        UserType.CUSTOMER -> Icons.Default.Person
                        UserType.TECHNICIAN -> Icons.Default.HomeRepairService
                        UserType.ADMIN -> Icons.Default.Person
                        UserType.GYM_OWNER -> Icons.Default.SportsGymnastics
                        UserType.SALES_PERSON -> Icons.Default.Money
                    },
                contentDescription = user.userType.label,
                tint = MaterialTheme.colorScheme.secondary
            )
            Row(
                modifier = Modifier.padding(LocalDimens.current.spacingLarge.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundedTextGradient(user.name.toCharArray()[0].toString())

                Spacer(Modifier.width(LocalDimens.current.spacingMedium.dp))

                Column {

                    Text(
                        text = user.name,
                        fontSize = LocalDimens.current.textLarge.sp,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = user.userType.label,
                        fontSize = LocalDimens.current.textMedium.sp,
                    )
                }

            }
        }
    }
}

