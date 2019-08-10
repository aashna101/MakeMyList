package com.example.pavilion.makemylist;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private  int res;
    private EditText numberText;
    private DatabaseReference refNumber;
    private String number;
    private List<String> getUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        refNumber=FirebaseDatabase.getInstance().getReference().child("USERS");
        getUsers=new ArrayList<>();

        Toolbar mToolbar=findViewById(R.id.ToolbarMy) ;
        setSupportActionBar(mToolbar);

        numberText=findViewById(R.id.EnterNumberText);

        refNumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()){
                    Log.d("USERS", "   "+snap.getValue()+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    getUsers.add(snap.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SaveAndRegisterMeBtn(View view){
        number=numberText.getText().toString().trim();
       int a= checkUser(number);
    if (a==1){
        refNumber.push().setValue(number);
        Toast.makeText(Main2Activity.this, "You are Successfully registered!!!", Toast.LENGTH_SHORT).show();
                }

        writeFile(number,getApplicationContext());


        Intent next = new Intent(Main2Activity.this,MainActivity.class);
        startActivity(next);
       }

    private int checkUser(final String data){
       res=1;
        for(int i=0;i<getUsers.size();i++){
            if (getUsers.get(i).equals(data)){
                Toast.makeText(Main2Activity.this, "This number is already exist...Welcome Again!!!", Toast.LENGTH_SHORT).show();
                res=2;
                return res;
            }else{ res=1; }
        }

        return res;
    }

    private void writeFile(String data, Context context){
        try{
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(context.openFileOutput("config.txt",Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();

        }
        catch (IOException e){
            Log.e("Exception!!!", "writeFileFailed   : "+e.toString());
        }
    }

}
