package www.allmato.com.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import www.allmato.com.data.model.MenuItem
import www.allmato.com.data.model.OrderResponse
import www.allmato.com.data.remote.RetrofitClient
import java.sql.DriverManager

class HomeViewModel() : ViewModel() {
    private var isStillCallingAPI = false
    private var lastKey =  ""
    private var isQueryPsbl = true
    private val _items = mutableStateListOf<MenuItem>()
    private val _quantityMap =  mutableStateMapOf<String, Int>()

    private val _orderResponse = MutableStateFlow<String>("")

    val orderResponse = _orderResponse.asStateFlow()
    val items :  List<MenuItem> get() = _items
    val quantityMap: Map<String, Int> get() = _quantityMap


    fun callGetItems() {
        viewModelScope.launch {
            if ((isQueryPsbl) && (!isStillCallingAPI)) {
                DriverManager.println("New Batch called")
                isStillCallingAPI = true;
                try {
                    val newItemsMenu = RetrofitClient.api.getItems(lastKey)
                    if (newItemsMenu.menu.size != 0) {
                        println(newItemsMenu.menu.size)
                        _items.addAll(newItemsMenu.menu)
                        println(items.size)
                    }
                    lastKey = newItemsMenu.lstKeEncrpt
                    isQueryPsbl = newItemsMenu.canQueryForMore
                } catch (ex : Exception) {
                    println(ex)
                }
                finally {
                    isStillCallingAPI = false;
                }
            }
        }
    }

    fun decrementQuantity(item: String, i: Int) {
        if (_quantityMap[item] != null && _quantityMap[item]!! >= i) {
            _quantityMap[item] = (_quantityMap[item]!!) - i;
        }
    }
    fun incrementQuantity(item: String, i: Int) {
        _quantityMap[item] = (_quantityMap[item]?:0) + i;
    }

    fun placeOrder() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.order(_quantityMap)
                if (response.isSuccess) {
                    _orderResponse.emit(response.message)
                }
            } catch (ex : Exception) {
                println(ex)
            }
        }
    }

}