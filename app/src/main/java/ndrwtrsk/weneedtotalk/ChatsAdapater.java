package ndrwtrsk.weneedtotalk;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matteo.firebase_recycleview.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 20.06.2016.
 */
public class ChatsAdapater
        extends RecyclerView.Adapter<ChatsAdapater.ChatItemViewHolder>
        implements ChildEventListener {

    private static final String TAG = ChatsAdapater.class.getCanonicalName();

    public List<Chat> chats = new LinkedList<>();
    public List<String> chatsKeys = new ArrayList<>();

    Query mChatsQuery;
    private FirebaseDatabase db;

    public ChatsAdapater(Query chatsQuery, FirebaseDatabase db) {
        mChatsQuery = chatsQuery;
        this.db = db;
        mChatsQuery.addChildEventListener(this);
    }

    @Override
    public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatItemViewHolder holder, int position) {
        holder.bind(chats.get(position));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    //region ChildEventListener methods

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, final String s) {
        Log.d(TAG, "onChildAdded: ");
        final String val = (String) dataSnapshot.getValue();
            db.getReference("chats/" + val).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (chatsKeys.contains(val)) {
                        String changedChatKey = dataSnapshot.getKey();
                        int changedIndex = chatsKeys.indexOf(changedChatKey);
                        Chat newChat = dataSnapshot.getValue(Chat.class);
                        chats.set(changedIndex, newChat);
                        notifyItemChanged(changedIndex);
                        return;
                    }
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    Log.d(TAG, "onDataChange: chat title = " + chat.title);
                    int insertedPos;
                    if (s == null){
                        chatsKeys.add(0, val);
                        chats.add(0, chat);
                        insertedPos = 0;
                    } else {
                        int previousIndex = chatsKeys.indexOf(s);
                        int nextIndex  = previousIndex + 1;
                        if (nextIndex == chats.size()){
                            chats.add(chat);
                            chatsKeys.add(val);
                        } else {
                            chats.add(nextIndex, chat);
                            chatsKeys.add(nextIndex, val);
                        }
                        insertedPos = nextIndex;
                    }
                    notifyItemInserted(insertedPos);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildChanged: ");
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved: ");
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildMoved: ");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCancelled: ");
    }

    //endregion ChildEventListener methods

    public class ChatItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title)
        public TextView title;

        public ChatItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Chat chat) {
            title.setText(chat.title);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: Clicked ChatItemViewHolder");
        }
    }
}
