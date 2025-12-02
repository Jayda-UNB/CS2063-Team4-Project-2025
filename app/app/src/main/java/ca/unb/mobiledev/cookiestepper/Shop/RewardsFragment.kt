package ca.unb.mobiledev.cookiestepper.Shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.unb.mobiledev.cookiestepper.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RewardsFragment: Fragment() {

    private lateinit var pointsTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rewards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RewardsFragment", "Fragment loaded")

        pointsTextView = view.findViewById(R.id.pointsTextView)
        updatePointsDisplay()

        val recyclerView = view.findViewById<RecyclerView>(R.id.shopRecyclerView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val items = listOf(
            ShopItem(R.drawable.basic_cookie, "Basic Cookie", 100),
            ShopItem(R.drawable.bow_cookie, "Bow Tie Cookie", 250),
            ShopItem(R.drawable.head_cookie, "Headphone Cookie", 500),
            ShopItem(R.drawable.chef_cookie,"Chef Cookie", 750),
            ShopItem(R.drawable.mus_cookie, "Mustache Cookie", 1000),
            ShopItem(R.drawable.royal_cookie, "Royal Cookie", 2000)

        )

        val adapter = ShopAdapter(items) { item ->
            if (Points.points >= item.price) {
                Points.points -= item.price
                updatePointsDisplay()
                Toast.makeText(requireContext(), "Bought ${item.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Not enough points", Toast.LENGTH_SHORT).show()
            }


        }
        recyclerView.adapter = adapter

        progressBar.visibility = View.GONE
    }
    private fun updatePointsDisplay() {
        pointsTextView.text = "Points: ${Points.points}"
    }
}