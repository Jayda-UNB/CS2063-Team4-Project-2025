package ca.unb.mobiledev.cookiestepper.Shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.unb.mobiledev.cookiestepper.R
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.zip.Inflater

class RewardsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rewards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RewardsFragment", "Fragment loaded")

        val recyclerView = view.findViewById<RecyclerView>(R.id.shopRecyclerView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val items = listOf(
            ShopItem(R.drawable.avatar1, "TEST 1", 200),
            ShopItem(R.drawable.avatar2, "TEST 2", 300),
            ShopItem(R.drawable.avatar3, "TEST 3", 400)

        )

        val adapter = ShopAdapter(items) { item ->
            Toast.makeText(requireContext(), "Bought ${item.name}", Toast.LENGTH_SHORT).show()


        }
        recyclerView.adapter = adapter

        progressBar.visibility = View.GONE
    }
}