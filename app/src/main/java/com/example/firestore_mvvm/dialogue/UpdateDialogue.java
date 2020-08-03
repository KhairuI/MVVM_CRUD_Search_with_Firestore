package com.example.firestore_mvvm.dialogue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.firestore_mvvm.R;
import com.example.firestore_mvvm.model.ContactUser;
import com.example.firestore_mvvm.viewmodel.ContactViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;

public class UpdateDialogue extends DialogFragment {
    private CircleImageView circleImageView;
    private AppCompatTextView idTextView;
    private AppCompatEditText nameEditText,phoneEditText,emailEditText;
    private ContactUser user;
    private static final int CAPTURE_PICCODE = 1;
    private Bitmap updateImageUri= null;
   // private UpdateListener updateListener;
    //view Model
    private Uri mainUri=null;
    private ContactViewModel contactViewModel;

    public UpdateDialogue(ContactUser user) {
        this.user = user;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
      initialViewModel();

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        final View view= inflater.inflate(R.layout.update_dialogue,null);
        builder.setView(view).setTitle("Update Contact").setIcon(R.drawable.ic_update).setCancelable(true)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                //image and data pass in view model


                String id= user.contactId;
                String name= nameEditText.getEditableText().toString();
                String image= "image_url";
                String phone= phoneEditText.getEditableText().toString();
                String email= emailEditText.getEditableText().toString();
                ContactUser user1= new ContactUser(id,name,image,phone,email);
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(email)){
                    updateData(user1);
                }
                else {

                    Toast.makeText(getActivity(), "Please Fill up all field", Toast.LENGTH_SHORT).show();
                }


            /*    contactViewModel.updateResultLiveData.observe( getActivity(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), ""+s, Toast.LENGTH_SHORT).show();
                    }
                });*/

            }
        });

        circleImageView= view.findViewById(R.id.updateImageId);
        idTextView= view.findViewById(R.id.updateId);
        nameEditText= view.findViewById(R.id.updateNameId);
        phoneEditText= view.findViewById(R.id.updatePhoneId);
        emailEditText= view.findViewById(R.id.updateEmailId);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        idTextView.setText("ID: "+user.contactId);
        Glide.with(view.getContext()).load(user.getContactImage()).centerCrop()
                .placeholder(R.drawable.profile).into(circleImageView);
        nameEditText.setText(user.getContactName());
        phoneEditText.setText(user.getContactPhone());
        phoneEditText.setText(user.getContactPhone());
        emailEditText.setText(user.getContactEmail());

        return builder.create();
    }

    private void updateData(ContactUser user1) {

        AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
        dialogue.show();
        contactViewModel.update(user1,updateImageUri);
        dialogue.dismiss();
        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
    }

    private void initialViewModel() {
        contactViewModel= new ViewModelProvider(getActivity(),ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ContactViewModel.class);
    }

    private void pickImage() {
        Intent intent= new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CAPTURE_PICCODE);
        // start picker to get image for cropping and then use the image in cropping activity
        /*CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getActivity());*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==getActivity().RESULT_OK){
                mainUri= result.getUri();
                circleImageView.setImageURI(mainUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/
        if(resultCode== getActivity().RESULT_OK){
            if(requestCode== CAPTURE_PICCODE){
                Uri returnUri = data.getData();

                File actualImage= new File(returnUri.getPath());
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap resize= Bitmap.createScaledBitmap(bitmapImage,
                        (int) (bitmapImage.getWidth()*0.5),
                        (int) (bitmapImage.getHeight()*0.5),
                        true);

                updateImageUri= resize;
                circleImageView.setImageBitmap(resize);

            }
        }
    }

/*    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            updateListener= (UpdateListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() +"Must Implement updateDialogue Listener");
        }
    }

    public interface UpdateListener{
        void updateData(ContactUser user,Bitmap bitmap);
    }*/
}
