package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartItem
import com.example.data.DeliveryCompany
import com.example.ui.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: FoodViewModel,
    onBackClick: () -> Unit,
    onOrderPlaced: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val selectedDelivery by viewModel.selectedDelivery.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val momoNumber by viewModel.momoNumber.collectAsState()
    val momoProvider by viewModel.momoProvider.collectAsState()
    val isCheckingOut by viewModel.isCheckingOut.collectAsState()

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val deliveryFee = if (cartItems.isNotEmpty()) selectedDelivery.fee else 0.0
    val serviceFee = if (cartItems.isNotEmpty()) 2.50 else 0.0
    val grandTotal = subtotal + deliveryFee + serviceFee

    val activeRestaurantName = cartItems.firstOrNull()?.restaurantName ?: ""
    val activeRestaurantId = cartItems.firstOrNull()?.restaurantId ?: ""

    // Find restaurant wait time Range
    val activeRestWaitRange = remember(activeRestaurantId) {
        viewModel.filteredRestaurants.value.find { it.id == activeRestaurantId }?.waitingTimeRange ?: "20-30 min"
    }
    val activeRestWaitMin = remember(activeRestaurantId) {
        viewModel.filteredRestaurants.value.find { it.id == activeRestaurantId }?.waitingTimeInMinutes ?: 25
    }

    // Interactive PIN verification mock bottom sheet
    var showPinSheet by remember { mutableStateOf(false) }
    var pinValue by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("cart_back_button")) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                if (selectedPaymentMethod.contains("MoMo")) {
                                    if (momoNumber.length < 9) {
                                        // simple prompt alert triggers in modal or just open pin
                                    }
                                    showPinSheet = true
                                } else {
                                    // Paystack simulated card popup triggers
                                    showPinSheet = true
                                }
                            },
                            enabled = !isCheckingOut && momoNumber.isNotEmpty() || !selectedPaymentMethod.contains("MoMo"),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("place_order_checkout_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isCheckingOut) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = "Pay ₵${String.format("%.2f", grandTotal)} with $selectedPaymentMethod",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛍️", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Checkout Cart is Empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Go select delicious food items from a restaurant first!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onBackClick) {
                        Text("Browse Restaurants")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Restaurant info context
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ordering from",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = activeRestaurantName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "Restaurant wait time: $activeRestWaitRange",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
                }

                // Ordered Food Items List
                item {
                    Text(
                        text = "Summary of Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                items(cartItems) { item ->
                    CartListItem(
                        cartItem = item,
                        quantityChange = { viewModel.updateCartQuantity(item, it) }
                    )
                }

                // Delivery Options Header
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Text(
                        text = "Select Delivery Partner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                // Delivery horizontal choices (SPECIFIC REQUIREMENT)
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.deliveryCompanies) { company ->
                            val isSelected = company.id == selectedDelivery.id
                            DeliveryCard(
                                company = company,
                                isSelected = isSelected,
                                onClick = { viewModel.setDeliveryCompany(company) }
                            )
                        }
                    }
                }

                // Payment Options Section (MOMO / PAYSTACK REQUIREMENT)
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PaymentSelectorButton(
                                name = "MoMo Pay",
                                icon = Icons.Default.PhoneAndroid,
                                isSelected = selectedPaymentMethod.startsWith("MTN MoMo") || selectedPaymentMethod.startsWith("Vodafone/Telecel"),
                                onClick = { viewModel.setPaymentMethod("MTN MoMo") },
                                modifier = Modifier.weight(1f)
                            )
                            PaymentSelectorButton(
                                name = "Paystack",
                                icon = Icons.Default.Payment,
                                isSelected = selectedPaymentMethod == "Paystack",
                                onClick = { viewModel.setPaymentMethod("Paystack") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dynamic Payment Method Context Form
                        if (selectedPaymentMethod.contains("MoMo")) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Mobile Money Details (GH/NG/Local)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Pick MoMo Provider
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        listOf("MTN MoMo", "Telecel Cash", "AirtelTigo").forEach { prov ->
                                            val isProvSelected = momoProvider == prov
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isProvSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                                border = if (isProvSelected) null else ButtonDefaults.outlinedButtonBorder,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        viewModel.setMomoProvider(prov)
                                                        viewModel.setPaymentMethod(prov)
                                                    }
                                            ) {
                                                Text(
                                                    text = prov,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(vertical = 10.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = momoNumber,
                                        onValueChange = { viewModel.setMomoNumber(it) },
                                        placeholder = { Text("e.g. 0244123456") },
                                        label = { Text("Mobile Money Phone Number") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("momo_phone_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Paystack Secure Gate (Debit Card & Bank)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = cardName,
                                        onValueChange = { cardName = it },
                                        placeholder = { Text("Kofi Mensah") },
                                        label = { Text("Cardholder Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = cardNumber,
                                        onValueChange = { cardNumber = it },
                                        placeholder = { Text("4111 2222 3333 4444") },
                                        label = { Text("Card Number") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth().testTag("paystack_card_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        OutlinedTextField(
                                            value = cardExpiry,
                                            onValueChange = { cardExpiry = it },
                                            placeholder = { Text("MM/YY") },
                                            label = { Text("Expiry") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        OutlinedTextField(
                                            value = cardCvv,
                                            onValueChange = { cardCvv = it },
                                            placeholder = { Text("123") },
                                            label = { Text("CVV") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bill Receipt Details View
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider()
                    Text(
                        text = "Receipt Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            ReceiptRow("Subtotal", "₵${String.format("%.2f", subtotal)}")
                            ReceiptRow("Delivery fee (${selectedDelivery.name})", "₵${String.format("%.2f", deliveryFee)}")
                            ReceiptRow("Service Processing fee", "₵${String.format("%.2f", serviceFee)}")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Grand Total",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "₵${String.format("%.2f", grandTotal)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // PIN Bottom Sheet simulation (Momo / Paystack trigger)
    if (showPinSheet) {
        AlertDialog(
            onDismissRequest = { showPinSheet = false },
            confirmButton = {
                Button(
                    onClick = {
                        showPinSheet = false
                        val itemsSummary = cartItems.joinToString(", ") { "${it.quantity}x ${it.itemName}" }
                        viewModel.placeOrder(
                            restaurantId = activeRestaurantId,
                            restaurantName = activeRestaurantName,
                            deliveryFee = deliveryFee,
                            itemsSummary = itemsSummary,
                            totalAmount = grandTotal,
                            waitTimeMinutes = activeRestWaitMin,
                            onFinished = onOrderPlaced
                        )
                    },
                    enabled = selectedPaymentMethod.contains("MoMo") && pinValue.length >= 4 || !selectedPaymentMethod.contains("MoMo")
                ) {
                    Text("Confirm Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinSheet = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (selectedPaymentMethod.contains("MoMo")) Icons.Default.PhoneAndroid else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (selectedPaymentMethod.contains("MoMo")) "Confirm Mobile Money PIN" else "Paystack Gateway Authorize"
                    )
                }
            },
            text = {
                Column {
                    if (selectedPaymentMethod.contains("MoMo")) {
                        Text(
                            text = "A push prompt has been initiated to $momoNumber. Please authorize with your 4-digit mobile money PIN below.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = pinValue,
                            onValueChange = { if (it.length <= 4) pinValue = it },
                            placeholder = { Text("● ● ● ●") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pin_code_modal_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        Text(
                            text = "Please authorize transaction of ₵${String.format("%.2f", grandTotal)} via Paystack 3D-Secure safe gateway.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Secured encrypted channel by Paystack card payment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun CartListItem(
    cartItem: CartItem,
    quantityChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.itemName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "₵${String.format("%.2f", cartItem.price)} each",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { quantityChange(-1) },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Reduce qty", modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        onClick = { quantityChange(1) },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add qty", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "₵${String.format("%.2f", cartItem.price * cartItem.quantity)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DeliveryCard(
    company: DeliveryCompany,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder,
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
            .testTag("delivery_card_${company.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(company.iconEmoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = company.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = company.etaRange,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₵${String.format("%.2f", company.fee)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PaymentSelectorButton(
    name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder,
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
