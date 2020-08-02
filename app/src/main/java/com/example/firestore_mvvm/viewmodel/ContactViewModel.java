package com.example.firestore_mvvm.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.firestore_mvvm.model.ContactUser;
import com.example.firestore_mvvm.repository.ContactRepository;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    private ContactRepository repository;
    public LiveData<String> insertResultLiveData;
    public LiveData<List<ContactUser>> getContactLiveData;
    //public LiveData<String> updateResultLiveData;
    public ContactViewModel(@NonNull Application application) {
        super(application);
        repository= new ContactRepository();
    }

    public void insert(ContactUser user, Bitmap bit){
        insertResultLiveData= repository.insertContactFirebase(user, bit);
    }

    public void show(){
        getContactLiveData= repository.getDataFromFireStore();
    }

    public void update(ContactUser user, Bitmap bit){
        repository.updateDataFirebase(user,bit);

    }
    public void delete(String id){
        repository.deleteDataFirebase(id);

    }
}
