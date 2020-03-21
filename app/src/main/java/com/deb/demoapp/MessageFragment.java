package com.deb.demoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class MessageFragment extends Fragment {

    //Variables
    Spinner mSpinner;
    EditText mEditText;
    Button mButton;
    DatabaseReference root,local ;
    String ab;
    private ArrayAdapter<String> adapter;
    private List<String> arrayList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_message, container, false );
        mSpinner = rootView.findViewById(R.id.spinner);
        mButton = rootView.findViewById(R.id.bt_save);
        mEditText = rootView.findViewById(R.id.ed_msg);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagelist messagelist = new messagelist(mEditText.getText().toString());
                root.child("Messages").push().setValue(messagelist);
            }
        });
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.messages));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference();



    }

}
