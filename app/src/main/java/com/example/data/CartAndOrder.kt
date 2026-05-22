package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantId: String,
    val restaurantName: String,
    val itemId: String,
    val itemName: String,
    val price: Double,
    val quantity: Int,
    val itemImage: String // abstract visual category keyword (e.g. "jollof", "fufu", "pizza")
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantId: String,
    val restaurantName: String,
    val deliveryCompany: String,
    val deliveryFee: Double,
    val paymentMethod: String,
    val phoneNumber: String,
    val totalAmount: Double,
    val waitTimeMinutes: Int,
    val status: String, // "Confirmed", "Preparing", "Out for Delivery", "Delivered"
    val itemsSummary: String, // Stringified list of items (e.g. "2x Jollof Rice, 1x Sobolo")
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface FoodDao {
    // Cart operations
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Order operations
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?
}

@Database(entities = [CartItem::class, OrderEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val foodDao: FoodDao
}
