package com.deb.demoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    //Variables
    Spinner mSpinner;
    EditText mEditText;
    Button mButton;
    DatabaseReference root,local ;
    String ab;
    private ArrayAdapter<String> adapter;
   ArrayList<String> arrayList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_message, container, false );
        mSpinner = rootView.findViewById(R.id.spinner);
        mButton = rootView.findViewById(R.id.bt_save);
        mEditText = rootView.findViewById(R.id.ed_msg);
        arrayList = new ArrayList<String>();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                messagelist messagelist = new messagelist(mEditText.getText().toString());
                DatabaseReference newChildRef = local.push();
                String key = newChildRef.getKey();
                local.child(key).setValue(mEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Added Successfully",Toast.LENGTH_SHORT).show();
                    }
                });
                mEditText.setText("");
                arrayList.clear();
            }
        });
        local.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item:dataSnapshot.getChildren()){
                    arrayList.add(item.getValue().toString());
                }
                ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item,arrayList);
                mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(mAdapter);
               mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference();
        local = FirebaseDatabase.getInstance().getReference("/Messages");





    }

}
