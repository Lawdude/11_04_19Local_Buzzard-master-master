package com.lb.richardk.lbfour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SendAlertActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference messRef = database.getReference("Alert");
    DatabaseReference myMessRef = database.getReference("Message");

    public DatabaseReference myRef, voteRef;

    private EditText aRegistration, aMessage;
    private Spinner subject;

    public String value;
    public String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_alert);

        aRegistration = (EditText) findViewById(R.id.ViewMakeeditText);
        subject = (Spinner) findViewById(R.id.Subjectspinner2);

        Spinner mySpinner = (Spinner) findViewById(R.id.Subjectspinner2);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SendAlertActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Subject));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        voteRef = FirebaseDatabase.getInstance().getReference().child("Votes");

        aRegistration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable arg) {

                String s = arg.toString();
                if(!s.equals(s.toUpperCase())){
                    s = s.toUpperCase();
                    aRegistration.setText(s);
                    aRegistration.setSelection(aRegistration.getText().length());

                }

            }
        });

        Button nextbtn = (Button)findViewById(R.id.Sendbtn);
        nextbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            String vReg = aRegistration.getText().toString();
            // String mess = aMessage.getText().toString();
            String sub = subject.getSelectedItem().toString();
            int vote = 0;

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            uid = user.getUid();

            final Alert alert = new Alert(vReg, sub, uid, vote);

            myRef = FirebaseDatabase.getInstance().getReference().child("Registration").child(vReg);

                // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if(dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            value = ds.getValue(String.class);
                            Log.d("success", "Value is: " + value);

//                          value = dataSnapshot.getValue(String.class);
                            Log.d("success", "Value is: " + value);
                            String key = messRef.child(value).push().getKey();
                            messRef.child(value).child(key).setValue(alert);
                            voteRef.child(value).child(key).child("vote").setValue(0);
                            myMessRef.child(uid).push().setValue(alert);

                            Intent startIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(startIntent);
                        }
                    }
                    else
                    {
                        aRegistration.setError("The registration entered is not registered");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("fail", "Failed to read value.", error.toException());
                }
            });
            }
        });
    }
}