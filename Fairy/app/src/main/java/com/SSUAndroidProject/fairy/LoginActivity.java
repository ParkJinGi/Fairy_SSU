package com.SSUAndroidProject.fairy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.otto.Subscribe;
import com.unstoppable.submitbuttonview.SubmitButton;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private SubmitButton emailCreate;
    private SubmitButton emailLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Context mContext;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Bitmap bitmap;

    //다이얼로그 내의 edittext들
    private UserInfo userInfo;

    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        userInfo = mPushEvent.getUserinfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);
        emailCreate = (SubmitButton) findViewById(R.id.email_create_button);
        emailLogin = (SubmitButton) findViewById(R.id.email_login_button);
        BusProvider.getInstance().register(this);
        emailCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(mContext, CreateUser.class), 1);
            }
        });

        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(editTextEmail.getText().toString().trim().getBytes().length <= 1) && !(editTextPassword.getText().toString().trim().getBytes().length <= 1))
                    signinUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                else {
                    Toast.makeText(getApplicationContext(), "Email 및 비밀번호를 입력하시오.", Toast.LENGTH_SHORT).show();
                    emailLogin.doResult(false);
                    emailLogin.reset();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (userInfo != null) {
                if (!(userInfo.getEmail().trim().getBytes().length <= 1) &&
                        !(userInfo.getPassword().trim().getBytes().length <= 1) &&
                        !(userInfo.getName().trim().getBytes().length <= 1)) {
                    String filename = "Temp.png";
                    try {
                        FileInputStream TempStream = this.openFileInput(filename);
                        bitmap = BitmapFactory.decodeStream(TempStream);
                        TempStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    createUser(userInfo.getEmail(), userInfo.getPassword());
                } else {
                    emailCreate.doResult(false);
                    emailCreate.reset();
                    Toast.makeText(getApplicationContext(), "Email 및 비밀번호를 입력하시오.", Toast.LENGTH_SHORT).show();
                }
            } else {
                emailCreate.doResult(false);
                emailCreate.reset();
            }
        }
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            emailCreate.doResult(false);
                            emailCreate.reset();
                        } else {
                            saveProfile();
                            emailCreate.doResult(true);
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            emailCreate.reset();
                        }
                    }
                });
    }

    private void signinUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            emailLogin.doResult(true);
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplication(), MainActivity.class);
                            intent.putExtra("first",true);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 정보를 확인하시오.", Toast.LENGTH_SHORT).show();
                            emailLogin.doResult(false);
                            emailLogin.reset();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void saveProfile() {
        storageRef = storage.getReferenceFromUrl("gs://ssuandroid-1228.appspot.com");
        StorageReference riversRef = storageRef
                .child("Profile")
                .child(mAuth.getCurrentUser().getUid());
        try {
            byte[] tempbitmap = bitmapToByteArray(bitmap);
            UploadTask uploadTask = riversRef.putBytes(tempbitmap);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    userInfo.setProfile(downloadUrl.toString());
                    database.getReference().child("Users")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("Info")
                            .setValue(userInfo);
                }
            });
            tempbitmap = null;

        } catch (Exception e) {}
    }
    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 10, stream) ;
        byte[] byteArray = stream.toByteArray();
        try{
            stream.close();
        }catch (Exception e){}
        return byteArray ;
    }
}
