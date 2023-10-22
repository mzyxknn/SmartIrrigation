package com.example.smartirrigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewData extends AppCompatActivity {

    ListView myListview;
    List<String> phoneList; // Modified to store phone numbers as strings
    List<String> idList; // Added to store ID numbers

    DatabaseReference phoneNumRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        myListview = findViewById(R.id.myListView);
        phoneList = new ArrayList<>();
        idList = new ArrayList<>(); // Initialize the ID list

        phoneNumRef = FirebaseDatabase.getInstance().getReference("Text").child("MobileNumbers");
        phoneNumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phoneList.clear();
                idList.clear();

                for (DataSnapshot phoneDataSnapshot : dataSnapshot.getChildren()){
                    String phoneNumber = phoneDataSnapshot.getValue(String.class);
                    String id = phoneDataSnapshot.getKey(); // Get the ID

                    phoneList.add(phoneNumber);
                    idList.add(id);
                }

                ListAdapter adapter = new RealtimeHelperClass(ViewData.this, idList, phoneList);
                myListview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if needed
            }
        });

        // Set itemLong listener on listview item
        myListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber = phoneList.get(position);
                String firebaseId = idList.get(position); // Get the Firebase-generated key
                showUpdateDialog(firebaseId, phoneNumber); // Pass the Firebase-generated key
                return false;
            }
        });


    }

    private void showUpdateDialog(final String id, String phoneNumber) {
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mDialogView = inflater.inflate(R.layout.dialog_update, null);
        mDialog.setView(mDialogView);

        final EditText updatePhoneNumber = mDialogView.findViewById(R.id.updateNumber);
        Button btnUpdate = mDialogView.findViewById(R.id.updateBttn);
        Button btnDelete = mDialogView.findViewById(R.id.deleteBttn);

        mDialog.setTitle("Updating " + phoneNumber + " record");

        final AlertDialog alertDialog = mDialog.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPhoneNum = updatePhoneNumber.getText().toString();
                updateData(id, newPhoneNum);
                Toast.makeText(ViewData.this, "Record Updated", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(id);
                alertDialog.dismiss();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteRecord(String id) {
        // Create a reference to the database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Text").child("MobileNumbers");

        // Use the provided ID to delete the corresponding record
        Task<Void> mTask = dbRef.child(id).removeValue();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                showToast("Error deleting record");
            }
        });
    }

    private void updateData(String id, String phoneNumber) {
        // Create a reference to the database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Text").child("MobileNumbers").child(id);

        // Update the phone number
        dbRef.setValue(phoneNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Record Updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                showToast("Error updating record");
            }
        });
    }
}
