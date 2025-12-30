package com.example.luminacal.data.ml

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for FoodNutritionDatabase
 */
class FoodNutritionDatabaseTest {

    // ==================== LOOKUP TESTS ====================

    @Test
    fun `lookup returns nutrition info for exact match`() {
        val result = FoodNutritionDatabase.lookup("Nasi Goreng")
        
        assertNotNull(result)
        assertEquals("Nasi Goreng", result?.name)
        assertTrue(result?.calories ?: 0 > 0)
    }

    @Test
    fun `lookup is case insensitive`() {
        val lowerCase = FoodNutritionDatabase.lookup("nasi goreng")
        val upperCase = FoodNutritionDatabase.lookup("NASI GORENG")
        val mixedCase = FoodNutritionDatabase.lookup("Nasi GORENG")
        
        assertNotNull(lowerCase)
        assertNotNull(upperCase)
        assertNotNull(mixedCase)
        assertEquals(lowerCase?.calories, upperCase?.calories)
        assertEquals(lowerCase?.calories, mixedCase?.calories)
    }

    @Test
    fun `lookup returns null for unknown food`() {
        val result = FoodNutritionDatabase.lookup("Unknown Food XYZ")
        assertNull(result)
    }

    @Test
    fun `lookup returns correct macros for known food`() {
        val result = FoodNutritionDatabase.lookup("Rendang")
        
        assertNotNull(result)
        assertTrue(result?.protein ?: 0 > 0)
        assertTrue(result?.carbs ?: 0 >= 0)
        assertTrue(result?.fat ?: 0 > 0)
    }

    @Test
    fun `lookup returns serving size for food`() {
        val result = FoodNutritionDatabase.lookup("Nasi Putih")
        
        assertNotNull(result)
        assertNotNull(result?.servingSize)
        assertTrue(result?.servingSize?.isNotEmpty() == true)
    }

    // ==================== SEARCH TESTS ====================

