package com.lb.richardk.lbfour;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;

    private EditText mEmail, mPassword, fName, lName, mNumber, mConfirmPassword;

    public String uid;

    private String email, password, confPassword, firstName, lastName, number;

    private TextView error;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("User");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        Rootref = FirebaseDatabase.getInstance().getReference();

        fName = (EditText) findViewById(R.id.FirstNameeditText);
        lName = (EditText) findViewById(R.id.LastNameeditText);
        mEmail = (EditText) findViewById(R.id.EmaileditText);
        mNumber = (EditText) findViewById(R.id.ContactNumbereditText);
        mPassword = (EditText) findViewById(R.id.PasswordeditText);
        mConfirmPassword = (EditText) findViewById(R.id.ConfirmPasswordeditText);

        error = (TextView)findViewById(R.id.errorText);

        Button nextbtn = (Button)findViewById(R.id.Nextbtn);
        nextbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                confPassword = mConfirmPassword.getText().toString();
                firstName = fName.getText().toString();
                lastName = lName.getText().toString();
                number = mNumber.getText().toString();

                if (TextUtils.isEmpty(fName.getText()))
                {
                    fName.setError("First name required");
                }
                else if (TextUtils.isEmpty(lName.getText()))
                {
                    lName.setError("Last name is required");
                }
                else if (TextUtils.isEmpty(mEmail.getText()))
                {
                    mEmail.setError("Email is required");
                }
                else if (TextUtils.isEmpty(mNumber.getText()))
                {
                    mNumber.setError("Mobile number is required");
                }
                else if (TextUtils.isEmpty(mPassword.getText()))
                {
                    mPassword.setError("Password is required");
                }
                else if (mPassword.getText().length()<6)
                {
                    mPassword.setError("Password must be atleast 6 characters long");
                }
                else if (TextUtils.isEmpty(mConfirmPassword.getText()))
                {
                    mConfirmPassword.setError("Confirmation password is required");
                }
                else if (!confPassword.equals(password))
                {
                    mConfirmPassword.setError("Passwords do not match");
                }
                else
                {
                    mAuth = FirebaseAuth.getInstance();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        String currentUserID = mAuth.getCurrentUser().getUid();
                                        Rootref.child("User").child(currentUserID).setValue("");


                                        Rootref.child("User").child(currentUserID).child("device_token")
                                                .setValue(deviceToken);

                                        NewUser newUser = new NewUser(firstName, lastName, number, email);

                                        mAuth.signInWithEmailAndPassword(email, password);

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        uid = user.getUid();

                                        myRef.child(uid).setValue(newUser);

                                        Intent startIntent = new Intent(getApplicationContext(), VehicleActivity.class);
                                        //show how to pass information to another activity
                                        startActivity(startIntent);
                                    }
                                    else
                                    {
                                        error.setTextColor(Color.RED);
                                        error.setText("That email address is already in use");
                                    }
                                }
                            });
                }
            }
        });
    }
}