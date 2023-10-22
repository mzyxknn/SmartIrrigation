package com.example.smartirrigation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertNumber extends AppCompatActivity {

    EditText recipientName;
    EditText phoneNum;
    Button btnInsertData;
    DatabaseReference phoneNumRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_number);

        recipientName = findViewById(R.id.recipient);
        phoneNum = findViewById(R.id.number);
        btnInsertData = findViewById(R.id.btnInsertData);
        phoneNumRef = FirebaseDatabase.getInstance().getReference("Phone Numbers");

        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoneNum();
            }
        });
    }
    private void addPhoneNum(){
        String name = recipientName.getText().toString();
        String phone_num = phoneNum.getText().toString();

        String id = phoneNumRef.push().getKey();

        PhoneNumber phonenum = new PhoneNumber(id,name,phone_num);
        assert id != null;
        phoneNumRef.child(id).setValue(phonenum);
        Toast.makeText(InsertNumber.this,"Data inserted!",Toast.LENGTH_SHORT).show();
    }
}