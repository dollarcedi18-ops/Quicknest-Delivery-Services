package com.example.data

import kotlinx.coroutines.flow.Flow

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String, // "Mains", "Sides", "Drinks", "Desserts"
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Dessert"
    val cuisine: String  // "Ghanaian", "Nigerian", "Italian", "Asian"
)

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val cuisines: List<String>,
    val mealTypes: List<String>,
    val waitingTimeRange: String,
    val waitingTimeInMinutes: Int,
    val rating: Float,
    val ratingCount: Int,
    val bannerColorStart: Long, // Hex color like 0xFFFF5722
    val bannerColorEnd: Long,   // Hex color like 0xFFFF7043
    val iconEmoji: String,
    val menu: List<MenuItem>
)

data class DeliveryCompany(
    val id: String,
    val name: String,
    val fee: Double,
    val etaRange: String,
    val rating: Float,
    val iconEmoji: String
)

class FoodRepository(private val foodDao: FoodDao) {

    // Room operations
    val cartItems: Flow<List<CartItem>> = foodDao.getCartItems()
    val allOrders: Flow<List<OrderEntity>> = foodDao.getAllOrders()

    suspend fun addToCart(cartItem: CartItem) {
        foodDao.insertCartItem(cartItem)
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        foodDao.updateCartItem(cartItem)
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        foodDao.deleteCartItem(cartItem)
    }

    suspend fun clearCart() {
        foodDao.clearCart()
    }

    suspend fun placeOrder(order: OrderEntity): Long {
        return foodDao.insertOrder(order)
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        foodDao.updateOrderStatus(orderId, status)
    }

    suspend fun getOrderById(orderId: Int): OrderEntity? {
        return foodDao.getOrderById(orderId)
    }

    // Static Listings & Filtering
    val deliveryCompanies = listOf(
        DeliveryCompany("bolt", "Bolt Food Moto", 12.0, "15-20 min", 4.8f, "🛵"),
        DeliveryCompany("jumia", "Jumia Express", 15.0, "20-25 min", 4.7f, "📦"),
        DeliveryCompany("glovo", "Glovo Courier", 14.0, "18-22 min", 4.6f, "🚴"),
        DeliveryCompany("swift", "BiteSwift Local Rider", 10.0, "10-15 min", 4.9f, "⚡")
    )

    val cuisinesList = listOf("All", "Ghanaian", "Nigerian", "Italian", "Asian", "Café")
    val mealTypesList = listOf("All", "Breakfast", "Lunch", "Dinner", "Dessert")

