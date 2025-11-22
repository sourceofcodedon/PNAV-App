package com.pampang.nav.models

sealed interface DropdownItem {
    data class Header(val title: String) : DropdownItem
    data class StoreItem(val id: String, val displayName: String) : DropdownItem {
        override fun toString(): String {
            return displayName
        }
    }
}

object StoreCategories {

    private val fishStores = listOf(
        DropdownItem.StoreItem(id = "FirstFishStore", displayName = "Store no. 105"),
        DropdownItem.StoreItem(id = "SecondFishStore", displayName = "Store no. 100")
    )

    private val vegetableStores = listOf(
        DropdownItem.StoreItem(id = "FirstGulayStore", displayName = "Store no. 25"),
        DropdownItem.StoreItem(id = "SecondGulayStore", displayName = "Store no. 24")
    )

    private val meatStores = listOf(
        DropdownItem.StoreItem(id = "FirstMeatStore", displayName = "Store no. 2658"),
        DropdownItem.StoreItem(id = "SecondMeatStore", displayName = "Store no. 2506")
    )

    fun getGroupedStores(): List<DropdownItem> {
        return mutableListOf<DropdownItem>().apply {
            add(DropdownItem.Header("Fish Stores"))
            addAll(fishStores)
            add(DropdownItem.Header("Vegetable Stores"))
            addAll(vegetableStores)
            add(DropdownItem.Header("Meat Stores"))
            addAll(meatStores)
        }
    }

    fun getDisplayName(id: String): String? {
        return getGroupedStores()
            .filterIsInstance<DropdownItem.StoreItem>()
            .find { it.id == id }?.displayName
    }
}
