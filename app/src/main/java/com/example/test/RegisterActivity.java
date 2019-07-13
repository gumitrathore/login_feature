package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class RegisterActivity extends AppCompatActivity {
    private Button login;
    private TextView loginPage;
    private EditText mEmail,mName;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();
        mEmail =(EditText) findViewById(R.id.emailText);
        mName  =(EditText) findViewById(R.id.nameText);
        mPassword =(EditText) findViewById(R.id.passwordText);
        loginPage = findViewById(R.id.Login);
        loginPage.setText("Already a User? Login.");
        loginPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        });
        login =(Button) findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }
    private void startRegister(){
        if(!validate()){
            return;
        }

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String encryptedMsg = null;
        String qwertyasdfghg1 = "qwertyasdfghg1";
        try {
            encryptedMsg = AESCrypt.encrypt(qwertyasdfghg1, password);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.createUserWithEmailAndPassword(email,encryptedMsg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Success",Toast.LENGTH_LONG).show();
                        mEmail.setText("");
                        mPassword.setText("");
                        mName.setText("");
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    else
                    {
                        try
                        {
                            throw task.getException();
                        }
                        // if user enters wrong email.
                        catch (FirebaseAuthWeakPasswordException weakPassword)
                        {
                            //Log.d(TAG, "onComplete: weak_password");
                            mPassword.setError("Weak Password");
                            // TODO: take your actions!
                        }
                        // if user enters wrong password.
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                        {
                            //Log.d(TAG, "onComplete: malformed_email");
                            mEmail.setError("Enter Correct Email id");
                            // TODO: Take your action
                        }
                        catch (FirebaseAuthUserCollisionException existEmail)
                        {
                            // Log.d(TAG, "onComplete: exist_email");
                            //Toast.makeText(MainActivity.this,"USER ALREADY EXIST",Toast.LENGTH_LONG).show();
                            // TODO: Take your action
                            mEmail.setError("User Already Exist!");
                        }
                        catch (Exception e)
                        {
                            Log.d("erorr", "onComplete: " + e.getMessage());
                        }
                    }
                }
            });
        }
    }
    public boolean validate(){
        boolean allPassed = true;
        String name = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if(name.length()== 0){
            allPassed = false;
            mEmail.setError("Enter Name");
        }
        if(email.length()<5){
            allPassed = false;
            mEmail.setError("Enter Correct Email id");
        }
        if(password.length() < 5 ){
            allPassed = false;
            mPassword.setError("USE PASSWORD OF LENGTH 5 OR GREATER");
        }
        return  allPassed;

    }
}
