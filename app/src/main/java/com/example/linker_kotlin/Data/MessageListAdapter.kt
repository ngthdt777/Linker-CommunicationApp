package com.example.linker_kotlin.Data

import android.content.Context
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.Util.Utility
import com.squareup.picasso.Picasso
import org.jetbrains.annotations.NotNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageListAdapter(
    private val mContext: Context,
    private val mMessageList: List<MessageInterface>,
    private val currentUser: User,
    private val chatRoom: MyChatRoom
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position]
        val viewType = holder.itemViewType
        if (viewType == VIEW_TYPE_MESSAGE_SENT || viewType == VIEW_TYPE_CALL_SENT) {
            (holder as SentMessageHolder?)!!.bind(message)
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED || viewType == VIEW_TYPE_CALL_RECEIVED) {
            (holder as ReceivedMessageHolder?)!!.bind(message)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_send, parent, false)
                return SentMessageHolder(view)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_receceived, parent, false)
                return ReceivedMessageHolder(view)
            }
            VIEW_TYPE_CALL_SENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_call_send, parent, false)
                return SentMessageHolder(view)
            }
            VIEW_TYPE_CALL_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_call_receceived, parent, false)
                return ReceivedMessageHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_send, parent, false)
                return SentMessageHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position]
        return if (message.getSenderID().equals(currentUser.getUserId())) {
            if (message.getMessageType() == 1) {
                VIEW_TYPE_CALL_SENT
            } else VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            if (message.getMessageType() == 1) {
                VIEW_TYPE_CALL_RECEIVED
            } else VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    private inner class ReceivedMessageHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView
        var timeText: TextView
        var nameText: TextView
        var profileImage: ImageView
        fun bind(message: MessageInterface) {
            val type = message.getMessageType()
            messageText.text = message.getContent()

            // Format the stored timestamp into a readable String using method.
            timeText.text =(Utility().dateToStringFull(message.getTimeSent()!!))
            val sender = chatRoom.getMemberByID(message.getSenderID())
            if (sender == null) {
                Database.getInstance().getAPI().getUserById(message.getSenderID()!!)
                    .enqueue(object : Callback<User?> {
                        override fun onResponse(call: Call<User?>, response: Response<User?>) {
                            val senderRes = response.body()
                            nameText.text = senderRes!!.getDisplayName()
                            Picasso.get().load(senderRes.getProfilePicture()).into(profileImage)
                        }

                        override fun onFailure(call: Call<User?>, t: Throwable) {
                            nameText.text = message.getSenderID()
                        }
                    })
            } else {
                nameText.text = sender.getDisplayName()
                // Insert the profile image from the URL into the ImageView.
                Picasso.get().load(sender.getProfilePicture()).into(profileImage)
            }
        }

        init {
            messageText = itemView.findViewById(R.id.text_message_body)
            timeText = itemView.findViewById(R.id.text_message_time)
            nameText = itemView.findViewById(R.id.text_message_name)
            profileImage = itemView.findViewById(R.id.image_message_profile)
        }
    }

    private inner class SentMessageHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        var messageText: TextView
        var timeText: TextView
        fun bind(message: MessageInterface) {
            val type = message.getMessageType()
            messageText.text = message.getContent()
            // Format the stored timestamp into a readable String using method.
            timeText.text = Utility().dateToStringFull(message.getTimeSent()!!)
            if (message.getMessageType() == 1) {
                messageText.setOnCreateContextMenuListener(this)
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            menu.add(this.adapterPosition, 10, 0, "Delete")
        }

        init {
            messageText = itemView.findViewById(R.id.text_message_body)
            timeText = itemView.findViewById(R.id.text_message_time)
        }
    }

    fun getMessageByPosition(pos: Int): MessageInterface {
        return mMessageList[pos]
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
        private const val VIEW_TYPE_CALL_SENT = 3
        private const val VIEW_TYPE_CALL_RECEIVED = 4
    }
}