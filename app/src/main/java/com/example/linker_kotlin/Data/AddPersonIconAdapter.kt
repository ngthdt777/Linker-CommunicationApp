package com.example.linker_kotlin.Data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.R
import com.example.linker_kotlin.UI.AddGroupActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AddPersonIconAdapter(var context: Context, private var userList: List<User>) :
    RecyclerView.Adapter<AddPersonIconAdapter.PersonViewHolder>() {

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleImageView: CircleImageView = itemView.findViewById(R.id.search_icon_pic)
        var firstName: TextView = itemView.findViewById(R.id.search_item_first_name)
        var removeButton: ImageView = itemView.findViewById(R.id.search_item_remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.add_group_add_icon, parent, false)
        return PersonViewHolder(v)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val user: User = userList[position]
        Picasso.get().load(user.getProfilePicture()).into(holder.circleImageView)
        holder.firstName.text = user.getFirstName()
        holder.removeButton.setOnClickListener {
            (context as AddGroupActivity).removePerson(position)
            (context as AddGroupActivity).unCheckPerson(user.getUserId())
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}

