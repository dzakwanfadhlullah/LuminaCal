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
 * All image URLs verified and working as of Jan 2026
 */
object FoodNutritionDatabase {
    
    private val database: Map<String, NutritionInfo> = mapOf(
        // === INDONESIAN RICE DISHES ===
        "Nasi Goreng" to NutritionInfo("Nasi Goreng", 575, 18, 72, 24, "1 piring", "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800"),
        "Nasi Putih" to NutritionInfo("Nasi Putih", 200, 4, 44, 1, "1 piring", "https://images.unsplash.com/photo-1516684732162-798a0062be99?w=800"),
        "Nasi Uduk" to NutritionInfo("Nasi Uduk", 490, 12, 70, 19, "1 piring", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=800"),
        "Nasi Kuning" to NutritionInfo("Nasi Kuning", 450, 11, 65, 17, "1 piring", "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=800"),
        "Nasi Padang" to NutritionInfo("Nasi Padang", 750, 40, 77, 35, "1 porsi", "https://images.unsplash.com/photo-1562565652-a0d8f0c59eb4?w=800"),
        "Bubur Ayam" to NutritionInfo("Bubur Ayam", 370, 18, 47, 12, "1 mangkok", "https://images.unsplash.com/photo-1569058242567-93de6f36f8e6?w=800"),
        "Lontong Sayur" to NutritionInfo("Lontong Sayur", 460, 16, 60, 21, "1 porsi", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800"),
        
        // === INDONESIAN MEAT DISHES ===
        "Rendang" to NutritionInfo("Rendang", 255, 25, 8, 19, "100g", "https://images.unsplash.com/photo-1562565651-7d4948f339eb?w=800"),
        "Sate Ayam" to NutritionInfo("Sate Ayam", 375, 32, 15, 25, "10 tusuk", "https://images.unsplash.com/photo-1529563021893-cc83c992d75d?w=800"),
        "Ayam Goreng" to NutritionInfo("Ayam Goreng", 290, 24, 7, 18, "1 potong", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800"),
        "Ayam Geprek" to NutritionInfo("Ayam Geprek", 560, 30, 42, 31, "1 porsi", "https://images.unsplash.com/photo-1606728035253-49e8a23146de?w=800"),
        "Ayam Bakar" to NutritionInfo("Ayam Bakar", 330, 32, 8, 20, "1 potong", "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=800"),
        "Opor Ayam" to NutritionInfo("Opor Ayam", 420, 26, 12, 28, "1 potong", "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=800"),
        "Bebek Goreng" to NutritionInfo("Bebek Goreng", 490, 30, 9, 37, "1 potong", "https://images.unsplash.com/photo-1619221882266-2e12d9d7e9e8?w=800"),
        "Ikan Bakar" to NutritionInfo("Ikan Bakar", 320, 33, 6, 19, "1 ekor", "https://images.unsplash.com/photo-1580476262798-bddd9f4b7369?w=800"),
        "Ikan Goreng" to NutritionInfo("Ikan Goreng", 370, 29, 8, 23, "1 ekor", "https://images.unsplash.com/photo-1619221881375-c4d54f27fbbb?w=800"),
        
        // === INDONESIAN SOUPS ===
        "Soto Ayam" to NutritionInfo("Soto Ayam", 285, 22, 30, 12, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Soto Betawi" to NutritionInfo("Soto Betawi", 430, 24, 30, 29, "1 mangkok", "https://images.unsplash.com/photo-1583224964978-2257b960c3d3?w=800"),
        "Bakso" to NutritionInfo("Bakso", 450, 25, 50, 18, "1 mangkok", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800"),
        "Rawon" to NutritionInfo("Rawon", 370, 30, 32, 17, "1 mangkok", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Sop Buntut" to NutritionInfo("Sop Buntut", 565, 35, 24, 34, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        
        // === INDONESIAN NOODLES ===
        "Mie Goreng" to NutritionInfo("Mie Goreng", 510, 16, 65, 21, "1 piring", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800"),
        "Mie Ayam" to NutritionInfo("Mie Ayam", 430, 20, 55, 18, "1 mangkok", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800"),
        "Indomie Goreng" to NutritionInfo("Indomie Goreng", 390, 8, 55, 16, "1 bungkus", "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=800"),
        "Indomie Kuah" to NutritionInfo("Indomie Kuah", 330, 7, 52, 11, "1 bungkus", "https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=800"),
        "Kwetiau Goreng" to NutritionInfo("Kwetiau Goreng", 490, 15, 60, 21, "1 piring", "https://images.unsplash.com/photo-1555126634-323283e090fa?w=800"),
        
        // === INDONESIAN VEGETABLES ===
        "Gado-Gado" to NutritionInfo("Gado-Gado", 320, 13, 28, 21, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Sayur Asem" to NutritionInfo("Sayur Asem", 90, 4, 17, 2, "1 mangkok", "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=800"),
        "Sayur Lodeh" to NutritionInfo("Sayur Lodeh", 200, 6, 23, 10, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Capcay" to NutritionInfo("Capcay", 220, 12, 20, 12, "1 porsi", "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800"),
        "Tumis Kangkung" to NutritionInfo("Tumis Kangkung", 120, 4, 9, 9, "1 porsi", "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=800"),
        "Pecel" to NutritionInfo("Pecel", 280, 11, 26, 16, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Urap" to NutritionInfo("Urap", 190, 6, 22, 9, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Lalapan" to NutritionInfo("Lalapan", 60, 2, 11, 0, "1 porsi", "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=800"),
        
        // === INDONESIAN TOFU/TEMPE ===
        "Tempe Goreng" to NutritionInfo("Tempe Goreng", 200, 14, 11, 13, "3 potong", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Tahu Goreng" to NutritionInfo("Tahu Goreng", 165, 10, 9, 11, "3 potong", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800"),
        "Tahu Isi" to NutritionInfo("Tahu Isi", 230, 12, 21, 13, "3 potong", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800"),
        "Tempe Mendoan" to NutritionInfo("Tempe Mendoan", 215, 11, 18, 14, "3 potong", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Perkedel" to NutritionInfo("Perkedel", 190, 7, 22, 11, "2 buah", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
        
        // === INDONESIAN SNACKS ===
        "Gorengan" to NutritionInfo("Gorengan", 275, 6, 28, 18, "3 buah", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Martabak Manis" to NutritionInfo("Martabak Manis", 525, 10, 70, 27, "1 potong", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
        "Martabak Telur" to NutritionInfo("Martabak Telur", 400, 20, 34, 24, "1 potong", "https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=800"),
        "Risol" to NutritionInfo("Risol", 190, 6, 21, 11, "2 buah", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800"),
        "Pisang Goreng" to NutritionInfo("Pisang Goreng", 250, 3, 35, 11, "2 buah", "https://images.unsplash.com/photo-1528751014936-863e6e7a319c?w=800"),
        "Pempek" to NutritionInfo("Pempek", 395, 16, 50, 14, "4 buah", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800"),
        "Ketoprak" to NutritionInfo("Ketoprak", 420, 15, 55, 17, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Bakwan" to NutritionInfo("Bakwan", 125, 3, 14, 9, "2 buah", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Tahu Sumedang" to NutritionInfo("Tahu Sumedang", 190, 10, 15, 13, "4 buah", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800"),
        "Tempe Kemul" to NutritionInfo("Tempe Kemul", 170, 10, 15, 11, "3 buah", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Cireng" to NutritionInfo("Cireng", 180, 3, 31, 9, "4 buah", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Combro" to NutritionInfo("Combro", 220, 6, 34, 12, "2 buah", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Misro" to NutritionInfo("Misro", 200, 3, 38, 9, "2 buah", "https://images.unsplash.com/photo-1528751014936-863e6e7a319c?w=800"),
        "Gehu" to NutritionInfo("Gehu", 150, 8, 14, 9, "3 buah", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800"),
        "Ote-ote" to NutritionInfo("Ote-ote", 150, 6, 19, 9, "3 buah", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Bala-bala" to NutritionInfo("Bala-bala", 120, 3, 15, 7, "3 buah", "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=800"),
        "Sukun Goreng" to NutritionInfo("Sukun Goreng", 180, 3, 34, 7, "3 potong", "https://images.unsplash.com/photo-1528751014936-863e6e7a319c?w=800"),
        
        "Martabak Telor Spesial" to NutritionInfo("Martabak Telor Spesial", 620, 29, 44, 36, "1 porsi", "https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=800"),
        "Martabak Manis Coklat" to NutritionInfo("Martabak Manis Coklat", 520, 10, 68, 27, "1 potong", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
        "Martabak Manis Keju" to NutritionInfo("Martabak Manis Keju", 480, 12, 58, 24, "1 potong", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
        "Lumpia Semarang" to NutritionInfo("Lumpia Semarang", 220, 8, 28, 11, "2 buah", "https://images.unsplash.com/photo-1544025162-d76694265947?w=800"),
        "Pastel" to NutritionInfo("Pastel", 180, 6, 23, 9, "2 buah", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800"),
        "Kue Cubit" to NutritionInfo("Kue Cubit", 130, 3, 19, 6, "6 buah", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
        "Serabi" to NutritionInfo("Serabi", 180, 6, 32, 7, "3 buah", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
        "Terang Bulan" to NutritionInfo("Terang Bulan", 520, 11, 74, 27, "1 potong", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
        
        // === INDONESIAN DRINKS ===
        "Es Teh" to NutritionInfo("Es Teh", 80, 0, 18, 0, "1 gelas", "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800"),
        "Es Jeruk" to NutritionInfo("Es Jeruk", 125, 0, 29, 0, "1 gelas", "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=800"),
        "Kopi" to NutritionInfo("Kopi", 50, 0, 8, 1, "1 gelas", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"),
        "Jus Alpukat" to NutritionInfo("Jus Alpukat", 340, 4, 35, 23, "1 gelas", "https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=800"),
        "Es Campur" to NutritionInfo("Es Campur", 290, 4, 50, 9, "1 porsi", "https://images.unsplash.com/photo-1541658016709-82535e94bc69?w=800"),
        "Cendol" to NutritionInfo("Cendol", 320, 4, 60, 11, "1 porsi", "https://images.unsplash.com/photo-1541658016709-82535e94bc69?w=800"),
        "Teh Tarik" to NutritionInfo("Teh Tarik", 150, 3, 28, 6, "1 gelas", "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800"),
        "Susu" to NutritionInfo("Susu", 160, 8, 11, 8, "1 gelas", "https://images.unsplash.com/photo-1563636619-e9143da7973b?w=800"),
        
        // === FRUITS ===
        "Pisang" to NutritionInfo("Pisang", 100, 1, 27, 0, "1 buah", "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=800"),
        "Mangga" to NutritionInfo("Mangga", 145, 1, 35, 0, "1 buah", "https://images.unsplash.com/photo-1553279768-865429fa0078?w=800"),
        "Jeruk" to NutritionInfo("Jeruk", 70, 1, 15, 0, "1 buah", "https://images.unsplash.com/photo-1582979512210-99b6a53386f9?w=800"),
        "Apel" to NutritionInfo("Apel", 90, 0, 24, 0, "1 buah", "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=800"),
        "Semangka" to NutritionInfo("Semangka", 90, 1, 22, 0, "1 potong", "https://images.unsplash.com/photo-1563114773-84221bd62daa?w=800"),
        "Pepaya" to NutritionInfo("Pepaya", 120, 3, 30, 0, "1 mangkok", "https://images.unsplash.com/photo-1517282009859-f000ec3b26fe?w=800"),
        "Nanas" to NutritionInfo("Nanas", 85, 1, 22, 0, "1 mangkok", "https://images.unsplash.com/photo-1550258987-190a2d41a8ba?w=800"),
        "Durian" to NutritionInfo("Durian", 350, 4, 70, 14, "1 biji", "https://images.unsplash.com/photo-1588117472013-59bb13edafec?w=800"),
        
        // === COMMON INTERNATIONAL FOODS ===
        "Avocado Toast" to NutritionInfo("Avocado Toast", 340, 8, 35, 22, "1 serving", "https://images.unsplash.com/photo-1541519227354-08fa5d50c44d?w=800"),
        "Egg Omelet" to NutritionInfo("Egg Omelet", 220, 15, 4, 17, "2 eggs", "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800"),
        "Pancakes" to NutritionInfo("Pancakes", 420, 8, 51, 18, "3 pieces", "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=800"),
        "Salad" to NutritionInfo("Salad", 170, 5, 17, 12, "1 bowl", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Burger" to NutritionInfo("Burger", 575, 30, 45, 29, "1 piece", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
        "Pizza" to NutritionInfo("Pizza", 305, 12, 35, 14, "1 slice", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
        "Pasta" to NutritionInfo("Pasta", 510, 18, 65, 22, "1 plate", "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=800"),
        "Fried Rice" to NutritionInfo("Fried Rice", 520, 14, 75, 21, "1 plate", "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800"),
        "Fried Chicken" to NutritionInfo("Fried Chicken", 335, 24, 14, 20, "1 piece", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800"),
        
        // === REGIONAL SPECIALTIES - ACEH & SUMATERA ===
        "Mie Aceh" to NutritionInfo("Mie Aceh", 530, 20, 66, 28, "1 piring", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800"),
        "Kuah Pliek U" to NutritionInfo("Kuah Pliek U", 290, 15, 24, 15, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Sate Padang" to NutritionInfo("Sate Padang", 490, 30, 22, 37, "10 tusuk", "https://images.unsplash.com/photo-1529563021893-cc83c992d75d?w=800"),
        "Gulai Kepala Ikan" to NutritionInfo("Gulai Kepala Ikan", 420, 32, 16, 27, "1 porsi", "https://images.unsplash.com/photo-1580476262798-bddd9f4b7369?w=800"),
        "Dendeng Batokok" to NutritionInfo("Dendeng Batokok", 320, 34, 9, 16, "100g", "https://images.unsplash.com/photo-1544025162-d76694265947?w=800"),
        "Gulai Cubadak" to NutritionInfo("Gulai Cubadak", 280, 8, 25, 18, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Asam Padeh" to NutritionInfo("Asam Padeh", 350, 30, 12, 22, "1 porsi", "https://images.unsplash.com/photo-1580476262798-bddd9f4b7369?w=800"),
        "Pempek Palembang" to NutritionInfo("Pempek Palembang", 430, 18, 53, 13, "6 buah", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800"),
        "Tekwan" to NutritionInfo("Tekwan", 290, 16, 34, 11, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Model" to NutritionInfo("Model", 370, 16, 44, 13, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        
        // === REGIONAL SPECIALTIES - JAWA ===
        "Nasi Liwet" to NutritionInfo("Nasi Liwet", 550, 20, 75, 22, "1 porsi", "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=800"),
        "Gudeg" to NutritionInfo("Gudeg", 370, 12, 50, 16, "1 porsi", "https://images.unsplash.com/photo-1562565652-a0d8f0c59eb4?w=800"),
        "Nasi Pecel" to NutritionInfo("Nasi Pecel", 450, 15, 59, 23, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Rujak Cingur" to NutritionInfo("Rujak Cingur", 320, 18, 37, 14, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Tahu Campur" to NutritionInfo("Tahu Campur", 390, 18, 45, 17, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Lontong Balap" to NutritionInfo("Lontong Balap", 430, 16, 54, 17, "1 porsi", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800"),
        "Sate Klatak" to NutritionInfo("Sate Klatak", 550, 35, 9, 42, "10 tusuk", "https://images.unsplash.com/photo-1529563021893-cc83c992d75d?w=800"),
        "Sego Tempong" to NutritionInfo("Sego Tempong", 480, 18, 62, 20, "1 porsi", "https://images.unsplash.com/photo-1562565652-a0d8f0c59eb4?w=800"),
        "Nasi Gandul" to NutritionInfo("Nasi Gandul", 550, 30, 60, 28, "1 porsi", "https://images.unsplash.com/photo-1562565652-a0d8f0c59eb4?w=800"),
        "Tahu Tek" to NutritionInfo("Tahu Tek", 340, 15, 42, 18, "1 porsi", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800"),
        "Nasi Krawu" to NutritionInfo("Nasi Krawu", 550, 28, 62, 28, "1 porsi", "https://images.unsplash.com/photo-1562565652-a0d8f0c59eb4?w=800"),
        "Soto Lamongan" to NutritionInfo("Soto Lamongan", 340, 23, 31, 15, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        
        // === REGIONAL SPECIALTIES - BALI & NUSA TENGGARA ===
        "Babi Guling" to NutritionInfo("Babi Guling", 610, 38, 14, 44, "1 porsi", "https://images.unsplash.com/photo-1544025162-d76694265947?w=800"),
        "Sate Lilit" to NutritionInfo("Sate Lilit", 370, 28, 9, 26, "8 tusuk", "https://images.unsplash.com/photo-1529563021893-cc83c992d75d?w=800"),
        "Ayam Betutu" to NutritionInfo("Ayam Betutu", 470, 35, 16, 30, "1 potong", "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=800"),
        "Lawar" to NutritionInfo("Lawar", 280, 19, 17, 16, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        "Nasi Jinggo" to NutritionInfo("Nasi Jinggo", 380, 15, 46, 16, "1 bungkus", "https://images.unsplash.com/photo-1562565652-a0d8f0c59eb4?w=800"),
        "Se'i Sapi" to NutritionInfo("Se'i Sapi", 320, 38, 6, 16, "100g", "https://images.unsplash.com/photo-1544025162-d76694265947?w=800"),
        
        // === REGIONAL SPECIALTIES - SULAWESI & KALIMANTAN ===
        "Coto Makassar" to NutritionInfo("Coto Makassar", 370, 32, 22, 23, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Pallubasa" to NutritionInfo("Pallubasa", 420, 30, 25, 26, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Konro" to NutritionInfo("Konro", 560, 34, 29, 37, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        "Pisang Epe" to NutritionInfo("Pisang Epe", 300, 4, 60, 9, "2 buah", "https://images.unsplash.com/photo-1528751014936-863e6e7a319c?w=800"),
        "Kapurung" to NutritionInfo("Kapurung", 310, 15, 41, 9, "1 mangkok", "https://images.unsplash.com/photo-1547928576-b822bc410a08?w=800"),
        
        // === FAST FOOD - KFC ===
        "KFC Original" to NutritionInfo("KFC Original", 310, 27, 13, 20, "1 potong", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800"),
        "KFC Crispy" to NutritionInfo("KFC Crispy", 370, 24, 17, 26, "1 potong", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800"),
        "KFC Combo Attack" to NutritionInfo("KFC Combo Attack", 850, 43, 82, 38, "1 paket", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800"),
        "KFC Chicken Wings" to NutritionInfo("KFC Chicken Wings", 450, 32, 18, 29, "5 buah", "https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=800"),
        "KFC Rice" to NutritionInfo("KFC Rice", 190, 4, 40, 2, "1 porsi", "https://images.unsplash.com/photo-1516684732162-798a0062be99?w=800"),
        "KFC Pergedel" to NutritionInfo("KFC Pergedel", 115, 3, 14, 6, "1 buah", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
        "KFC Coleslaw" to NutritionInfo("KFC Coleslaw", 155, 3, 15, 10, "1 porsi", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800"),
        
        // === FAST FOOD - McDONALD'S ===
        "McD Big Mac" to NutritionInfo("McD Big Mac", 565, 26, 49, 29, "1 buah", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
        "McD McChicken" to NutritionInfo("McD McChicken", 410, 16, 41, 22, "1 buah", "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=800"),
        "McD Cheeseburger" to NutritionInfo("McD Cheeseburger", 320, 16, 33, 14, "1 buah", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=800"),
        "McD Double Cheeseburger" to NutritionInfo("McD Double Cheeseburger", 460, 27, 39, 25, "1 buah", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
        "McD PaNas 1" to NutritionInfo("McD PaNas 1", 600, 31, 70, 28, "1 paket", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800"),
        "McD Ayam Gulai" to NutritionInfo("McD Ayam Gulai", 340, 25, 13, 22, "1 potong", "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=800"),
        "McD McFlurry" to NutritionInfo("McD McFlurry", 430, 8, 63, 16, "1 cup", "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=800"),
        "McD French Fries M" to NutritionInfo("McD French Fries M", 350, 4, 42, 17, "1 porsi", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=800"),
        "McD Sundae" to NutritionInfo("McD Sundae", 290, 5, 44, 8, "1 cup", "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=800"),
        
        // === FAST FOOD - BURGER KING ===
        "BK Whopper" to NutritionInfo("BK Whopper", 700, 31, 53, 40, "1 buah", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
        "BK Whopper Jr" to NutritionInfo("BK Whopper Jr", 390, 16, 32, 22, "1 buah", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=800"),
        "BK Chicken Royale" to NutritionInfo("BK Chicken Royale", 560, 27, 48, 33, "1 buah", "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=800"),
        "BK Onion Rings" to NutritionInfo("BK Onion Rings", 370, 4, 46, 22, "1 porsi", "https://images.unsplash.com/photo-1639024471283-03518883512d?w=800"),
        
        // === CAFE BEVERAGES - STARBUCKS ===
        "Starbucks Caramel Macchiato" to NutritionInfo("Starbucks Caramel Macchiato", 280, 10, 34, 8, "Grande", "https://images.unsplash.com/photo-1485808191679-5f86510681a2?w=800"),
        "Starbucks Latte" to NutritionInfo("Starbucks Latte", 200, 12, 19, 8, "Grande", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=800"),
        "Starbucks Cappuccino" to NutritionInfo("Starbucks Cappuccino", 140, 10, 15, 6, "Grande", "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=800"),
        "Starbucks Americano" to NutritionInfo("Starbucks Americano", 15, 1, 3, 0, "Grande", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"),
        "Starbucks Frappuccino" to NutritionInfo("Starbucks Frappuccino", 375, 6, 58, 16, "Grande", "https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=800"),
        "Starbucks Green Tea Latte" to NutritionInfo("Starbucks Green Tea Latte", 320, 8, 44, 10, "Grande", "https://images.unsplash.com/photo-1515823064-d6e0c04616a7?w=800"),
        "Starbucks Java Chip" to NutritionInfo("Starbucks Java Chip", 440, 8, 68, 16, "Grande", "https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=800"),
        
        // === CAFE BEVERAGES - CHATIME ===
        "Chatime Pearl Milk Tea" to NutritionInfo("Chatime Pearl Milk Tea", 370, 4, 67, 12, "Large", "https://images.unsplash.com/photo-1558857563-b371033873b8?w=800"),
        "Chatime Hazelnut Chocolate" to NutritionInfo("Chatime Hazelnut Chocolate", 420, 6, 73, 14, "Large", "https://images.unsplash.com/photo-1558857563-b371033873b8?w=800"),
        "Chatime Taro Milk Tea" to NutritionInfo("Chatime Taro Milk Tea", 340, 4, 63, 11, "Large", "https://images.unsplash.com/photo-1558857563-b371033873b8?w=800"),
        "Chatime Mango Green Tea" to NutritionInfo("Chatime Mango Green Tea", 280, 3, 56, 6, "Large", "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800"),
        "Chatime Matcha Milk Tea" to NutritionInfo("Chatime Matcha Milk Tea", 340, 6, 59, 11, "Large", "https://images.unsplash.com/photo-1515823064-d6e0c04616a7?w=800"),
        
        // === CAFE BEVERAGES - KOPI KENANGAN ===
        "Kopi Kenangan Mantan" to NutritionInfo("Kopi Kenangan Mantan", 180, 7, 30, 6, "Medium", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"),
        "Kopi Kenangan Signature" to NutritionInfo("Kopi Kenangan Signature", 220, 8, 32, 8, "Medium", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"),
        "Kopi Susu Tetangga" to NutritionInfo("Kopi Susu Tetangga", 250, 9, 39, 9, "Medium", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"),
        "Es Kopi Susu" to NutritionInfo("Es Kopi Susu", 210, 7, 32, 6, "Medium", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"),
        "Avocado Coffee" to NutritionInfo("Avocado Coffee", 370, 6, 45, 19, "Medium", "https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=800"),
        
        // === CAFE BEVERAGES - OTHER POPULAR ===
        "Kopi Janji Jiwa" to NutritionInfo("Kopi Janji Jiwa", 190, 7, 31, 7, "Medium", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"),
        "Fore Coffee Latte" to NutritionInfo("Fore Coffee Latte", 190, 9, 25, 9, "Medium", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=800"),
        "Gulu Gulu Cheese Tea" to NutritionInfo("Gulu Gulu Cheese Tea", 420, 8, 60, 18, "Large", "https://images.unsplash.com/photo-1558857563-b371033873b8?w=800"),
        "Xing Fu Tang" to NutritionInfo("Xing Fu Tang", 490, 6, 83, 15, "Large", "https://images.unsplash.com/photo-1558857563-b371033873b8?w=800"),
        "Tiger Sugar" to NutritionInfo("Tiger Sugar", 490, 6, 87, 18, "Large", "https://images.unsplash.com/photo-1558857563-b371033873b8?w=800")
    )
    
    // Cached lowercase lookup map for O(1) access
    private val lookupCache: Map<String, NutritionInfo> by lazy {
        database.entries.associate { it.key.lowercase() to it.value }
    }
    
    // Cached sorted list for getAllFoodNames
    private val sortedFoodNames: List<String> by lazy {
        database.keys.toList().sorted()
    }
    
    /**
     * Lookup nutrition info by exact food name (case-insensitive)
     * Uses cached O(1) lookup instead of O(n) linear search
     */
    fun lookup(foodName: String): NutritionInfo? {
        return lookupCache[foodName.lowercase()]
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
     * Get all food names (cached sorted list)
     */
    fun getAllFoodNames(): List<String> = sortedFoodNames
    
    /**
     * Get all foods as NutritionInfo list (for Explore screen)
     */
    fun getAllFoods(): List<NutritionInfo> = database.values.sortedBy { it.name }
    
    /**
     * Get foods by category for Explore screen
     * Categories: Indonesian, Breakfast, Snacks, Drinks, FastFood, Cafe
     */
    fun getExploreFoods(category: String): List<NutritionInfo> {
        return database.values.filter { food ->
            when (category) {
                "All" -> true
                "Indonesian" -> food.name.let { name ->
                    name.contains("Nasi") || name.contains("Mie") || name.contains("Sate") ||
                    name.contains("Ayam") || name.contains("Rendang") || name.contains("Soto") ||
                    name.contains("Bakso") || name.contains("Gado") || name.contains("Tempe") ||
                    name.contains("Tahu") || name.contains("Gulai") || name.contains("Coto") ||
                    name.contains("Gudeg") || name.contains("Pempek") || name.contains("Rawon") ||
                    name.contains("Opor") || name.contains("Bebek") || name.contains("Ikan") ||
                    name.contains("Sayur") || name.contains("Pecel") || name.contains("Lawar") ||
                    name.contains("Rujak") || name.contains("Lontong") || name.contains("Ketoprak")
                }
                "Breakfast" -> food.name.let { name ->
                    name.contains("Bubur") || name.contains("Nasi Uduk") || name.contains("Nasi Kuning") ||
                    name.contains("Toast") || name.contains("Omelet") || name.contains("Pancakes") ||
                    name.contains("Nasi Goreng") || name.contains("Mie Goreng")
                }
                "Snacks" -> food.name.let { name ->
                    name.contains("Gorengan") || name.contains("Martabak") || name.contains("Pisang Goreng") ||
                    name.contains("Cireng") || name.contains("Combro") || name.contains("Bakwan") ||
                    name.contains("Risol") || name.contains("Pastel") || name.contains("Lumpia") ||
                    name.contains("Serabi") || name.contains("Kue") || name.contains("Es ") ||
                    name.contains("Cendol") || name.contains("Campur") || name == "Durian"
                }
                "Drinks" -> food.name.let { name ->
                    name.contains("Es ") || name.contains("Jus") || name.contains("Kopi") ||
                    name.contains("Teh") || name.contains("Susu") || name.contains("Starbucks") ||
                    name.contains("Chatime") || name.contains("Kenangan") || name.contains("Jiwa") ||
                    name.contains("Fore") || name.contains("Gulu") || name.contains("Tiger") ||
                    name.contains("Xing") || name.contains("Coffee") || name.contains("Latte") ||
                    name.contains("Frappuccino") || name.contains("Milk Tea")
                }
                "FastFood" -> food.name.let { name ->
                    name.startsWith("KFC") || name.startsWith("McD") || name.startsWith("BK") ||
                    name.contains("Burger") || name.contains("Pizza") || name.contains("Fried Chicken")
                }
                else -> true
            }
        }.sortedBy { it.name }
    }
}
