import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amc.amcapp.Equipment
import com.amc.amcapp.gym.EquipmentsListViewModel
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.GymDest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EquipmentsListScreen(
    navController: NavController,
    user: User,
    equipmentsListViewModel: EquipmentsListViewModel = koinViewModel()
) {
    val equipmentsListState by equipmentsListViewModel.equipmentsListState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd), onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("user", user)
                }
                navController.navigate(GymDest.AddEquipment.route)
            }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
private fun EquipmentItem(equipment: Equipment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = equipment.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
