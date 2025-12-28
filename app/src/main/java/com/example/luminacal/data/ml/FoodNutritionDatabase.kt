package com.example.luminacal.data.ml

/**
 * Nutrition information for a food item
 */
data class NutritionInfo(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val servingSize: String = "1 serving",
    val imageUrl: String? = null
)

/**
 * Database of Indonesian and common food nutrition information
 */
object FoodNutritionDatabase {
    
    private val database: Map<String, NutritionInfo> = mapOf(
        // === INDONESIAN RICE DISHES ===
        "Nasi Goreng" to NutritionInfo("Nasi Goreng", 580, 18, 72, 24, "1 piring"),
        "Nasi Putih" to NutritionInfo("Nasi Putih", 204, 4, 44, 0, "1 piring"),
        "Nasi Uduk" to NutritionInfo("Nasi Uduk", 480, 12, 68, 18, "1 piring"),
        "Nasi Kuning" to NutritionInfo("Nasi Kuning", 450, 10, 65, 16, "1 piring"),
        "Nasi Padang" to NutritionInfo("Nasi Padang", 750, 35, 80, 32, "1 porsi"),
        "Bubur Ayam" to NutritionInfo("Bubur Ayam", 350, 18, 45, 12, "1 mangkok"),
        "Lontong Sayur" to NutritionInfo("Lontong Sayur", 470, 15, 58, 20, "1 porsi"),
        
        // === INDONESIAN MEAT DISHES ===
        "Rendang" to NutritionInfo("Rendang", 350, 28, 8, 24, "100g"),
        "Sate Ayam" to NutritionInfo("Sate Ayam", 420, 32, 12, 28, "10 tusuk"),
        "Ayam Goreng" to NutritionInfo("Ayam Goreng", 280, 24, 8, 18, "1 potong"),
        "Ayam Geprek" to NutritionInfo("Ayam Geprek", 620, 28, 45, 36, "1 porsi"),
        "Ayam Bakar" to NutritionInfo("Ayam Bakar", 350, 32, 8, 22, "1 potong"),
        "Opor Ayam" to NutritionInfo("Opor Ayam", 420, 25, 12, 32, "1 potong"),
        "Bebek Goreng" to NutritionInfo("Bebek Goreng", 480, 28, 10, 38, "1 potong"),
        "Ikan Bakar" to NutritionInfo("Ikan Bakar", 340, 35, 5, 20, "1 ekor"),
        "Ikan Goreng" to NutritionInfo("Ikan Goreng", 380, 32, 8, 25, "1 ekor"),
        
        // === INDONESIAN SOUPS ===
        "Soto Ayam" to NutritionInfo("Soto Ayam", 280, 20, 25, 12, "1 mangkok"),
        "Soto Betawi" to NutritionInfo("Soto Betawi", 420, 22, 28, 26, "1 mangkok"),
        "Bakso" to NutritionInfo("Bakso", 480, 22, 48, 20, "1 mangkok"),
        "Rawon" to NutritionInfo("Rawon", 390, 28, 32, 18, "1 mangkok"),
        "Sop Buntut" to NutritionInfo("Sop Buntut", 550, 35, 25, 35, "1 mangkok"),
        
        // === INDONESIAN NOODLES ===
        "Mie Goreng" to NutritionInfo("Mie Goreng", 510, 15, 62, 22, "1 piring"),
        "Mie Ayam" to NutritionInfo("Mie Ayam", 450, 18, 55, 18, "1 mangkok"),
        "Indomie Goreng" to NutritionInfo("Indomie Goreng", 380, 8, 52, 16, "1 bungkus"),
        "Indomie Kuah" to NutritionInfo("Indomie Kuah", 310, 7, 48, 11, "1 bungkus"),
        "Kwetiau Goreng" to NutritionInfo("Kwetiau Goreng", 480, 14, 58, 20, "1 piring"),
        
        // === INDONESIAN VEGETABLES ===
        "Gado-Gado" to NutritionInfo("Gado-Gado", 310, 12, 28, 18, "1 porsi"),
        "Sayur Asem" to NutritionInfo("Sayur Asem", 95, 4, 18, 2, "1 mangkok"),
        "Sayur Lodeh" to NutritionInfo("Sayur Lodeh", 180, 6, 22, 8, "1 mangkok"),
        "Capcay" to NutritionInfo("Capcay", 220, 12, 18, 12, "1 porsi"),
        "Tumis Kangkung" to NutritionInfo("Tumis Kangkung", 120, 4, 8, 8, "1 porsi"),
        "Pecel" to NutritionInfo("Pecel", 280, 10, 25, 16, "1 porsi"),
        "Urap" to NutritionInfo("Urap", 180, 6, 22, 8, "1 porsi"),
        "Lalapan" to NutritionInfo("Lalapan", 50, 2, 10, 0, "1 porsi"),
        
        // === INDONESIAN TOFU/TEMPE ===
        "Tempe Goreng" to NutritionInfo("Tempe Goreng", 180, 12, 10, 12, "3 potong"),
        "Tahu Goreng" to NutritionInfo("Tahu Goreng", 150, 10, 8, 10, "3 potong"),
        "Tahu Isi" to NutritionInfo("Tahu Isi", 220, 12, 18, 12, "3 potong"),
        "Tempe Mendoan" to NutritionInfo("Tempe Mendoan", 200, 10, 15, 12, "3 potong"),
        "Perkedel" to NutritionInfo("Perkedel", 180, 6, 20, 9, "2 buah"),
        
        // === INDONESIAN SNACKS ===
        "Gorengan" to NutritionInfo("Gorengan", 250, 5, 25, 15, "3 buah"),
        "Martabak Manis" to NutritionInfo("Martabak Manis", 450, 8, 55, 22, "1 potong"),
        "Martabak Telur" to NutritionInfo("Martabak Telur", 380, 18, 32, 20, "1 potong"),
        "Risol" to NutritionInfo("Risol", 180, 6, 18, 10, "2 buah"),
        "Pisang Goreng" to NutritionInfo("Pisang Goreng", 220, 3, 32, 10, "2 buah"),
        "Pempek" to NutritionInfo("Pempek", 380, 15, 48, 14, "4 buah"),
        "Ketoprak" to NutritionInfo("Ketoprak", 410, 14, 52, 16, "1 porsi"),
        
        // === INDONESIAN DRINKS ===
        "Es Teh" to NutritionInfo("Es Teh", 80, 0, 20, 0, "1 gelas"),
        "Es Jeruk" to NutritionInfo("Es Jeruk", 120, 1, 28, 0, "1 gelas"),
        "Kopi" to NutritionInfo("Kopi", 40, 0, 8, 0, "1 gelas"),
        "Jus Alpukat" to NutritionInfo("Jus Alpukat", 350, 4, 35, 22, "1 gelas"),
        "Es Campur" to NutritionInfo("Es Campur", 280, 4, 52, 8, "1 porsi"),
        "Cendol" to NutritionInfo("Cendol", 320, 3, 58, 10, "1 porsi"),
        "Teh Tarik" to NutritionInfo("Teh Tarik", 150, 3, 28, 4, "1 gelas"),
        "Susu" to NutritionInfo("Susu", 150, 8, 12, 8, "1 gelas"),
        
        // === FRUITS ===
        "Pisang" to NutritionInfo("Pisang", 105, 1, 27, 0, "1 buah"),
        "Mangga" to NutritionInfo("Mangga", 135, 1, 35, 0, "1 buah"),
        "Jeruk" to NutritionInfo("Jeruk", 62, 1, 15, 0, "1 buah"),
        "Apel" to NutritionInfo("Apel", 95, 0, 25, 0, "1 buah"),
        "Semangka" to NutritionInfo("Semangka", 86, 2, 22, 0, "1 potong"),
        "Pepaya" to NutritionInfo("Pepaya", 120, 2, 30, 0, "1 mangkok"),
        "Nanas" to NutritionInfo("Nanas", 82, 1, 22, 0, "1 mangkok"),
        "Durian" to NutritionInfo("Durian", 357, 4, 66, 13, "1 biji"),
        
        // === COMMON INTERNATIONAL FOODS ===
        "Avocado Toast" to NutritionInfo("Avocado Toast", 340, 8, 32, 22, "1 serving"),
        "Egg Omelet" to NutritionInfo("Egg Omelet", 220, 14, 2, 18, "2 eggs"),
        "Pancakes" to NutritionInfo("Pancakes", 410, 8, 52, 18, "3 pieces"),
        "Salad" to NutritionInfo("Salad", 150, 4, 12, 10, "1 bowl"),
        "Burger" to NutritionInfo("Burger", 550, 25, 45, 32, "1 piece"),
        "Pizza" to NutritionInfo("Pizza", 285, 12, 36, 10, "1 slice"),
        "Pasta" to NutritionInfo("Pasta", 420, 15, 58, 14, "1 plate"),
        "Fried Rice" to NutritionInfo("Fried Rice", 510, 12, 68, 20, "1 plate"),
        "Fried Chicken" to NutritionInfo("Fried Chicken", 320, 28, 12, 18, "1 piece")
    )
    
