package ca.unb.mobiledev.cookiestepper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry

/**
 * Class used to populate each row of the RecyclerView
 * @param entries The list of foods to be displayed
 * @param listener The onClickListener behaviour for each row
 */
class FoodHistoryAdapter(private var entries: List<FoodLogEntry>, private val listener: (FoodLogEntry) -> Unit) :
    RecyclerView.Adapter<FoodHistoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_history_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        // Get the Food at index position in courseList
        val entry = entries[position]

        val portionsText = "${entry.portionCount} portion" + if(entry.portionCount != 1) "s" else ""
        val details = String.format("%s, %.0f kcal", portionsText, entry.totalCalories)
        holder.portionCalsTextView.text = details
        holder.foodNameTextView.text = entry.foodName
        holder.foodIdTextView.text = entry.fdcId.toString()
        holder.dateTextView.text = entry.date

        holder.itemView.setOnClickListener { listener(entry) }
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    fun updateList(newEntries: List<FoodLogEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    // Inner ViewHolder Class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodNameTextView: TextView = itemView.findViewById(R.id.foodName)
        val foodIdTextView: TextView = itemView.findViewById(R.id.foodId)
        val portionCalsTextView: TextView = itemView.findViewById(R.id.portionCalsTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }
}