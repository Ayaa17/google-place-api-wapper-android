package com.aya.google_api_wapper


interface PlaceType {
    val apiValue: String
}

enum class FoodDrink(override val apiValue: String) :PlaceType {

    ACAI_SHOP("acai_shop"),
    AFGHANI_RESTAURANT("afghani_restaurant"),
    AFRICAN_RESTAURANT("african_restaurant"),
    AMERICAN_RESTAURANT("american_restaurant"),
    ASIAN_RESTAURANT("asian_restaurant"),
    BAGEL_SHOP("bagel_shop"),
    BAKERY("bakery"),
    BAR("bar"),
    BAR_AND_GRILL("bar_and_grill"),
    BARBECUE_RESTAURANT("barbecue_restaurant"),
    BRAZILIAN_RESTAURANT("brazilian_restaurant"),
    BREAKFAST_RESTAURANT("breakfast_restaurant"),
    BRUNCH_RESTAURANT("brunch_restaurant"),
    BUFFET_RESTAURANT("buffet_restaurant"),
    CAFE("cafe"),
    CAFETERIA("cafeteria"),
    CANDY_STORE("candy_store"),
    CAT_CAFE("cat_cafe"),
    CHINESE_RESTAURANT("chinese_restaurant"),
    CHOCOLATE_FACTORY("chocolate_factory"),
    CHOCOLATE_SHOP("chocolate_shop"),
    COFFEE_SHOP("coffee_shop"),
    CONFECTIONERY("confectionery"),
    DELI("deli"),
    DESSERT_RESTAURANT("dessert_restaurant"),
    DESSERT_SHOP("dessert_shop"),
    DINER("diner"),
    DOG_CAFE("dog_cafe"),
    DONUT_SHOP("donut_shop"),
    FAST_FOOD_RESTAURANT("fast_food_restaurant"),
    FINE_DINING_RESTAURANT("fine_dining_restaurant"),
    FOOD_COURT("food_court"),
    FRENCH_RESTAURANT("french_restaurant"),
    GREEK_RESTAURANT("greek_restaurant"),
    HAMBURGER_RESTAURANT("hamburger_restaurant"),
    ICE_CREAM_SHOP("ice_cream_shop"),
    INDIAN_RESTAURANT("indian_restaurant"),
    INDONESIAN_RESTAURANT("indonesian_restaurant"),
    ITALIAN_RESTAURANT("italian_restaurant"),
    JAPANESE_RESTAURANT("japanese_restaurant"),
    JUICE_SHOP("juice_shop"),
    KOREAN_RESTAURANT("korean_restaurant"),
    LEBANESE_RESTAURANT("lebanese_restaurant"),
    MEAL_DELIVERY("meal_delivery"),
    MEAL_TAKEAWAY("meal_takeaway"),
    MEDITERRANEAN_RESTAURANT("mediterranean_restaurant"),
    MEXICAN_RESTAURANT("mexican_restaurant"),
    MIDDLE_EASTERN_RESTAURANT("middle_eastern_restaurant"),
    PIZZA_RESTAURANT("pizza_restaurant"),
    PUB("pub"),
    RAMEN_RESTAURANT("ramen_restaurant"),
    RESTAURANT("restaurant"),
    SANDWICH_SHOP("sandwich_shop"),
    SEAFOOD_RESTAURANT("seafood_restaurant"),
    SPANISH_RESTAURANT("spanish_restaurant"),
    STEAK_HOUSE("steak_house"),
    SUSHI_RESTAURANT("sushi_restaurant"),
    TEA_HOUSE("tea_house"),
    THAI_RESTAURANT("thai_restaurant"),
    TURKISH_RESTAURANT("turkish_restaurant"),
    VEGAN_RESTAURANT("vegan_restaurant"),
    VEGETARIAN_RESTAURANT("vegetarian_restaurant"),
    VIETNAMESE_RESTAURANT("vietnamese_restaurant"),
    WINE_BAR("wine_bar");

    companion object {

        /** 取得「完整 enum 內容」 */
        fun toList(): List<FoodDrink> =
            values().toList()

        /** 只取 Places API 需要的字串 */
        fun toListValue(): List<String> =
            values().map { it.apiValue }
    }
}