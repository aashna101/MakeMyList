package com.example.pavilion.makemylist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mRef, mRef1, mRef2, refRecord, userDelete;
    private AutoCompleteTextView quantity, Items;
    private ListView mList;
    int fetchedTime=0;
    int i;
    private ArrayAdapter itemAdapter, quantityAdapter, finalListAdapter;
    private Button ItemAdd , quantityAdd, addToList, removeItem, uploadList, sendList;
    String TAG= "Items name", ItemSelected;
    private ArrayList<String> itemList, quantityList, finalList;
    ProgressDialog pd;
    String number;
    Intent back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mList=(ListView)findViewById(R.id.myList);
        Items= (AutoCompleteTextView) findViewById(R.id.itemText);
        removeItem=(Button)findViewById(R.id.removeBtn);
        ItemAdd=(Button)findViewById(R.id.addItemBtn);
        quantityAdd= (Button) findViewById(R.id.addQuantityBtn);
        addToList=(Button)findViewById(R.id.addToListBtn);
        uploadList=(Button)findViewById(R.id.uploadListBtn);
        sendList=(Button)findViewById(R.id.sendListBtn);

        quantity=(AutoCompleteTextView)findViewById(R.id.quantityText);
        Toolbar mToolbar=(Toolbar)findViewById(R.id.ToolbarMy) ;
        setSupportActionBar(mToolbar);


        pd =new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setCancelable(false);
        // pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);


       quantity.setThreshold(1);
        Items.setThreshold(1);

        itemList =new ArrayList<>();
        quantityList=new ArrayList<>();
        finalList=new ArrayList<>();

         number= readFile(getApplicationContext());  // getting registered number;
        if( number.isEmpty()){
            back=new Intent(this,Main2Activity.class);
            startActivity(back);
        }
        else {
            refRecord = FirebaseDatabase.getInstance().getReference().child("MAKE_MY_LIST").child(number);
            mRef = refRecord.child("Item");
            mRef1 = refRecord.child("Quantity");
            mRef2 = refRecord.child("List");


            finalListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalList);
            mList.setAdapter(finalListAdapter);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ItemSelected = mList.getItemAtPosition(position).toString();
                    Toast.makeText(MainActivity.this, "Item selected:  " + ItemSelected, Toast.LENGTH_SHORT).show();

                }
            });

            quantityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, quantityList);
            quantity.setAdapter(quantityAdapter);

            itemAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, itemList);
            Items.setAdapter(itemAdapter);


            mRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    itemList =new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String value = ds.getValue(String.class);
                        itemList.add(value);
                        itemAdapter.notifyDataSetChanged();
                        Log.d(TAG, " " + value);
                    }

                    itemAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, itemList);
                    Items.setAdapter(itemAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "task cancelled", Toast.LENGTH_SHORT).show();
                }
            });                         //Value change of ITEM


            mRef1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    quantityList=new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String value = ds.getValue(String.class);
                        Log.d(TAG, " " + value);
                        quantityList.add(value);
                        quantityAdapter.notifyDataSetChanged();
                    }
                    quantityAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, quantityList);
                    quantity.setAdapter(quantityAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "task cancelled", Toast.LENGTH_SHORT).show();

                }
            });                        //Value change of Quantity


            quantity.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    quantity.showDropDown();
                }
            });


            Items.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Items.showDropDown();
                }
            });
        }                // else part closing of very beginning if's


        ItemAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:  {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;}
                    case MotionEvent.ACTION_UP:{
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;}
                }
                return false;
            }
        });

        quantityAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:  {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;}
                    case MotionEvent.ACTION_UP:{
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;}
                }
                return false;
            }
        });
        removeItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:  {
                        v.getBackground().setColorFilter(0x99FFFFFF, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;}
                    case MotionEvent.ACTION_UP:{
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;}
                }
                return false;
            }
        });
        addToList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:  {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;}
                    case MotionEvent.ACTION_UP:{
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;}
                }
                return false;
            }
        });
        uploadList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pd.setMessage("Please wait while uploading...");
                pd.show();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:  {
                        v.getBackground().setColorFilter(0x55000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;}
                    case MotionEvent.ACTION_UP:{
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;}
                }
                return false;
            }
        });
        sendList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:  {
                        v.getBackground().setColorFilter(0x55000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;}
                    case MotionEvent.ACTION_UP:{
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;}
                }
                return false;
            }
        });


}


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.fetchData) {

            if (fetchedTime == 0){

                pd.setMessage("Checking for your previous list!!!");
                pd.show() ;

                fetchedTime = 1;

            mRef2.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String value = ds.getValue(String.class);
                        finalList.add(value);
                        finalListAdapter.notifyDataSetChanged();
                        Log.d(TAG, " " + value);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "task cancelled", Toast.LENGTH_SHORT).show();

                }
            });
            pd.dismiss();
        }
        else if (fetchedTime==1){
                Toast.makeText(this, "You already fetched the list... clear all to fetch again", Toast.LENGTH_SHORT).show();
            }

        }
        else
            if (item.getItemId()== R.id.clearAll){
            finalList.clear();
            finalListAdapter.notifyDataSetChanged();
            fetchedTime=0;
            }

       else  if (item.getItemId() == R.id.delete){

            dialogShow(MainActivity.this);

            }
        return super.onOptionsItemSelected(item);
    }

       public void uploadItemBtnClicked(View view){

       String val= Items.getText().toString().trim();
        if(!val.isEmpty()){
       mRef.push().setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){

                   Toast.makeText(getApplicationContext(),"UPLOADED!!!",Toast.LENGTH_SHORT).show();
                   itemAdapter.notifyDataSetChanged();     // change  &&&&&&&&&&&&&&&&&&&&&&

                              }
               else {
                   Toast.makeText(getApplicationContext(),"Not uploaded",Toast.LENGTH_SHORT).show();
               }
           }
       });}else{
            Toast.makeText(getApplicationContext(),"Enter the Item first",Toast.LENGTH_SHORT).show();
        }
        pd.dismiss();
   }

       public void uploadQuantityBtnClicked(View view){

           pd.setMessage("Please wait while uploading...");
           pd.show();

       String val1= quantity.getText().toString().trim();
           if(!val1.isEmpty()){
       mRef1.push().setValue(val1).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){

                   Toast.makeText(getApplicationContext(),"UPLOADED!!!",Toast.LENGTH_SHORT).show();
               }
               else {
                   Toast.makeText(getApplicationContext(),"Not uploaded",Toast.LENGTH_SHORT).show();
               }
           }
       });}else{
               Toast.makeText(getApplicationContext(),"Enter the Quantity first",Toast.LENGTH_SHORT).show();
           }
           pd.dismiss();
   }

       public void addBtnClicked(View view){


       String itm=Items.getText().toString().trim();
       String qun=quantity.getText().toString().trim();
       if(!TextUtils.isEmpty(itm)){

           String newItem=itm+" "+qun;
           finalList.add(newItem);
           finalListAdapter.notifyDataSetChanged();

       }else{
           Toast.makeText(getApplicationContext(),"Enter any Item first",Toast.LENGTH_SHORT).show();
       }
       Items.setText("");
       quantity.setText("");
   }

       public void SaveListBtnClicked(View view){


           mRef2.removeValue();

           if(!finalList.isEmpty()){


               List list1= new ArrayList<>(finalList);
               mRef2.setValue(list1);
              // mList.setAdapter(null);
               finalList.clear();
               finalListAdapter.notifyDataSetChanged();
              // finalListAdapter.notifyDataSetChanged();

pd.dismiss();
           Toast.makeText(getApplicationContext(),"List Saved for next time",Toast.LENGTH_SHORT).show();
       }else{
               Toast.makeText(getApplicationContext(),"First create the list to save it",Toast.LENGTH_SHORT).show();
               pd.dismiss();
           }

       }

       public void removeListBtnClicked(View view){
      if(!finalList.isEmpty() && !ItemSelected.isEmpty()){
           finalList.remove(ItemSelected);
           finalListAdapter.notifyDataSetChanged();
           Toast.makeText(getApplicationContext(),"Item Removed",Toast.LENGTH_SHORT).show();
                          }
    else{
        Toast.makeText(getApplicationContext(),"Select the item to be deleted",Toast.LENGTH_SHORT).show();
    }}

       public  void senBtnClicked(View view){

    if(!finalList.isEmpty()){
        StringBuilder str=new StringBuilder();
        String[] arrStr=new String[finalList.size()];
        finalList.toArray(arrStr);

        for(i=0;i<arrStr.length;i++){
            str.append(arrStr[i]);
            str.append("\n");
        }



           // String smsNumber = "9212197079"; //without '+'
            try {
                Intent sendIntent = new Intent();//"android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, str.toString());
               // sendIntent.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(sendIntent,"send List Using..."));
            } catch(Exception e) {
                Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
            }
    }else{
        Toast.makeText(this, "Create the List first to send", Toast.LENGTH_SHORT).show();
    }        }


    private String readFile(Context context){
        String ret="";
        try{
            InputStream inputStream=context.openFileInput("config.txt");
            if (inputStream !=null){
                InputStreamReader inputStreamReader= new InputStreamReader(inputStream);
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                String receive="";
                StringBuilder stringBuilder=new StringBuilder();

                while ((receive=bufferedReader.readLine()) != null){
                    stringBuilder.append(receive);}
                inputStream.close();
                ret=stringBuilder.toString().trim();
            }
        }catch (FileNotFoundException e){
            Toast.makeText(context, "file not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            Toast.makeText(context, "cannot read file", Toast.LENGTH_SHORT).show();
        }
        return ret;
    }

    public void dialogShow(Context context){
        final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage(R.string.confirm).setTitle("Confirm your action!!!");
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.d("button pressed", "no button pressed");

            }
        }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userDelete=FirebaseDatabase.getInstance().getReference().child("USERS");
                userDelete.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren() ){
                            if (number .equals( ds.getValue(String.class))){
                                Log.d("datasnapshot value", "ds:   "+ ds.getValue(String.class));


                                String key= ds.getKey();
                                Log.d("key value", "key:   "+ ds.getKey());
                                userDelete.child(key).setValue(null);

                                File file = new File("config.txt");
                                file.delete();
                                Log.d("file deleted", "sucessfully file deleted...");
                                Toast.makeText(getApplicationContext(), "your number: "+ number+" key:    "+ key+
                                        "  is deleted sucessfully!!!",Toast.LENGTH_LONG).show();

                               // refRecord.setValue(null);
                                refRecord.removeValue();
                                Log.d("deleted", "list also deleted... ");
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                startActivity(intent);


                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        final AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();

    }
}
