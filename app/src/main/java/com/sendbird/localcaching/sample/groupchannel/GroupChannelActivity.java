package com.sendbird.localcaching.sample.groupchannel;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.localcaching.sample.R;
import com.sendbird.localcaching.sample.main.LoginActivity;
import com.sendbird.localcaching.sample.main.MessageActivity;
import com.sendbird.localcaching.sample.utils.PreferenceUtils;
import com.sendbird.localcaching.sample.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupChannelActivity extends BaseActivity {
    private LinearLayout layout;
    private String url = "http://10.68.140.2:8080/recommend/add";
    //private
    private TextView txtrecieverName;
    private String userId;
    OkHttpClient client = new OkHttpClient();
    Handler handler = new Handler();
    static Response response;
    ArrayList<Integer> userid;
    ArrayList<String> username;
    NewTask newTask = new NewTask();
    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        setContentView(R.layout.activity_group_channel);

        layout = findViewById(R.id.chat_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_group_channel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        userid = intent.getIntegerArrayListExtra("id");
        username = intent.getStringArrayListExtra("userName");
        String userName = username.get(0);//自己的名字
        userId = userid.get(0).toString();
        txtrecieverName = findViewById(R.id.Name);
        txtrecieverName.setText(userName);
        View divider = null;
        for (int i = 1; i < userid.size(); i++) {
            //EditText editText = new EditText(this);
            String chatId = userid.get(i).toString();

            Button button_chat_list = new Button(this);

            String chatName = username.get(i);
            //System.out.println(userInfo);
            button_chat_list.setText(chatName);
            button_chat_list.setGravity(Gravity.LEFT);
            button_chat_list.setAllCaps(false);
//            button_chat_list.setBackgroundResource(R.drawable.rounded_button);
            button_chat_list.setBackgroundColor(getResources().getColor(R.color.color_white));
            Drawable leftDrawable = getResources().getDrawable(R.drawable.profile1).mutate();
            leftDrawable.setBounds(0, 0, 220, 220);
            button_chat_list.setCompoundDrawables(leftDrawable, null, null, null);
            button_chat_list.setHeight(250);
            button_chat_list.setCompoundDrawablePadding(200);
            //userid.get(i)
            button_chat_list.setLayoutParams(new LinearLayout.
                    LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            button_chat_list.setOnClickListener(v -> {
                //传入自己的id和对方id
                connectToChat(chatId, chatName, userId, userName);
            });
            divider = new View(this);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
            dividerParams.setMargins(0, 0, 0,0);
            divider.setLayoutParams(dividerParams);
            int color = Color.parseColor("#7f7f7f");
            divider.setBackgroundColor(color);
            layout.addView(button_chat_list);
            layout.addView(divider);
        }
        findViewById(R.id.NewFriendButton).setOnClickListener(v -> {
            JSONObject json = new JSONObject();
            try {
                json.put("id", userId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            new NewTask().execute(json);
        });
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_group_channel);
//        setSupportActionBar(toolbar);



    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    private void connectToChat(final String chatID, final String chatName,final String userID, final String userName){
        Intent intent = new Intent(GroupChannelActivity.this, MessageActivity.class);
        intent.putExtra("RecieverId",chatID);
        intent.putExtra("RecieverName",chatName);
        intent.putExtra("UserId",userID);
        intent.putExtra("UserName",userName);
        startActivity(intent);
//        finish();

    }

    interface onBackPressedListener {
        boolean onBack();
    }
    private onBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener(onBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
//        if (mOnBackPressedListener != null && mOnBackPressedListener.onBack()) {
//            return;
//        }
//        super.onBackPressed();
        Intent intent = new Intent(GroupChannelActivity.this, LoginActivity.class);

        startActivity(intent);
        finish();
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

    void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
//    private class NewTask extends AsyncTask<JSONObject, Void, String> {
//
//        @Override
//        protected String doInBackground(JSONObject... jsonObjects) {
//
//
//            MediaType JSON = MediaType.get("application/json; charset=utf-8");
//            RequestBody requestBody = RequestBody.create(JSON, userId);
//            Request request = new Request.Builder()
//                    .url("http://16.163.58.122:8080/recommend/add")
//                    .post(requestBody)
//                    .build();
//
//            try {
//
//                        try {
//                            response = client.newCall(request).execute();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//
//                if (response.isSuccessful()) {
//                    String responseData = response.body().string();
//                    //System.out.println(responseData);
//                    JSONObject jsonObject = new JSONObject(responseData);
//                    System.out.println(responseData);
//                    userid.add(Integer.valueOf(jsonObject.getInt("id")));
//                    username.add(jsonObject.getString("name"));
//                    recreate();
//                }
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//        }
//    }
    private class NewTask extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... jsonObjects) {
            OkHttpClient client = new OkHttpClient();

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, jsonObjects[0].toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    //System.out.println(responseData);
                    return responseData;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                //System.out.println(result);
                ArrayList<Integer> id = new ArrayList<>();
                ArrayList<String> name = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("msg");
                    //登录成功
                    if(status.equals("ok")) {
                        //判断是登录 注册
                        //登录
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        System.out.println(message);
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            id.add(dataObject.getInt("id"));
                            name.add(dataObject.getString("username"));
                            userid.add(id.get(i));
                            username.add(name.get(i));
                            recreate();
                            System.out.println("Id: " + id.get(i) + ", Username: " + username.get(i));
                        }
                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }
        }
    }
}
