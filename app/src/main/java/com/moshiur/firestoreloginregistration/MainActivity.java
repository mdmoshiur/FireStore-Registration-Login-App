package com.moshiur.firestoreloginregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.moshiur.firestoreloginregistration.models.User;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {


    private Button loginButton;
    private EditText loginUserID;
    private EditText loginPass;
    private TextView noAccount;
    private Group grp_login, grp_user_info;
    private TextView userName, userID;
    private Button logoutButton;

    private String user_id, password, actual_password, user_name;

    private boolean loginStatus = false;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grp_login = findViewById(R.id.grp_login);
        grp_user_info = findViewById(R.id.grp_user_info);

        loginButton = findViewById(R.id.logInButton);
        loginUserID  = findViewById(R.id.login_user_id);
        loginPass  = findViewById(R.id.login_pass_id);
        noAccount = findViewById(R.id.dont_account);

        userName = findViewById(R.id.user_name_id);
        userID = findViewById(R.id.user_id);
        logoutButton = findViewById(R.id.logout_button);

        // SharedPreferences to save user information
        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //check login status
        if(!loginStatus){
            //user not logged in
            user_name = sharedPref.getString("user_name","" );
            user_id = sharedPref.getString("user_id","" );
            if(!user_id.equals("") && !user_name.equals("")){
                loginStatus = true;
                grp_login.setVisibility(View.GONE);
                grp_user_info.setVisibility(View.VISIBLE);

                setUserInfo();
            }
        }

        //don't have account
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register user
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){
                    //check user exists not or not
                    checkUserID(user_id);
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user_name", "");
                editor.putString("user_id", "");
                editor.apply();
                loginStatus = false;
                user_id = "";
                user_name = "";

                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

    }

    private void checkUserID(String user_id) {
        FirebaseFirestore.getInstance().collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isExistsUserID = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                user.setDocumentID(document.getId());
                                if(user.getUser_id().equals(user_id)){
                                    isExistsUserID = true;
                                    actual_password = user.getPassword();
                                    user_name = user.getName();
                                    break;
                                }
                                Log.d("debug", document.getId() + " => " + document.getData());
                            }
                            if(isExistsUserID){
                                doLogin(actual_password);
                            } else {
                                Toasty.error(MainActivity.this, "This user ID does not exists", Toasty.LENGTH_SHORT).show();
                            }
                        } else {
                            Toasty.error(MainActivity.this, "Failed to Login ", Toasty.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(MainActivity.this, "Failed to Login", Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    private void doLogin(String actual_password){
        if(password.equals(actual_password)){
            Toasty.success(MainActivity.this, "Login Successful", Toasty.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
//            intent.putExtra("user_name", user_name);
//            intent.putExtra("user_id", user_id);
//            startActivity(intent);
            loginStatus = true;
            editor.putString("user_name", user_name);
            editor.putString("user_id", user_id);
            editor.apply();
            grp_login.setVisibility(View.GONE);
            grp_user_info.setVisibility(View.VISIBLE);
        } else {
            Toasty.error(MainActivity.this, "Password don't matched", Toasty.LENGTH_SHORT).show();
        }
    }

    private boolean validateData(){
        user_id = loginUserID.getText().toString().trim();
        password = loginPass.getText().toString().trim();

        if(user_id.equals("")){
            Toasty.error(MainActivity.this, "User ID should not be null", Toast.LENGTH_SHORT).show();
            return false;
        } else if(password.equals("")){
            Toasty.error(MainActivity.this, "Password should not be null", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setUserInfo(){
        userName.setText("Name: " + user_name);
        userID.setText("User ID: " + user_id);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user_name", "");
                editor.putString("user_id", "");
                editor.apply();
                loginStatus = false;
                user_id = "";
                user_name = "";

                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
    }
}