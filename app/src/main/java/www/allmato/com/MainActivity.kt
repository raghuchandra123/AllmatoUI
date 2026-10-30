package www.allmato.com

import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import www.allmato.com.data.model.MenuItem
import www.allmato.com.ui.home.HomeViewModel
import www.allmato.com.ui.theme.AllmatoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.launch
import www.allmato.com.data.model.OrderResponse
import www.allmato.com.data.remote.RetrofitClient
import java.sql.DriverManager.println

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AllmatoTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {

    var itemState = rememberLazyListState()

    var context = LocalContext.current

    val viewModel : HomeViewModel = viewModel()

    LaunchedEffect(itemState) {
        snapshotFlow({ itemState.canScrollForward })
            .collect {
                    canScroll ->
                    if (!canScroll) {
                        viewModel.callGetItems()
                    }
            }
    }

    LaunchedEffect(viewModel) {
        viewModel.orderResponse.collect {
            message -> {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        println("ccc: ")
        Text(
            text = "Allmato",
            style = MaterialTheme.typography.headlineMedium
        )

        Scaffold(
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(1.dp, Color.Black)
                        .background(Color(0xFF303030))
                        .padding(horizontal = 12.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var cost = 0;
                    for (item in viewModel.items) {
                        cost += (item.price * (viewModel.quantityMap[item.item]?:0))
                    }

                    Text(
                        text = cost.toString(),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            viewModel.placeOrder()
                        },
                        enabled = (cost > 0),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Order")
                    }

                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = itemState,
                contentPadding = innerPadding
            ) {

                items(viewModel.items) { item ->
                    MenuItemBox(item,viewModel)
                }
            }
        }
    }
}

@Composable
fun MenuItemBox(item: MenuItem, viewModel : HomeViewModel) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.dp, Color.Black)
            .padding(horizontal = 12.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // Item name - left
        Text(
            text = item.item,
            modifier = Modifier.weight(1f)
        )

        // Price of ONE quantity
        Text(
            text = "₹${item.price}"
        )

        Spacer(modifier = Modifier.width(12.dp))

        // -
        Button(
            onClick = {
                viewModel.decrementQuantity(item.item, 1);
            }
        ) {
            Text("-")
        }

        // Quantity
        Text(
            text = (viewModel.quantityMap[item.item]?:0).toString(),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // +
        Button(
            onClick = {
                viewModel.incrementQuantity(item.item, 1);
            }
        ) {
            Text("+")
        }
    }
}
