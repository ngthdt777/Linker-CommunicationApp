package com.example.linker_kotlin.Data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.R
import com.example.linker_kotlin.UI.AddGroupActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.annotations.NotNull


class SearchContactAdapter(mContext: Context, searchItemList: MutableList<SearchItem>) :
    RecyclerView.Adapter<SearchContactAdapter.SearchItemViewHolder>(), Filterable {
        private val searchItemList: List<SearchItem> = searchItemList
        val searchItemListFull: List<SearchItem> = searchItemList as ArrayList<SearchItem>
        val context = mContext

    @NonNull
    @NotNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_group_person_item, parent, false)
        return SearchItemViewHolder(v)
    }

    class SearchItemViewHolder(@NotNull @NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profilePicture: CircleImageView = itemView.findViewById(R.id.search_profile_picture)
        var personName: TextView = itemView.findViewById(R.id.contact_name)
        var checkBox: CheckBox = itemView.findViewById(R.id.search_checkbox)
    }

    override fun onBindViewHolder(@NonNull @NotNull holder: SearchItemViewHolder, position: Int) {
        val currentItem = searchItemList[position]
        Picasso.get().load(currentItem.getProfilePicture()).into(holder.profilePicture)
        holder.personName.text = currentItem.getDisplayName()
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = currentItem.isClicked
        holder.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                (context as AddGroupActivity).removePerson(position)
                return@OnCheckedChangeListener
            }
            Log.d("INFO", "onClick: running")
            (context as AddGroupActivity).addPerson(currentItem)
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
        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            searchItemList.clear()
            searchItemList.addAll(results.values as List<SearchItem>)
            notifyDataSetChanged()
        }
    }


}
