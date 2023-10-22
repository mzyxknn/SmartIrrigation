package com.example.smartirrigation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InsertNumber extends AppCompatActivity {

    EditText recipientName;
    EditText phoneNum;
    Button btnInsertData;
    DatabaseReference textRef; // Reference to the "Text" node
    DatabaseReference mobileNumbersRef;
    int maxDataCount = 5;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_number);

        //recipientName = findViewById(R.id.recipient);
        phoneNum = findViewById(R.id.number);
        btnInsertData = findViewById(R.id.btnInsertData);

        // Get a reference to the "Text" node
        textRef = FirebaseDatabase.getInstance().getReference("Text").child("MobileNumbers");



        // Add a listener to count the data entries in Firebase
        textRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dataCount = dataSnapshot.getChildrenCount();

                // Enable or disable the button based on the data count
                if (dataCount >= maxDataCount) {
                    btnInsertData.setEnabled(false);
                    btnInsertData.setText("Data Limit Reached");
                    Toast.makeText(InsertNumber.this, "You've reached the limit of 5 data entries.", Toast.LENGTH_SHORT).show();
                } else {
                    btnInsertData.setEnabled(true);
                    btnInsertData.setText("Insert Data");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors if needed
            }
        });


        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoneNum();
            }
        });
    }
    private void addPhoneNum() {
        String phone_num = phoneNum.getText().toString();

        // Check if the current count is less than 5
        textRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dataCount = dataSnapshot.getChildrenCount();
                if (dataCount < maxDataCount) {
                    // Generate an ID from 1 to 5 (inclusive) based on the current count
                    long newId = dataCount + 1;

                    // Set the phone number with the custom ID
                    textRef.child(String.valueOf(newId)).setValue(phone_num);

                    Toast.makeText(InsertNumber.this, "Data inserted!", Toast.LENGTH_SHORT).show();
                    // Clear the text in the EditText
                    phoneNum.setText("");
                } else {
                    Toast.makeText(InsertNumber.this, "Data limit reached (5 entries).", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors if needed
            }
        });
    }


}
