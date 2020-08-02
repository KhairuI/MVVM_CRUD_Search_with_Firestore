package com.example.firestore_mvvm.dialogue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.firestore_mvvm.R;
import com.example.firestore_mvvm.model.ContactUser;
import com.example.firestore_mvvm.view.ListFragment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsDialogue extends DialogFragment {
    private CircleImageView circleImageView;
    private AppCompatTextView idTextView,nameTextView,phoneTextView,emailTextView;
    private List<ContactUser> userList;
    private int position;
    private ListFragment listFragment;

    public DetailsDialogue(List<ContactUser> userList, int position, ListFragment listFragment) {
        this.userList = userList;
        this.position = position;
        this.listFragment = listFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.details_dialogue,null);
        builder.setView(view).setTitle("Contact Details").setIcon(R.drawable.ic_view).setCancelable(true).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id= userList.get(position).getContactId();
                String name= userList.get(position).getContactName();
                String image= userList.get(position).getContactImage();
                String phone= userList.get(position).getContactPhone();
                String email= userList.get(position).getContactEmail();
                ContactUser user= new ContactUser(id,name,image,phone,email);

                UpdateDialogue updateDialogue= new UpdateDialogue(user);
                updateDialogue.show(getParentFragmentManager(),"Update Fragment");

                /*//sent in the insert fragment....
                FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                InsertFragment fragment= new InsertFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("my_object",user);
                fragment.setArguments(bundle);
                ft.replace(R.id.insertFragment,fragment);
                ft.addToBackStack(null);
                ft.commit();*/
            }
        });

        circleImageView= view.findViewById(R.id.detailsImageId);
        idTextView= view.findViewById(R.id.detailsId);
        nameTextView= view.findViewById(R.id.detailsNameId);
        phoneTextView= view.findViewById(R.id.detailsPhoneId);
        emailTextView= view.findViewById(R.id.detailsEmailId);

        Glide.with(view.getContext()).load(userList.get(position).getContactImage()).centerCrop()
                .placeholder(R.drawable.profile).into(circleImageView);
        idTextView.setText("ID: "+userList.get(position).getContactId());
        nameTextView.setText("Name: "+userList.get(position).getContactName());
        phoneTextView.setText("Phone: "+userList.get(position).getContactPhone());
        emailTextView.setText("Email: "+userList.get(position).getContactEmail());

        return builder.create();

    }
}