    val restaurants = listOf(
        Restaurant(
            id = "chop_bar",
            name = "Chop Bar Express",
            description = "Traditional local soups, freshly pounded fufu, and smoky jollof rice.",
            cuisines = listOf("Ghanaian"),
            mealTypes = listOf("Lunch", "Dinner"),
            waitingTimeRange = "20-25 min",
            waitingTimeInMinutes = 22,
            rating = 4.8f,
            ratingCount = 142,
            bannerColorStart = 0xFFE65100, // Deep Orange
            bannerColorEnd = 0xFFFFB74D,   // Warm Yellow Orange
            iconEmoji = "🍲",
            menu = listOf(
                MenuItem("cb_fufu", "Pounded Fufu & Light Soup", "Fresh fufu served with light soup, chicken, and local spices.", 45.0, "Mains", "Lunch", "Ghanaian"),
                MenuItem("cb_jollof", "Chop Bar Jollof Wood-fire", "Smoky party-style Jollof rice with grilled beef and hot shito sauce.", 35.0, "Mains", "Lunch", "Ghanaian"),
                MenuItem("cb_waakye", "Classic Waakye Bowl", "Brown rice and beans cooked with millet leaves, tallia wele, shito, egg and fish.", 40.0, "Mains", "Breakfast", "Ghanaian"),
                MenuItem("cb_kelewele", "Spicy Kelewele", "Crispy fried plantain cubes marinated in fresh ginger, garlic, and chili.", 15.0, "Sides", "Dinner", "Ghanaian"),
                MenuItem("cb_sobolo", "Iced Sobolo Tea", "Traditional organic hibiscus flower drink infused with ginger and pineapple.", 10.0, "Drinks", "Lunch", "Ghanaian")
            )
        ),
        Restaurant(
            id = "asanka_delight",
            name = "Asanka Local Delight",
            description = "Hot Banku with grilled Tilapia, spicy Kenkey, and authentic local vibes.",
            cuisines = listOf("Ghanaian"),
            mealTypes = listOf("Lunch", "Dinner"),
            waitingTimeRange = "25-35 min",
            waitingTimeInMinutes = 30,
            rating = 4.7f,
            ratingCount = 98,
            bannerColorStart = 0xFF3E2723, // Deep Chocolate
            bannerColorEnd = 0xFF8D6E63,   // Soft Brown
            iconEmoji = "🥣",
            menu = listOf(
                MenuItem("as_banku", "Banku with Grilled Tilapia", "Warm fermented corn/cassava dough with perfectly grilled spiced tilapia & salsa.", 55.0, "Mains", "Dinner", "Ghanaian"),
                MenuItem("as_kenkey", "Ga Kenkey & Fried Fish", "Hot steamed fermented corn dough wrapped in corn husks, served with shito, pepper & crisp fish.", 35.0, "Mains", "Lunch", "Ghanaian"),
                MenuItem("as_redred", "Red Red (Gob3)", "Black-eyed beans stew cooked in red palm oil, served with golden fried plantains.", 30.0, "Mains", "Lunch", "Ghanaian"),
                MenuItem("as_egg", "Boiled Farm Egg", "Fresh farm egg boiled, perfect addon to Red Red or Waakye.", 5.0, "Sides", "Lunch", "Ghanaian"),
                MenuItem("as_water", "Bottled Mineral Water", "Chilled pure refreshing spring water.", 6.0, "Drinks", "Lunch", "Ghanaian")
            )
        ),
        Restaurant(
            id = "aburi_cafe",
            name = "Aburi Heights Cafe",
            description = "Indulgent breakfast pancakes, loaded waffles, hot fresh coffee and Sobolo tea.",
            cuisines = listOf("Café"),
            mealTypes = listOf("Breakfast", "Dessert"),
            waitingTimeRange = "15-20 min",
            waitingTimeInMinutes = 15,
            rating = 4.9f,
            ratingCount = 210,
            bannerColorStart = 0xFF00796B, // Teal
            bannerColorEnd = 0xFF4DB6AC,   // Soft Teal
            iconEmoji = "☕",
            menu = listOf(
                MenuItem("ab_pancakes", "Golden Butter Pancakes", "Fluffy hot pancakes served with organic local honey & whipped cream.", 32.0, "Mains", "Breakfast", "Café"),
                MenuItem("ab_waffles", "Aburi Berry Waffles", "Belgian waffles topped with mixed berries, vanilla ice cream & strawberry syrup.", 38.0, "Desserts", "Dessert", "Café"),
                MenuItem("ab_latte", "House Spiced Latte", "Freshly brewed espresso with steamed milk, cinnamon and secret spices.", 18.0, "Drinks", "Breakfast", "Café"),
                MenuItem("ab_avotoast", "Creamy Avocado Sourdough", "Toasted artisanal sourdough bread topped with crushed avocado, egg and spices.", 28.0, "Mains", "Breakfast", "Café"),
                MenuItem("ab_muffin", "Double Chocolate Muffin", "Warm rich chocolate muffin with liquid chocolate cream core.", 12.0, "Desserts", "Dessert", "Café")
            )
        ),
        Restaurant(
            id = "mama_africa",
            name = "Mama Africa Kitchen",
            description = "Fluffy pounded yam with rich Egusi soup, grilled suiyas, and sweet puff puff.",
            cuisines = listOf("Nigerian"),
            mealTypes = listOf("Lunch", "Dinner"),
            waitingTimeRange = "30-45 min",
            waitingTimeInMinutes = 35,
            rating = 4.6f,
            ratingCount = 85,
            bannerColorStart = 0xFF1B5E20, // Forest Green
            bannerColorEnd = 0xFF81C784,   // Grass Green
            iconEmoji = "🥘",
            menu = listOf(
                MenuItem("ma_egusi", "Pounded Yam with Egusi Soup", "Egusi stew enriched with melon seeds, spinach, assorted fish & beef, with fluffy pounded yam.", 60.0, "Mains", "Dinner", "Nigerian"),
                MenuItem("ma_suya", "Assorted Beef Suya", "Thinly sliced beef charcoal grilled with spicy Yaji peanut dry seasoning & raw onions.", 30.0, "Mains", "Dinner", "Nigerian"),
                MenuItem("ma_puff", "Sweet Golden Puff Puff", "Light, fluffy, sweet fried dough balls (local dessert classic).", 10.0, "Desserts", "Dessert", "Nigerian"),
                MenuItem("ma_malt", "Chilled Malta Guinness", "Rich nutritious sweet carbonated malt drink.", 12.0, "Drinks", "Lunch", "Nigerian")
            )
        ),
        Restaurant(
            id = "bella_italia",
            name = "Bella Italia Bistro",
            description = "Wood-fired artisanal Pizza, rich Alfredo pastas, and sweet Gelato cups.",
            cuisines = listOf("Italian"),
            mealTypes = listOf("Lunch", "Dinner"),
            waitingTimeRange = "25-35 min",
            waitingTimeInMinutes = 28,
            rating = 4.5f,
            ratingCount = 114,
            bannerColorStart = 0xFFC2185B, // Rose Red
            bannerColorEnd = 0xFFF06292,   // Pastel Pink
            iconEmoji = "🍕",
            menu = listOf(
                MenuItem("bi_margherita", "Artisanal Margherita Pizza", "Thin crust, sweet San Marzano tomato sauce, fresh mozzarella & fresh basil leaves.", 48.0, "Mains", "Dinner", "Italian"),
                MenuItem("bi_alfredo", "Fettuccine Alfredo with Chicken", "Creamy white parmesan sauce with tender grilled chicken and parsley.", 45.0, "Mains", "Lunch", "Italian"),
                MenuItem("bi_garlic", "Cheesy Garlic Breadsticks", "Warm oven baked baguette slathered in garlic butter & melted mozzarella.", 18.0, "Sides", "Lunch", "Italian"),
                MenuItem("bi_gelato", "Double Vanilla Gelato", "Creamy premium Italian ice cream served in a crisp chocolate cup.", 20.0, "Desserts", "Dessert", "Italian"),
                MenuItem("bi_cola", "Chilled Craft Soda", "Gourmet ginger-infused carbonated cola.", 10.0, "Drinks", "Lunch", "Italian")
            )
        ),
        Restaurant(
            id = "wok_roll",
            name = "Wok & Roll Bistro",
            description = "Hot sizzled noodles, spicy egg rolls, sweet and sour chickens, and bubble tea.",
            cuisines = listOf("Asian"),
            mealTypes = listOf("Lunch", "Dinner"),
            waitingTimeRange = "15-25 min",
            waitingTimeInMinutes = 18,
            rating = 4.7f,
            ratingCount = 129,
            bannerColorStart = 0xFF0D47A1, // Deep Blue
            bannerColorEnd = 0xFF64B5F6,   // Soft Blue
            iconEmoji = "🥢",
            menu = listOf(
                MenuItem("wr_noodles", "Sizzling Szechuan Noodles", "Stir-fried noodles with fresh vegetables, dry chilies, beef, and soy glaze.", 42.0, "Mains", "Dinner", "Asian"),
                MenuItem("wr_rice", "House Special Fried Rice", "Wok-fried premium rice loaded with farm egg, shrimp, chicken, and spring onions.", 44.0, "Mains", "Lunch", "Asian"),
                MenuItem("wr_spring", "Crispy Spring Rolls (3pcs)", "Golden crisp spring rolls stuffed with seasoned julienned veggies & sesame glaze.", 15.0, "Sides", "Lunch", "Asian"),
                MenuItem("wr_boba", "Taro Milk Bubble Tea", "Rich creamy sweet purple tea served with soft chewable brown sugar tapioca boba.", 22.0, "Drinks", "Lunch", "Asian")
            )
        )
    )
}
