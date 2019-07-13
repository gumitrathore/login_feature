package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scottyab.aescrypt.AESCrypt;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private EditText mEmail;
    private TextView registerPage,forgotPass;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
       /* mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Toast.makeText(MainActivity.this,"Already Logged In!",Toast.LENGTH_LONG).show();
                }
            }
        };*/
        mEmail =(EditText) findViewById(R.id.emailText);
        mPassword =(EditText) findViewById(R.id.passwordText);
        registerPage = findViewById(R.id.registerNow);
        registerPage.setText("Register Now!");
        registerPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                return true;
            }
        });
        forgotPass = findViewById(R.id.forgotPass);
        forgotPass.setText("Forgot Password?");
        forgotPass.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String resetEmail = mEmail.getText().toString().trim();
                if (TextUtils.isEmpty(resetEmail)){
                    mEmail.setError("Enter Email");
                }
                else {
                    mAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Email Sent Successfully", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                try
                                {
                                    throw task.getException();
                                }

                                catch (FirebaseAuthInvalidUserException notexist)
                                {

                                    // TODO: Take your action
                                    Toast.makeText(MainActivity.this, "User Does not Exist!", Toast.LENGTH_LONG).show();
                                   // mEmail.setError("User Does not Exist!");
                                }
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    //Log.d(TAG, "onComplete: malformed_email");
                                    mEmail.setError("Enter Correct Email id");
                                    // TODO: Take your action
                                }
                                catch (Exception e)
                                {
                                    Log.d("erorr", "onComplete: " + e.getMessage());
                                }
                            }
                        }
                    });
                }
                return true;
            }
        });
        login =(Button) findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startLogin();
            }
        });

    }

   /* @Override
    protected void onStart() {
        super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
    } */

    private void startLogin(){
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
            mAuth.signInWithEmailAndPassword(email, encryptedMsg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public boolean validate(){
        boolean allPassed = true;

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if(email.length() == 0){
            allPassed = false;
            mEmail.setError("Enter Email id");
        }
        if(password.length() == 0 ){
            allPassed = false;
            mPassword.setError("Enter Password");
        }
      return  allPassed;

    }

}
