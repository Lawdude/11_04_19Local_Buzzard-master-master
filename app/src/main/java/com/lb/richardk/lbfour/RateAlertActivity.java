package com.lb.richardk.lbfour;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RateAlertActivity extends AppCompatActivity
{
    private DatabaseReference myRef, Rootref, UserRef, VotesRef;

    public String alertID;
    public Integer voteChecker = 0;
    public Integer userVotes = 0;

    private String uid;

    public ImageButton upVote, downVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_alert);

        getIncomingIntent();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        uid = user.getUid();

        Rootref = FirebaseDatabase.getInstance().getReference();
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");
        VotesRef = FirebaseDatabase.getInstance().getReference().child("Votes").child(uid).child(alertID);

        myRef= FirebaseDatabase.getInstance().getReference().child("Alert").child(uid);
        myRef.keepSynced(true);

        Query query = myRef.child(alertID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        String key = user.getKey();
                        String value = user.getValue().toString();

                        if(key.equals("reg"))
                        {
                            ((TextView)findViewById(R.id.vehReg)).setText(value);
                        }
                        else if(key.equals("subject"))
                        {
                            ((TextView)findViewById(R.id.subject)).setText(value);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query votesQuery = VotesRef.child(alertID);
        votesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        String key = user.getKey();
                        String value = user.getValue().toString();

                        if(key.equals("vote"))
                        {
                            voteChecker = Integer.parseInt(value);

                            if(voteChecker == 1)
                            {
                                upVote.setBackgroundResource(R.drawable.upvotefull);
                                downVote.setBackgroundResource(R.drawable.downvoteempty);
                            }
                            else if(voteChecker == 2)
                            {
                                upVote.setBackgroundResource(R.drawable.upvoteempty);
                                downVote.setBackgroundResource(R.drawable.downvotefull);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query userQuery = UserRef.child(uid);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        String key = user.getKey();
                        String value = user.getValue().toString();

                        if(key.equals("votes"))
                        {
                            userVotes = Integer.parseInt(value);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        upVote = (ImageButton)findViewById(R.id.upVote);
        upVote.setOnClickListener(upVoteButtonHandler);

        downVote = (ImageButton)findViewById(R.id.downVote);
        downVote.setOnClickListener(downVoteButtonHandler);

    }

    View.OnClickListener upVoteButtonHandler = new View.OnClickListener() {

        public void onClick(View v) {

            if(voteChecker == 2) {

                upVote.setBackgroundResource(R.drawable.upvotefull);
                downVote.setBackgroundResource(R.drawable.downvoteempty);

                Rootref.child("Votes").child(uid).child(alertID).child("vote")
                        .setValue(1);

                Integer newUserVotes = userVotes + 2;
                String nuv = Integer.toString(newUserVotes);

                Rootref.child("User").child(uid).child("votes")
                        .setValue(nuv);
            }
            else if(voteChecker == 0)
            {
                upVote.setBackgroundResource(R.drawable.upvotefull);
                downVote.setBackgroundResource(R.drawable.downvoteempty);

                Rootref.child("Votes").child(uid).child(alertID).child("vote")
                        .setValue(1);

                Integer newUserVotes = userVotes + 1;
                String nuv = Integer.toString(newUserVotes);

                Rootref.child("User").child(uid).child("votes")
                        .setValue(nuv);
            }

        }
    };

    View.OnClickListener downVoteButtonHandler = new View.OnClickListener() {

        public void onClick(View v) {

            if(voteChecker == 1)
            {
                upVote.setBackgroundResource(R.drawable.upvoteempty);
                downVote.setBackgroundResource(R.drawable.downvotefull);

                Rootref.child("Votes").child(uid).child(alertID).child("vote")
                        .setValue(2);

                Integer newUserVotes = userVotes - 2;
                String nuv = Integer.toString(newUserVotes);

                Rootref.child("User").child(uid).child("votes")
                        .setValue(nuv);
            }
            else if(voteChecker == 0)
            {
                upVote.setBackgroundResource(R.drawable.upvoteempty);
                downVote.setBackgroundResource(R.drawable.downvotefull);

                Rootref.child("Votes").child(uid).child(alertID).child("vote")
                        .setValue(2);

                Integer newUserVotes = userVotes - 1;
                String nuv = Integer.toString(newUserVotes);

                Rootref.child("User").child(uid).child("votes")
                        .setValue(nuv);
            }
        }
    };

    private void getIncomingIntent()
    {
        if (getIntent().hasExtra("alertID"))
        {
            alertID = getIntent().getStringExtra("alertID");
        }
    }
}
