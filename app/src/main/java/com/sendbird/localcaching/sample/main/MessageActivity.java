package com.sendbird.localcaching.sample.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sendbird.localcaching.sample.R;
import com.sendbird.localcaching.sample.groupchannel.GroupChannelActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TextView txtrecieverName, txtLastSeen;
    private String recieverName, recieverId;
    private String userId,userName;
    private DatabaseReference userRef, chatRef;
    private FirebaseAuth mAuth;
    private EditText edtMessage;
    private ImageButton btnSend;
    private RecyclerView mRecyleView;
    private MessageAdapter mMessageAdpater;
    public static ArrayList<String> messages, messagePosition, messageId;

    private WebSocketClient webSocketClient;

    // TODO HERE
    private String ID = "2";

    private EditText mes;
    private ImageButton send_btn;
    private String ip = "16.163.58.122";

    private String tmpContent;
    private int port = 8080;//电脑内未被占用的端口号

    private String msgRev;

    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mToolBar = findViewById(R.id.toolBarMessage);
        setSupportActionBar(mToolBar);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        getWindow().setBackgroundDrawableResource(R.drawable.bgmessage);

        txtrecieverName = findViewById(R.id.txtRecieverName);
        Intent intent = getIntent();
        //传参两处
        recieverId = intent.getStringExtra("RecieverId");
        recieverName = intent.getStringExtra("RecieverName");
        userId = intent.getStringExtra("UserId");
        userName = intent.getStringExtra("UserName");

        txtrecieverName.setText(recieverName);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        messages = new ArrayList<>();
        messagePosition = new ArrayList<>();
        messageId = new ArrayList<>();
        messages.add("Hi");
        messagePosition.add("1");
        messages.add("Hi");
        messagePosition.add("0");
        mRecyleView = findViewById(R.id.recyclerView);
        mRecyleView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        mMessageAdpater = new MessageAdapter(MessageActivity.this, messages);
        mRecyleView.setAdapter(mMessageAdpater);
//        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
//                List<String> sender = new ArrayList<>();sender.add("Hi");
//                List<String> receiver = new ArrayList<>();receiver.add("Hi");
        try {
            URI uri = new URI("ws://10.68.140.2:8080");
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "Opened connection");
                    send("[[userid]]" + userId);//1为userid
                }

                @Override
                public void onMessage(String message) {
                    handleNewMessageFromServer(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "Closed connection");
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        webSocketClient.send("[[userid]]"+"1");

//        getMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<String> sender = new ArrayList<>();sender.add("Hi");
//                List<String> receiver = new ArrayList<>();receiver.add("Hi");
//
//                for(Message mes : msgList) {
//                    if(mes.getFromId().equals(ID))sender.add(mes.getContent());
//                    else receiver.add(mes.getContent());
//                }
                if (edtMessage.getText().toString().equals("")) {
                    Toast.makeText(MessageActivity.this, "Enter Something.", Toast.LENGTH_SHORT).show();
                } else {
//                    new SendTask().execute();
                    try {

                        String string = edtMessage.getText().toString();
                        edtMessage.setText("");
                        Message message = new Message();
                        message.setContent(string);
                        message.setIsGroup(0);
                        message.setToId(Integer.parseInt(recieverId));
                        message.setFromId(Integer.parseInt(userId));
                        message.setType("text");
                        String tmp = JSON.toJSONString(message);
                        if (!TextUtils.isEmpty(string)) {
//                    SendMes(ip,port,tmp);
//                    if (webSocketClient != null && webSocketClient.isOpen()) {
//                        System.out.println(webSocketClient);
                            messages.add(string);
                            messagePosition.add("1");
                            messageId.add("2");
                            System.out.println("sdaiuhdiuahduiahdi");
                            mMessageAdpater.notifyItemInserted(messages.size() - 1);
                            mRecyleView.smoothScrollToPosition(messages.size() - 1);
                            webSocketClient.send(tmp);
//                    }
                            System.out.println();
                        } else {
                            Toast.makeText(MessageActivity.this, "Enter Something.", Toast.LENGTH_SHORT).show();
                            mes.requestFocus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    HashMap<String,String> map = new HashMap<>();
//                    map.put("SenderId",mAuth.getCurrentUser().getUid());
//                    map.put("Message",edtMessage.getText().toString());
//                    map.put("RecieverId",recieverId);
//                    chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task)
//                        {
//                            userRef.child(mAuth.getCurrentUser().getUid()).child("ChatLists").child(recieverId).setValue(ServerValue.TIMESTAMP);
//                            userRef.child(recieverId).child("ChatLists").child(mAuth.getCurrentUser().getUid()).setValue(ServerValue.TIMESTAMP);
//                            edtMessage.setText("");
//                        }
//                    });
                }
            }
        });

    }

    private void handleNewMessageFromServer(String message) {
        String tmp = JSON.parseObject(message, Message.class).getContent();
        String senderid = String.valueOf(JSON.parseObject(message, Message.class).getFromId());
        String receiveid = String.valueOf(JSON.parseObject(message, Message.class).getToId());
        if(receiveid.equals(userId) && recieverId.equals(senderid)) {
        messages.add(tmp);
        messagePosition.add("0");
        messageId.add("1");
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                mMessageAdpater.notifyItemInserted(messages.size() - 1);
                mRecyleView.smoothScrollToPosition(messages.size() - 1);
            }
        };
        handler.post(r);}

    }

//    interface onBackPressedListener {
//        boolean onBack();
//    }
//    private onBackPressedListener mOnBackPressedListener;
//
//    public void setOnBackPressedListener(onBackPressedListener listener) {
//        mOnBackPressedListener = listener;
//    }

//    @Override
//    public void onBackPressed() {
//        if (mOnBackPressedListener != null && mOnBackPressedListener.onBack()) {
//            return;
//        }
//        super.onBackPressed();
//        Intent intent = new Intent(this,GroupChannelActivity.class);
//
//        startActivity(intent);
//        finish();
//
//
//    }
@Override
public void onBackPressed() {
    super.onBackPressed();
}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//
    void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }




}
