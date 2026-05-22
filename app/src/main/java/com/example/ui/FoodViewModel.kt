package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FoodViewModel(private val repository: FoodRepository) : ViewModel() {

    // Filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCuisine = MutableStateFlow("All")
    val selectedCuisine: StateFlow<String> = _selectedCuisine.asStateFlow()

    private val _selectedMealType = MutableStateFlow("All")
    val selectedMealType: StateFlow<String> = _selectedMealType.asStateFlow()

    // Observable DB states
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI-only interactive checkout selection states
    val _selectedDelivery = MutableStateFlow<DeliveryCompany>(repository.deliveryCompanies.first())
    val selectedDelivery: StateFlow<DeliveryCompany> = _selectedDelivery.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow("MTN MoMo")
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    private val _momoNumber = MutableStateFlow("")
    val momoNumber: StateFlow<String> = _momoNumber.asStateFlow()

    private val _momoProvider = MutableStateFlow("MTN MoMo")
    val momoProvider: StateFlow<String> = _momoProvider.asStateFlow()

    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut.asStateFlow()

    // Active order being tracked
    private val _activeTrackedOrder = MutableStateFlow<OrderEntity?>(null)
    val activeTrackedOrder: StateFlow<OrderEntity?> = _activeTrackedOrder.asStateFlow()

    // Filtered restaurants selector
    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        _searchQuery, _selectedCuisine, _selectedMealType
    ) { query, cuisine, mealType ->
        repository.restaurants.filter { rest ->
            val matchesQuery = rest.name.contains(query, ignoreCase = true) ||
                    rest.description.contains(query, ignoreCase = true)
            val matchesCuisine = cuisine == "All" || rest.cuisines.contains(cuisine)
            val matchesMealType = mealType == "All" || rest.menu.any { it.mealType.equals(mealType, ignoreCase = true) }
            matchesQuery && matchesCuisine && matchesMealType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.restaurants)

    // Constant lists handy for the views
    val cuisinesList = repository.cuisinesList
    val mealTypesList = repository.mealTypesList
    val deliveryCompanies = repository.deliveryCompanies

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCuisine(cuisine: String) {
        _selectedCuisine.value = cuisine
    }

    fun setSelectedMealType(mealType: String) {
        _selectedMealType.value = mealType
    }

    fun setDeliveryCompany(company: DeliveryCompany) {
        _selectedDelivery.value = company
    }

    fun setPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun setMomoNumber(num: String) {
        _momoNumber.value = num
    }

    fun setMomoProvider(provider: String) {
        _momoProvider.value = provider
    }

    // Cart Actions
    fun addToCart(restaurant: Restaurant, item: MenuItem) {
        viewModelScope.launch {
            val currentCart = cartItems.value
            if (currentCart.isNotEmpty() && currentCart.first().restaurantId != restaurant.id) {
                // Warning handles restaurant switching, let's clear it automatically or caller does it
                repository.clearCart()
            }
            
            val existing = currentCart.find { it.itemId == item.id }
            if (existing != null) {
                repository.updateCartItem(existing.copy(quantity = existing.quantity + 1))
            } else {
                repository.addToCart(
                    CartItem(
                        restaurantId = restaurant.id,
                        restaurantName = restaurant.name,
                        itemId = item.id,
                        itemName = item.name,
                        price = item.price,
                        quantity = 1,
                        itemImage = item.id
                    )
                )
            }
        }
    }

    fun updateCartQuantity(cartItem: CartItem, change: Int) {
        viewModelScope.launch {
            val newQty = cartItem.quantity + change
            if (newQty <= 0) {
                repository.deleteCartItem(cartItem)
            } else {
                repository.updateCartItem(cartItem.copy(quantity = newQty))
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun selectOrderForTracking(order: OrderEntity) {
        _activeTrackedOrder.value = order
    }

    fun placeOrder(
        restaurantId: String,
        restaurantName: String,
        deliveryFee: Double,
        itemsSummary: String,
        totalAmount: Double,
        waitTimeMinutes: Int,
        onFinished: (Int) -> Unit
    ) {
        viewModelScope.launch {
            _isCheckingOut.value = true
            // Simulate brief, stylized paystack/momo processing checkout wait
            delay(1800)

            val order = OrderEntity(
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                deliveryCompany = _selectedDelivery.value.name,
                deliveryFee = deliveryFee,
                paymentMethod = _selectedPaymentMethod.value,
                phoneNumber = if (_selectedPaymentMethod.value.contains("MoMo")) _momoNumber.value else "",
                totalAmount = totalAmount,
                waitTimeMinutes = waitTimeMinutes,
                status = "Confirmed",
                itemsSummary = itemsSummary
            )

            val id = repository.placeOrder(order)
            val insertedOrder = order.copy(id = id.toInt())
            _activeTrackedOrder.value = insertedOrder
            _isCheckingOut.value = false
            repository.clearCart()
            onFinished(insertedOrder.id)

            // Start simulated delivery status loop in background
            simulateDeliveryLifecycle(insertedOrder.id)
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            updateActiveTrackedIfMatches(orderId, status)
        }
    }

    private fun simulateDeliveryLifecycle(orderId: Int) {
        viewModelScope.launch {
            // "Confirmed" -> "Preparing"
            delay(8000)
            repository.updateOrderStatus(orderId, "Preparing")
            updateActiveTrackedIfMatches(orderId, "Preparing")

            // "Preparing" -> "Out for Delivery"
            delay(12000)
            repository.updateOrderStatus(orderId, "Out for Delivery")
            updateActiveTrackedIfMatches(orderId, "Out for Delivery")

            // "Out for Delivery" -> "Delivered"
            delay(15000)
            repository.updateOrderStatus(orderId, "Delivered")
            updateActiveTrackedIfMatches(orderId, "Delivered")
        }
    }

    private suspend fun updateActiveTrackedIfMatches(orderId: Int, status: String) {
        val currentActive = _activeTrackedOrder.value
        if (currentActive != null && currentActive.id == orderId) {
            _activeTrackedOrder.value = currentActive.copy(status = status)
        }
    }
}

class FoodViewModelFactory(private val repository: FoodRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            return FoodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
