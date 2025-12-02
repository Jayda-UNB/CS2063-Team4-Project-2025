package ca.unb.mobiledev.cookiestepper.Shop

data class ShopItem (
    val imageID: Int,
    val name: String,
    val price: Int,
    var purchased: Boolean = false
)