    /**
     * Lookup nutrition info by exact food name (case-insensitive)
     */
    fun lookup(foodName: String): NutritionInfo? {
        return database.entries.find { 
            it.key.equals(foodName, ignoreCase = true) 
        }?.value
    }
    
    /**
     * Search nutrition database by partial name match
     */
    fun search(query: String): List<NutritionInfo> {
        if (query.isBlank()) return emptyList()
        return database.values.filter { 
            it.name.contains(query, ignoreCase = true) 
        }.sortedBy { it.name }
    }
    
    /**
     * Get suggestions based on category
     */
    fun getByCategory(category: String): List<NutritionInfo> {
        val categoryMap = mapOf(
            "Food" to listOf("Nasi Goreng", "Rendang", "Sate Ayam", "Gado-Gado", "Bakso", "Mie Goreng"),
            "Drink" to listOf("Es Teh", "Es Jeruk", "Kopi", "Jus Alpukat", "Es Campur", "Cendol"),
            "Fruit" to listOf("Pisang", "Mangga", "Jeruk", "Apel", "Semangka", "Pepaya"),
            "Plant" to listOf("Sayur Asem", "Sayur Lodeh", "Capcay", "Tumis Kangkung", "Pecel", "Urap")
        )
        
        val foodNames = categoryMap[category] ?: return emptyList()
        return foodNames.mapNotNull { lookup(it) }
    }
    
    /**
     * Get all food names
     */
    fun getAllFoodNames(): List<String> = database.keys.toList().sorted()
}
