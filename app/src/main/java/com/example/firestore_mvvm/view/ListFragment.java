package com.example.firestore_mvvm.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firestore_mvvm.R;
import com.example.firestore_mvvm.adapter.ContactAdapter;
import com.example.firestore_mvvm.dialogue.DetailsDialogue;
import com.example.firestore_mvvm.model.ContactUser;
import com.example.firestore_mvvm.model.UpdateUser;
import com.example.firestore_mvvm.viewmodel.ContactViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;


public class ListFragment extends Fragment implements ContactAdapter.ClickInterface{
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactUser> userList= new ArrayList<>();

    private Button updateImageButton,updateInfoButton;
    private TextView idTextView;
    private EditText nameEditText,phoneEditText,emailEditText;
    private Uri updateUri= null;
    int userPosition;

    //view Model
    private ContactViewModel contactViewModel;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        setUpRecycle();
        //find Section...
        searchView= view.findViewById(R.id.searchViewId);
        recyclerView= view.findViewById(R.id.recycleViewId);
        adapter= new ContactAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                final AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
                dialogue.show();
                contactViewModel.search(s);
                contactViewModel.searchLiveData.observe(getActivity(), new Observer<List<ContactUser>>() {
                    @Override
                    public void onChanged(List<ContactUser> contactUsers) {
                        dialogue.dismiss();
                        userList= contactUsers;
                        adapter.getContactList(userList);
                        recyclerView.setAdapter(adapter);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }



    private void setUpRecycle() {
        final AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
        dialogue.show();
        contactViewModel.show();
        contactViewModel.getContactLiveData.observe(getActivity(), new Observer<List<ContactUser>>() {
            @Override
            public void onChanged(List<ContactUser> contactUsers) {
                dialogue.dismiss();
                userList= contactUsers;
                //userList.clear();
                adapter.getContactList(userList);
                recyclerView.setAdapter(adapter);
                //Toast.makeText(getActivity(), ""+contactUsers.get(0).getContactName(), Toast.LENGTH_SHORT).show(); //check it before set recycle

            }
        });
    }

    private void initViewModel() {
        contactViewModel= new ViewModelProvider(getActivity(),ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ContactViewModel.class);
    }


    @Override
    public void onItemClick(int position) {
        //Toast.makeText(getActivity(), ""+position, Toast.LENGTH_SHORT).show();
        openDetailsDialogue(position);
    }
    @Override
    public void onLongItemClick(final int position) {
        final String id= userList.get(position).contactId;
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        String[] option= {"Update","Delete"};
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    update(position);
                }
                if(which==1){
                    contactViewModel.delete(id);
                    Toast.makeText(getActivity(), "Delete", Toast.LENGTH_SHORT).show();
                    userList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        }).create().show();

    }

    private void update(final int position) {
        userPosition= position;

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        final View view= inflater.inflate(R.layout.update_dialogue,null);
        builder.setView(view).setTitle("Update Contact").setIcon(R.drawable.ic_update).setCancelable(true)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        /*.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id= userList.get(position).getContactId();
                String name= nameEditText.getEditableText().toString();
                String image= "image";
                String phone= phoneEditText.getEditableText().toString();
                String email= emailEditText.getEditableText().toString();
                final ContactUser user= new ContactUser(id,name,image,phone,email);

                if(updateUri==null){

                    Toast.makeText(getActivity(), "image", Toast.LENGTH_SHORT).show();
                    updateUri= Uri.parse(image);
                    AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
                    dialogue.show();
                    contactViewModel.update(user,updateUri);
                    dialogue.dismiss();
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
                    dialogue.show();
                    contactViewModel.update(user,updateUri);
                    dialogue.dismiss();
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        updateImageButton= view.findViewById(R.id.updateImageButtonId);
        updateInfoButton= view.findViewById(R.id.updateInfoButtonId);
        idTextView= view.findViewById(R.id.updateId);
        nameEditText= view.findViewById(R.id.updateNameId);
        phoneEditText= view.findViewById(R.id.updatePhoneId);
        emailEditText= view.findViewById(R.id.updateEmailId);
        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id= userList.get(position).getContactId();
                String name= nameEditText.getText().toString();
                String phone= phoneEditText.getText().toString();
                String email= emailEditText.getText().toString();

                UpdateUser user= new UpdateUser(id,name,phone,email);
                contactViewModel.updateInfo(user);
                Toast.makeText(getActivity(), "Info Update", Toast.LENGTH_SHORT).show();
            }
        });
        updateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        idTextView.setText("ID: "+userList.get(position).getContactId());
        /*Glide.with(view.getContext()).load(userList.get(position).getContactImage()).centerCrop()
                .placeholder(R.drawable.profile).into(circleImageView);*/
        nameEditText.setText(userList.get(position).getContactName());
        phoneEditText.setText(userList.get(position).getContactPhone());
        emailEditText.setText(userList.get(position).getContactEmail());
        builder.create().show();


    }

    private void pickImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getContext(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==getActivity().RESULT_OK){
                updateUri= result.getUri();
                String id= userList.get(userPosition).getContactId();
                contactViewModel.updateImage(id,updateUri);
                Toast.makeText(getActivity(), "Image Update", Toast.LENGTH_SHORT).show();
                //circleImageView.setImageURI(updateUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void openDetailsDialogue(int position) {
        DetailsDialogue dialogue= new DetailsDialogue(userList,position);
        dialogue.show(getChildFragmentManager(),"Details Dialogue");

    }
}