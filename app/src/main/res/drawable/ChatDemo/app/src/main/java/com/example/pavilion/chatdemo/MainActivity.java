package com.example.pavilion.chatdemo;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText editMessage;
    private DatabaseReference mDatabase;
    private RecyclerView mMessageList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editMessage=(EditText) findViewById(R.id.editMessageE);
        mMessageList=(RecyclerView) findViewById(R.id.messageRec);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Messages");
        mMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(linearLayoutManager);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));

                }
            }
        };
    }

    public  void sendButtonClicked(View view){
        mCurrentUser= mAuth.getCurrentUser();
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
            final String messageValue=editMessage.getText().toString().trim();
             if( !TextUtils.isEmpty(messageValue)){
                 final DatabaseReference newPost= mDatabase.push();
                 mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         newPost.child("content").setValue(messageValue);
                         newPost.child("username").setValue(dataSnapshot.child("NAME").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {

                             }
                         });
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });
                mMessageList.scrollToPosition(mMessageList.getAdapter().getItemCount());
             }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<MsgOk,MsgOkViewHolder> FBRA= new FirebaseRecyclerAdapter<MsgOk, MsgOkViewHolder>(
                MsgOk.class,
                R.layout.singlemessagelayout,
                MsgOkViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MsgOkViewHolder viewHolder, MsgOk model, int position) {
                viewHolder.setContent(model.getContent());
                viewHolder.setUsername(model.getUsername());
            }
        };
        mMessageList.setAdapter(FBRA);
    }

    public static class MsgOkViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public MsgOkViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setContent(String content){
            TextView message_content=(TextView) mView.findViewById(R.id.messageText);
            message_content.setText(content);
        }
        public void setUsername(String username){
            TextView username_content=(TextView) mView.findViewById(R.id.usernameText);
            username_content.setText(username);
        }
    }

}
// 28:21 video will start