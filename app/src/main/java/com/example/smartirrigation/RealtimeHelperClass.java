package com.example.smartirrigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RealtimeHelperClass extends ArrayAdapter<String> {
    private TextView phoneNumber, idNumber;
    private Activity mContext;
    List<String> phoneList;
    List<String> idList; // Add a list to store the IDs

    public RealtimeHelperClass(Activity mContext, List<String> idList, List<String> phoneList) {
        super(mContext, R.layout.phonenum_list, phoneList);
        this.mContext = mContext;
        this.idList = idList; // Initialize the ID list
        this.phoneList = phoneList;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.phonenum_list, null, true);

        phoneNumber = listItemView.findViewById(R.id.phonenumber);
        idNumber = listItemView.findViewById(R.id.idnumber);

        String phoneNumberText = phoneList.get(position);
        String idText = idList.get(position); // Get the corresponding ID

        phoneNumber.setText(phoneNumberText);
        idNumber.setText(idText);

        return listItemView;
    }
}
