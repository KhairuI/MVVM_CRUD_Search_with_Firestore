package com.example.firestore_mvvm.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.firestore_mvvm.model.SignInUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInRepository {

    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private SignInUser user= new SignInUser();

                         // check Authentication in firebase..........

    public MutableLiveData<SignInUser> checkAuthenticationInFirebase(){

        MutableLiveData<SignInUser> isAuthenticateLiveData= new MutableLiveData<>();
        FirebaseUser currentUser= firebaseAuth.getCurrentUser();
        if(currentUser==null){
            user.isAuth= false;
            isAuthenticateLiveData.setValue(user);

        }
        else {
            user.uId= currentUser.getUid();
            user.isAuth= true;
            isAuthenticateLiveData.setValue(user);

        }
        return isAuthenticateLiveData;
    }

                              //collect user info  from authentication..........

    public MutableLiveData<SignInUser> collectUserData(){
        MutableLiveData<SignInUser> collectUserMutableLiveData= new MutableLiveData<>();

        FirebaseUser currentUser= firebaseAuth.getCurrentUser();
        if(currentUser !=null){
            String uId= currentUser.getUid();
            String name= currentUser.getDisplayName();
            String email= currentUser.getEmail();
            Uri getImageUrl= currentUser.getPhotoUrl();
            String imageUrl= getImageUrl.toString();
            SignInUser user= new SignInUser(uId,name,email,imageUrl);
            collectUserMutableLiveData.setValue(user);
        }
        return collectUserMutableLiveData;
    }

                                            //firebase sign in with google

    public MutableLiveData<String> firebaseSignInWithGoogle(AuthCredential authCredential){

        final MutableLiveData<String> authMutableLiveData= new MutableLiveData<>();
        firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser currentUser= firebaseAuth.getCurrentUser();
                String uId= currentUser.getUid().toString();
                authMutableLiveData.setValue(uId);
                /*if(currentUser !=null){
                    String uId= currentUser.getUid().toString();
                    String name= currentUser.getDisplayName().toString();
                    String email= currentUser.getEmail().toString();
                    Uri getImageUrl= currentUser.getPhotoUrl();
                    String imageUrl= getImageUrl.toString();
                    SignInUser user= new SignInUser(uId,name,email,imageUrl);
                    authMutableLiveData.setValue(user);
                }*/

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                authMutableLiveData.setValue(e.toString());
            }
        });
        return authMutableLiveData;
    }

}
