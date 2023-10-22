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
    List<PhoneNumber> phoneList;

    DatabaseReference phoneNumRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        myListview = findViewById(R.id.myListView);
        phoneList = new ArrayList<>();

        phoneNumRef = FirebaseDatabase.getInstance().getReference("Phone Numbers");
        phoneNumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phoneList.clear();

                for (DataSnapshot studentDatasnap : dataSnapshot.getChildren()){
                    PhoneNumber phonenum = studentDatasnap.getValue(PhoneNumber.class);
                    phoneList.add(phonenum);
                }

                ListAdapter adapter = new RealtimeHelperClass(ViewData.this,phoneList);
                myListview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set itemLong listener on listview item

        myListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                PhoneNumber phonenum = phoneList.get(position);
                showUpdateDialog(phonenum.getId(), phonenum.getName());

                return false;
            }
        });
    }

    private void showUpdateDialog(final String id, String name){

        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mDialogView = inflater.inflate(R.layout.dialog_update, null);

        mDialog.setView(mDialogView);

        //create views refernces
        final EditText updateRecipientName = mDialogView.findViewById(R.id.updateRecipient);
        final EditText updatePhoneNumber = mDialogView.findViewById(R.id.updateNumber);
        Button btnUpdate = mDialogView.findViewById(R.id.updateBttn);
        Button btnDelete = mDialogView.findViewById(R.id.deleteBttn);

        mDialog.setTitle("Updating " + name +" record");

        final AlertDialog alertDialog = mDialog.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here we will update data in database
                //now get values from view

                String newName = updateRecipientName.getText().toString();
                String newPhoneNum = updatePhoneNumber.getText().toString();


                updateData(id,newName,newPhoneNum);

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

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteRecord(String id){
        //create reference to database
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference("Phone Numbers").child(id);
        //we referencing child here because we will be delete one record not whole data data in database
        //we will use generic Task here so lets do it..

        Task<Void> mTask = DbRef.removeValue();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Error deleting record");
            }
        });
    }

    private void updateData(String id, String name, String rollno){

        //creating database reference
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference("Phone Numbers").child(id);
        PhoneNumber phonenum = new PhoneNumber(id, name, rollno);
        DbRef.setValue(phonenum);
    }
}