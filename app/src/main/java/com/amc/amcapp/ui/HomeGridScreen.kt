package com.amc.amcapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.amc.amcapp.R
import com.amc.amcapp.ui.theme.LocalDimens

data class MenuItem(
    val id: String, val label: String, val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualSizeMenuGridScreen(
    items: List<MenuItem>,
    onClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    minTileSize: Dp = LocalDimens.current.tileSize.dp,
    tileCorner: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    spacing: Dp = 12.dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.red_gym),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(0.dp)
                .size(240.dp)
                .align(
                    Alignment.TopCenter
                )
        )

        val inner = PaddingValues()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = minTileSize),
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
                top = inner.calculateTopPadding() + contentPadding.calculateTopPadding(),
                bottom = inner.calculateBottomPadding() + contentPadding.calculateBottomPadding()
            ),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = modifier
                .align(
                    Alignment.BottomCenter
                )
        ) {
            items(items, key = { it.id }) { item ->
                MenuTile(
                    item = item, onClick = { onClick(item) }, corner = tileCorner
                )
            }
        }
    }

}

@Composable
private fun MenuTile(
    item: MenuItem, onClick: () -> Unit, corner: Dp
) {
    // aspectRatio(1f) forces a square -> guarantees equal width & height
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(corner))
            .clickable(onClick = onClick),
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/* ---------- Preview / Sample usage ---------- */

@Composable
fun SampleEqualSizeMenuGrid() {
    val items = listOf(
        MenuItem("dash", "Dashboard", Icons.Default.Dashboard),
        MenuItem("cust", "Customers", Icons.Default.People),
        MenuItem("sales", "Sales", Icons.Default.ShoppingCart),
        MenuItem("inv", "Inventory", Icons.Default.Inventory),
        MenuItem("rep", "Reports", Icons.Default.Assessment),
        MenuItem("set", "Settings", Icons.Default.Settings),
        MenuItem("help", "Help", Icons.Default.Help),
        MenuItem("abt", "About", Icons.Default.Info)
    )
    EqualSizeMenuGridScreen(
        items = items, onClick = { /* handle navigation */ })
}
