package com.example.smartirrigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RealtimeHelperClass extends ArrayAdapter {

    private TextView recipientName,phoneNumber;
    private Activity mContext;
    List<PhoneNumber> phoneList;

    public RealtimeHelperClass(Activity mContext, List<PhoneNumber> phoneList){
        super(mContext,R.layout.phonenum_list,phoneList);
        this.mContext = mContext;
        this.phoneList = phoneList;
    }


    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.phonenum_list,null,true);

        recipientName = listItemView.findViewById(R.id.recipientname);
        phoneNumber = listItemView.findViewById(R.id.phonenumber);


        PhoneNumber phonenum = phoneList.get(position);

        recipientName.setText(phonenum.getName());
        phoneNumber.setText(phonenum.getPhoneNum());


        return listItemView;
    }
}