package ca.unb.mobiledev.cookiestepper.Shop

import android.health.connect.datatypes.ExercisePerformanceGoal
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.cookiestepper.R


class ShopAdapter (
    val items: List<ShopItem>,
    val onBuyClick: (ShopItem) -> Unit
): RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {
    class ShopViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.itemImage)
        val name: TextView = view.findViewById(R.id.itemName)
        val price: TextView = view.findViewById(R.id.itemPrice)
        val buyButton: Button = view.findViewById(R.id.buyButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val item = items[position]
        Log.d("ShopAdapter", "Binding item: ${item.name}")

        try {
            holder.image.setImageResource(item.imageID)
            val drawable = holder.image.context.resources.getDrawable(item.imageID, null)
            if(drawable != null) {
                println("Image loaded: ${item.name}")
            }

        } catch (e: Exception) {
            println("Failed to load image: ${item.name}")
            holder.image.setImageResource(R.drawable.ic_launcher_foreground)
        }



        holder.image.setImageResource(item.imageID)
        holder.name.text = item.name
        holder.price.text = "Price: ${item.price} points"
        holder.buyButton.setOnClickListener {
            if(item.purchased == false && Points.points >= item.price) {
                item.purchased = true
                onBuyClick(item)
                holder.buyButton.text = "Equip"
            } else if (item.purchased){
                Toast.makeText(holder.itemView.context, "${item.name} equiped", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(holder.itemView.context, "Not enough points", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun getItemCount() = items.size
}