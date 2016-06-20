package ndrwtrsk.weneedtotalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import ndrwtrsk.weneedtotalk.dummy.DummyContent;

import java.util.List;

public class ChatListActivity extends AppCompatActivity {


    //region Fields

    private static final String TAG = ChatListActivity.class.getCanonicalName();
    private boolean mTwoPane;

    private Integer usersId;

    @BindView(R.id.fab)
    FloatingActionButton mUiFab;

    @BindView(R.id.userId)
    EditText mUiUsersId;

    @BindView(R.id.chat_list)
    RecyclerView mUiChatRecycler;

    private DatabaseReference mDatabase;
    private ChatsAdapater chatsAdapater;
    private String emilyKey = "-abba";

//    private FirebaseRecyclerA

    //endregion Fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        Query myChats = FirebaseDatabase.getInstance().getReference().child("users/"+emilyKey+"/chats");
        chatsAdapater = new ChatsAdapater(myChats, FirebaseDatabase.getInstance());
        mUiChatRecycler.setAdapter(chatsAdapater);
        mUiUsersId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usersId = Integer.parseInt(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        if (findViewById(R.id.chat_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        usersId = savedInstanceState.getInt("id", -1);
        Log.d(TAG, "onRestoreInstanceState: restoring: " + usersId);
        if (usersId != -1){
            mUiUsersId.setText(usersId.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (usersId == null) {
            return;
        }
        outState.putInt("id", usersId);
        Log.d(TAG, "onSaveInstanceState: saving: " + usersId);
        super.onSaveInstanceState(outState);
    }
}
