package com.example.firestore_mvvm.repository;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.firestore_mvvm.model.ContactUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactRepository {

    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    String currentUser= firebaseAuth.getCurrentUser().getUid();
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();


    public MutableLiveData<String> insertContactFirebase(final ContactUser user, Bitmap bit){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        final MutableLiveData<String> insertResultLiveData= new MutableLiveData<>();
        final StorageReference image_path= storageReference.child("profile_image").child(currentUser).child(user.contactId+".jpg");
        image_path.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Map<String,String> contactMap= new HashMap<>();
                        contactMap.put("contact_Id",user.contactId);
                        contactMap.put("contact_Name",user.contactName);
                        contactMap.put("contact_Image",uri.toString());
                        contactMap.put("contact_Phone",user.contactPhone);
                        contactMap.put("contact_Email",user.contactEmail);
                        contactMap.put("contact_Search",user.contactId);

                        //now put this data in firebase....
                        firebaseFirestore.collection("ContactList").document(currentUser).collection("User")
                                .document(user.contactId).set(contactMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        insertResultLiveData.setValue("Upload Successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        insertResultLiveData.setValue(e.toString());
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                insertResultLiveData.setValue(e.toString());
            }
        });

        return  insertResultLiveData;
    }

    public MutableLiveData<List<ContactUser>> getDataFromFireStore(){
       final List<ContactUser> contactList= new ArrayList<>();
        final MutableLiveData<List<ContactUser>> getFireStoreMutableLiveData= new MutableLiveData<>();
        firebaseFirestore.collection("ContactList").document(currentUser).collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                contactList.clear();
                for (DocumentSnapshot documentSnapshot: task.getResult()){
                    String id= documentSnapshot.getString("contact_Id");
                    String name= documentSnapshot.getString("contact_Name");
                    String image= documentSnapshot.getString("contact_Image");
                    String phone= documentSnapshot.getString("contact_Phone");
                    String email= documentSnapshot.getString("contact_Email");
                    ContactUser user= new ContactUser(id,name,image,phone,email);
                    contactList.add(user);
                }
                getFireStoreMutableLiveData.setValue(contactList);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        return getFireStoreMutableLiveData;
    }

    public void updateDataFirebase(final ContactUser user, Bitmap bit){
      //  final MutableLiveData<String> updateMutableLiveData= new MutableLiveData<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        final StorageReference image_path= storageReference.child("profile_image").child(currentUser).child(user.contactId+".jpg");
        image_path.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        firebaseFirestore.collection("ContactList").document(currentUser).collection("User")
                                .document(user.contactId)
                                .update("contact_Name", user.contactName,"contact_Image",uri.toString(),"contact_Phone",user.contactPhone,
                                        "contact_Email",user.contactEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });

    }

    public void deleteDataFirebase(final String id){
        StorageReference deleteImage= storageReference.child("profile_image").child(currentUser).child(id+".jpg");
        deleteImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseFirestore.collection("ContactList").document(currentUser).collection("User")
                        .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });

    }

    public MutableLiveData<List<ContactUser>> searchDataFirebase(String s){
         final List<ContactUser> searchList= new ArrayList<>();
         final MutableLiveData<List<ContactUser>> getSearchMutableLiveData= new MutableLiveData<>();
        firebaseFirestore.collection("ContactList").document(currentUser).collection("User").whereEqualTo("contact_Search",s)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    String id= documentSnapshot.getString("contact_Id");
                    String name= documentSnapshot.getString("contact_Name");
                    String image= documentSnapshot.getString("contact_Image");
                    String phone= documentSnapshot.getString("contact_Phone");
                    String email= documentSnapshot.getString("contact_Email");
                    ContactUser user= new ContactUser(id,name,image,phone,email);
                    searchList.add(user);
                }
                getSearchMutableLiveData.setValue(searchList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



         return getSearchMutableLiveData;
    }

}
