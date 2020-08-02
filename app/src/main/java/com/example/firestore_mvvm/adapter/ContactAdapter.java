package com.example.firestore_mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firestore_mvvm.R;
import com.example.firestore_mvvm.model.ContactUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private List<ContactUser> userList;
    private ClickInterface clickInterface;

    public ContactAdapter(ClickInterface clickInterface) {
        this.clickInterface= clickInterface;
    }
    public void getContactList(List<ContactUser> userList){
        this.userList= userList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.single_contact,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext()).load(userList.get(position).getContactImage()).centerCrop()
                .placeholder(R.drawable.profile).into(holder.circleImageView);
        holder.contactId.setText(userList.get(position).getContactId());
        holder.contactName.setText(userList.get(position).getContactName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public ContactUser getPosition(int position){

        return userList.get(position);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CircleImageView circleImageView;
        private AppCompatTextView contactId;
        private AppCompatTextView contactName;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView= itemView.findViewById(R.id.singleImageId);
            contactId= itemView.findViewById(R.id.singleContactId);
            contactName= itemView.findViewById(R.id.singleNameId);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            clickInterface.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            clickInterface.onLongItemClick(getAdapterPosition());
            return false;
        }
    }
    public interface ClickInterface {
        // for on Click....
        void onItemClick(int position);
        void onLongItemClick(int position);
    }

}
