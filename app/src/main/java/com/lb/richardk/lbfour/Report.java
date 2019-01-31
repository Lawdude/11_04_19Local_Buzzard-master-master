package com.lb.richardk.lbfour;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Report
{
    public String subject;
    public String message;

    public Report()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Report(String sub, String mess)
    {
        this.subject = sub;
        this.message = mess;
    }
}