    @Test
    fun `search returns results for partial match`() {
        val results = FoodNutritionDatabase.search("Nasi")
        
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.name.contains("Nasi", ignoreCase = true) })
    }

    @Test
    fun `search is case insensitive`() {
        val lowerResults = FoodNutritionDatabase.search("ayam")
        val upperResults = FoodNutritionDatabase.search("AYAM")
        
        assertEquals(lowerResults.size, upperResults.size)
    }

    @Test
    fun `search returns empty list for blank query`() {
        val results = FoodNutritionDatabase.search("")
        assertTrue(results.isEmpty())
        
        val whitespaceResults = FoodNutritionDatabase.search("   ")
        assertTrue(whitespaceResults.isEmpty())
    }

    @Test
    fun `search returns empty list for no matches`() {
        val results = FoodNutritionDatabase.search("xyznonexistent123")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `search results are sorted by name`() {
        val results = FoodNutritionDatabase.search("a")
        
        if (results.size > 1) {
            for (i in 0 until results.size - 1) {
                assertTrue(results[i].name <= results[i + 1].name)
            }
        }
    }

    @Test
    fun `search finds Indonesian foods`() {
        val results = FoodNutritionDatabase.search("Goreng")
        
        assertTrue(results.isNotEmpty())
        // Should find Nasi Goreng, Mie Goreng, Ayam Goreng, etc.
        assertTrue(results.size >= 3)
    }

    // ==================== GET BY CATEGORY TESTS ====================

    @Test
    fun `getByCategory returns foods for valid category`() {
        val results = FoodNutritionDatabase.getByCategory("Food")
        
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `getByCategory returns drinks for Drink category`() {
        val results = FoodNutritionDatabase.getByCategory("Drink")
        
        assertTrue(results.isNotEmpty())
        // Es Teh, Kopi, etc.
    }

    @Test
    fun `getByCategory returns fruits for Fruit category`() {
        val results = FoodNutritionDatabase.getByCategory("Fruit")
        
        assertTrue(results.isNotEmpty())
        // Pisang, Mangga, etc.
    }

    @Test
    fun `getByCategory returns vegetables for Plant category`() {
        val results = FoodNutritionDatabase.getByCategory("Plant")
        
        assertTrue(results.isNotEmpty())
        // Sayur Asem, Capcay, etc.
    }

    @Test
    fun `getByCategory returns empty list for invalid category`() {
        val results = FoodNutritionDatabase.getByCategory("InvalidCategory")
        
        assertTrue(results.isEmpty())
    }

    // ==================== GET ALL FOOD NAMES TESTS ====================

    @Test
    fun `getAllFoodNames returns non-empty list`() {
        val names = FoodNutritionDatabase.getAllFoodNames()
        
        assertTrue(names.isNotEmpty())
    }

    @Test
    fun `getAllFoodNames returns sorted list`() {
        val names = FoodNutritionDatabase.getAllFoodNames()
        
        for (i in 0 until names.size - 1) {
            assertTrue(names[i] <= names[i + 1])
        }
    }

    @Test
    fun `getAllFoodNames contains Indonesian foods`() {
        val names = FoodNutritionDatabase.getAllFoodNames()
        
        assertTrue(names.contains("Nasi Goreng"))
        assertTrue(names.contains("Rendang"))
        assertTrue(names.contains("Sate Ayam"))
    }

    @Test
    fun `getAllFoodNames contains international foods`() {
        val names = FoodNutritionDatabase.getAllFoodNames()
        
        assertTrue(names.contains("Burger"))
        assertTrue(names.contains("Pizza"))
        assertTrue(names.contains("Pasta"))
    }

    // ==================== NUTRITION INFO INTEGRITY TESTS ====================

    @Test
    fun `all foods have positive calories`() {
        val names = FoodNutritionDatabase.getAllFoodNames()
        
        names.forEach { name ->
            val info = FoodNutritionDatabase.lookup(name)
            assertNotNull("Missing nutrition info for: $name", info)
            assertTrue("$name should have positive calories", info!!.calories > 0)
        }
    }

    @Test
    fun `all foods have non-negative macros`() {
        val names = FoodNutritionDatabase.getAllFoodNames()
        
        names.forEach { name ->
            val info = FoodNutritionDatabase.lookup(name)
            assertNotNull(info)
            assertTrue("$name protein should be >= 0", info!!.protein >= 0)
            assertTrue("$name carbs should be >= 0", info.carbs >= 0)
            assertTrue("$name fat should be >= 0", info.fat >= 0)
        }
    }

    @Test
    fun `database has reasonable number of entries`() {
        val names = FoodNutritionDatabase.getAllFoodNames()
        
        // Should have at least 50 foods
        assertTrue("Database should have at least 50 foods", names.size >= 50)
    }

    // ==================== SPECIFIC FOOD TESTS ====================

    @Test
    fun `Nasi Goreng has expected values`() {
        val nasiGoreng = FoodNutritionDatabase.lookup("Nasi Goreng")
        
        assertNotNull(nasiGoreng)
        assertEquals("Nasi Goreng", nasiGoreng?.name)
        assertEquals(580, nasiGoreng?.calories)
        assertEquals(18, nasiGoreng?.protein)
        assertEquals(72, nasiGoreng?.carbs)
        assertEquals(24, nasiGoreng?.fat)
        assertEquals("1 piring", nasiGoreng?.servingSize)
    }

    @Test
    fun `Rendang has expected protein content`() {
        val rendang = FoodNutritionDatabase.lookup("Rendang")
        
        assertNotNull(rendang)
        // Rendang is high protein
        assertTrue(rendang!!.protein >= 20)
    }

    @Test
    fun `Es Teh has low calories`() {
        val esTeh = FoodNutritionDatabase.lookup("Es Teh")
        
        assertNotNull(esTeh)
        // Sweet iced tea should have moderate calories
        assertTrue(esTeh!!.calories < 200)
    }

    @Test
    fun `Pisang has expected fruit characteristics`() {
        val pisang = FoodNutritionDatabase.lookup("Pisang")
        
        assertNotNull(pisang)
        // Banana is high carb, low fat, low protein
        assertTrue(pisang!!.carbs > pisang.protein)
        assertTrue(pisang.carbs > pisang.fat)
    }
}
