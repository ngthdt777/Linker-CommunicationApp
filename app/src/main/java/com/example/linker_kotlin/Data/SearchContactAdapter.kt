package com.example.linker_kotlin.Data

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.R
import com.example.linker_kotlin.UI.AddGroupActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class SearchContactAdapter(context: Context, searchItemList: MutableList<SearchItem>) :
    RecyclerView.Adapter<SearchContactAdapter.SearchItemViewHolder>(), Filterable {
    private val searchItemList: List<SearchItem>
    private lateinit var searchItemListFull: List<SearchItem>
    private val context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_group_person_item, parent, false)
        return SearchItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val currentItem = searchItemList[position]
        Picasso.get().load(currentItem.getProfilePicture()).into(holder.profilePicture)
        holder.personName.text = currentItem.getDisplayName()
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = currentItem.isClicked
        holder.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                return@OnCheckedChangeListener
            }
            Log.d("INFO", "onClick: running")
            (context as AddGroupActivity).addPerson(currentItem as User)
        })
    }

    override fun onViewRecycled(holder: SearchItemViewHolder) {
        super.onViewRecycled(holder)
        holder.checkBox.setOnCheckedChangeListener(null)
    }

    override fun getItemCount(): Int {
        return searchItemList.size
    }

    override fun getFilter(): Filter {
        return searchFiler
    }

    private val searchFiler: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterList: MutableList<SearchItem?> = ArrayList()
            if (constraint.isEmpty()) {
                //filterList.addAll(searchItemListFull);
            } else {
                val filter = constraint.toString().lowercase().trim { it <= ' ' }
                for (searchItem in searchItemListFull) {
                    if (searchItem.getDisplayName()?.lowercase()?.contains(filter) == true) {
                        filterList.add(searchItem)
                    }
                }
            }
            val results = FilterResults()
            results.values = filterList
            return results
        }
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            searchItemList.clear()
            searchItemList.addAll(results.values as List<SearchItem>)
            notifyDataSetChanged()
        }
    }

    class SearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profilePicture: CircleImageView = itemView.findViewById(R.id.search_profile_picture)
        var personName: TextView
        var checkBox: CheckBox

        init {
            personName = itemView.findViewById(R.id.contact_name)
            checkBox = itemView.findViewById(R.id.search_checkbox)
        }
    }

    init {
        this.searchItemList = searchItemList
        searchItemListFull = ArrayList<SearchItem>(searchItemList)
        this.context = context
    }
}
