package ndrwtrsk.weneedtotalk;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Chat detail screen.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity}
 * on handsets.
 */
public class ChatDetailFragment extends Fragment {

    //region Fields & Ctr

    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = ChatDetailFragment.class.getCanonicalName();

    @BindView(R.id.messageRecycler)
    public RecyclerView messagesRecycler;
    FirebaseRecyclerAdapter<Message, MessageViewHolder> messagesAdapter;

    @BindView(R.id.composeMessage)
    public EditText composeMessageEditText;

    @BindView(R.id.sendButton)
    public Button sendButton;

    private String chatKey;
    private Chat chat;

    private CollapsingToolbarLayout appBarLayout;

    public String emilyKey = "-abba";

    public ChatDetailFragment() {
    }

    //endregion Fields & Ctr

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            chatKey = getArguments().getString(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Chat");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_detail, container, false);
        ButterKnife.bind(this, view);

        composeMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendButton.setEnabled((charSequence.toString().trim().length() > 0));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");

                Message message = new Message(emilyKey, composeMessageEditText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("chats-messages/"+chatKey).push().setValue(message);
                composeMessageEditText.setText("");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpWithFirebase();
    }

    private void setUpWithFirebase() {
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference chatRef = db.getReference("/chats/" + chatKey);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chat = dataSnapshot.getValue(Chat.class);
                chat.key = dataSnapshot.getKey();
                Log.d(TAG, "onDataChange: users in chat " + chat.users);
                int emilyIndex = chat.users.indexOf(emilyKey);
                int chattingUserKeyIndex = emilyIndex == 0 ? 1 : 0;
                final String chattingUserKey = chat.users.get(chattingUserKeyIndex);
                db.getReference("users/" + chattingUserKey).addListenerForSingleValueEvent(layoutTitleListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        messagesAdapter =
                new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                        Message.class,R.layout.item_message, MessageViewHolder.class, db.getReference("chats-messages/"+chatKey)) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                Log.d(TAG, "populateViewHolder: pos = " + position);
                Log.d(TAG, "populateViewHolder: message = " + model.message);
                viewHolder.bind(model, (Objects.equals(model.user, emilyKey)));
            }
        };
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messagesCount = messagesAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messagesCount - 1) && lastVisiblePosition == (positionStart -1 ))){
                    messagesRecycler.scrollToPosition(positionStart);
                }
            }
        });
        messagesRecycler.setLayoutManager(linearLayoutManager);
        messagesRecycler.setAdapter(messagesAdapter);
    }

    private ValueEventListener layoutTitleListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User chattingUser = dataSnapshot.getValue(User.class);
            appBarLayout.setTitle(chattingUser.name);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    public static ChatDetailFragment newInstance(String chatkey) {
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, chatkey);
        ChatDetailFragment fragment = new ChatDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message)
        public TextView messageTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Message message, boolean isOwned){
            messageTextView.setText(message.message);
            RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) messageTextView.getLayoutParams();
            int align = isOwned ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT;
            layout.addRule(align);

        }
    }

}
