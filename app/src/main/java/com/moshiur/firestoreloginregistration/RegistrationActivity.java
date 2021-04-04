package com.moshiur.firestoreloginregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.moshiur.firestoreloginregistration.models.User;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity {

    private EditText reg_name, reg_user_id, reg_pass, reg_con_pass;
    private Button registerButton;
    private TextView haveAccount;

    private String name, user_id, password, con_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        reg_name = findViewById(R.id.reg_name_id);
        reg_user_id = findViewById(R.id.reg_user_id);
        reg_pass = findViewById(R.id.reg_pass_id);
        reg_con_pass = findViewById(R.id.reg_con_pass_id);
        haveAccount = findViewById(R.id.have_account_id);
        registerButton = findViewById(R.id.reg_button);

        //click have account id
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register user
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    //check user already have or not
                    checkUserID(user_id);
                }
            }
        });

    }

    private boolean validateForm(){
        name = reg_name.getText().toString().trim();
        user_id = reg_user_id.getText().toString().trim();
        password = reg_pass.getText().toString().trim();
        con_password = reg_con_pass.getText().toString().trim();

        if(name.equals("")){
            Toasty.error(RegistrationActivity.this, "Name should not be null", Toast.LENGTH_SHORT).show();
            return false;
        } else if(name.length() < 3){
            Toasty.info(RegistrationActivity.this, "Name must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return false;
        }else if(user_id.equals("")){
            Toasty.error(RegistrationActivity.this, "User ID should not be null", Toast.LENGTH_SHORT).show();
            return false;
        } else if(user_id.length() < 3){
            Toasty.info(RegistrationActivity.this, "User ID must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if(password.equals("")){
            Toasty.error(RegistrationActivity.this, "Password should not be null", Toast.LENGTH_SHORT).show();
            return false;
        } else if(password.length() < 8){
            Toasty.info(RegistrationActivity.this, "Password length must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if(con_password.equals("")){
            Toasty.error(RegistrationActivity.this, "Confirm password should not be null", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!con_password.equals(password)){
            Toasty.error(RegistrationActivity.this, "Password must be matched", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    private void doRegistration() {
        User user = new User(name, user_id, password);
        FirebaseFirestore.getInstance().collection("Users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toasty.success(RegistrationActivity.this, "User Registration Successful", Toasty.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(RegistrationActivity.this, "User Registration failed", Toasty.LENGTH_SHORT).show();
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
                                    Toasty.error(RegistrationActivity.this, "This user id already exists", Toasty.LENGTH_SHORT).show();
                                    return;
                                }
                                Log.d("debug", document.getId() + " => " + document.getData());
                            }
                            if(!isExistsUserID){
                                doRegistration();
                            }
                        } else {
                            Toasty.error(RegistrationActivity.this, "Failed to registration ", Toasty.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(RegistrationActivity.this, "Failed to registration", Toasty.LENGTH_SHORT).show();
                    }
                });
    }
}