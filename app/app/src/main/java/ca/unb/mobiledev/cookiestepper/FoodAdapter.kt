package ca.unb.mobiledev.cookiestepper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mobiledev.unb.ca.threadinglab.models.Food

/**
 * Class used to populate each row of the RecyclerView
 * @param foodList The list of foods to be displayed
 * @param listener The onClickListener behaviour for each row
 */
class FoodAdapter(private var foodList: List<Food>, private val listener: (Food) -> Unit) :
    RecyclerView.Adapter<FoodAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        // Get the Food at index position in courseList
        val food = foodList[position]

        holder.foodNameTextView.text = food.description
        holder.foodIdTextView.text = food.fdcId.toString()

        holder.itemView.setOnClickListener { listener(food) }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateList(newList: List<Food>) {
        foodList = newList
        notifyDataSetChanged()
    }

    // Inner ViewHolder Class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodNameTextView: TextView = itemView.findViewById(R.id.foodName)
        val foodIdTextView: TextView = itemView.findViewById(R.id.foodId)
    }
}