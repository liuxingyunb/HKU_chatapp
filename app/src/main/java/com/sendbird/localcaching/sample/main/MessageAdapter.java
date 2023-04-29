package com.sendbird.localcaching.sample.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sendbird.localcaching.sample.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
    private Context mContext;
    private ArrayList<String> messages;
    private DatabaseReference chatRef;

    public MessageAdapter(Context mContext, ArrayList<String> messages) {
        this.mContext = mContext;
        this.messages = messages;
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pos = MessageActivity.messagePosition.get(position);
        if (pos.equals("0")) {
            holder.txtLeft.setVisibility(View.VISIBLE);
            holder.txtRight.setVisibility(View.GONE);
            holder.txtLeft.setText(messages.get(position));
        }
        else {
            holder.txtRight.setVisibility(View.VISIBLE);
            holder.txtLeft.setVisibility(View.GONE);
            holder.txtRight.setText(messages.get(position));
        }
        holder.txtRight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Delete Messages");
                alert.setMessage("Are you sure? Do you want to delete this message \nMessage: "+messages.get(position));
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chatRef.child(MessageActivity.messageId.get(position)).removeValue();
                    }
                });
                alert.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtLeft,txtRight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLeft = itemView.findViewById(R.id.txtLeft);
            txtRight = itemView.findViewById(R.id.txtRight);
        }
    }
}
