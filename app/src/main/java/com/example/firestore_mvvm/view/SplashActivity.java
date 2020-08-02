package com.example.firestore_mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.example.firestore_mvvm.R;
import com.example.firestore_mvvm.model.SignInUser;
import com.example.firestore_mvvm.viewmodel.SignInViewModel;

public class SplashActivity extends AppCompatActivity {

    private SignInViewModel signInViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSplashViewModel();
        checkIfUserIsAuthenticated();
    }

    private void initSplashViewModel() {
        signInViewModel= new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.
                getInstance(this.getApplication())).get(SignInViewModel.class);

    }

    private void checkIfUserIsAuthenticated() {

        signInViewModel.checkAuthentication();
        signInViewModel.checkAuthenticateLiveData.observe(this, new Observer<SignInUser>() {
            @Override
            public void onChanged(SignInUser signInUser) {
                if(!signInUser.isAuth){
                    goToSignInActivity();
                }
                else {
                    goToMainActivity();
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent intent= new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getUserInformation() {
        signInViewModel.collectUserInfo();
        signInViewModel.collectUserInfoLiveData.observe(this, new Observer<SignInUser>() {
            @Override
            public void onChanged(SignInUser signInUser) {
                //goToMainActivity(signInUser);
                Intent intent= new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*private void goToMainActivity(SignInUser signInUser) {
        getIntent().putExtra("userDetails",signInUser);
        FragmentManager fragmentManager= getSupportFragmentManager();
        HomeFragment homeFragment= new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.homeFragment,homeFragment).commit();
    }*/

    private void goToSignInActivity() {
        Intent intent= new Intent(SplashActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();
    }
}