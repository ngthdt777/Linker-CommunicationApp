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

class AddPersonIconAdapter(context: Context, userList: List<User>) :
    RecyclerView.Adapter<AddPersonIconAdapter.PersonViewHolder>() {
    private val userList: List<User> = userList
    private val context: Context = context

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleImageView: CircleImageView
        var firstName: TextView
        var removeButton: ImageView
        init {
            circleImageView = itemView.findViewById(R.id.search_icon_pic)
            firstName = itemView.findViewById(R.id.search_item_first_name)
            removeButton = itemView.findViewById(R.id.search_item_remove_button)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.add_group_add_icon, parent, false)
        return PersonViewHolder(v)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val user: User = userList[position]
        Picasso.get().load(user.getProfilePicture()).into(holder.circleImageView)
        holder.firstName.setText(user.getFirstName())
        holder.removeButton.setOnClickListener {
            (context as AddGroupActivity).removePerson(position)
            context.unCheckPerson(user.getUserId())
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}

