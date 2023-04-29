package com.sendbird.localcaching.sample.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.sendbird.android.SendBird;

import com.sendbird.localcaching.sample.R;
import com.sendbird.localcaching.sample.groupchannel.GroupChannelActivity;
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

public class LoginActivity extends BaseActivity {

    private CoordinatorLayout mLoginLayout;
    private TextInputEditText mUserIdConnectEditText, mUserNicknameEditText;
    private String url;

    private String url_Login="http://10.68.140.2:8080/login/go";
    private String url_Register="http://10.68.140.2:8080/login/register";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        //查找相应组件
        mLoginLayout = findViewById(R.id.layout_login);
        //两个文本编辑框
        mUserIdConnectEditText = findViewById(R.id.edittext_login_user_id);
        mUserNicknameEditText = findViewById(R.id.edittext_login_user_nickname);

        mUserIdConnectEditText = (TextInputEditText) findViewById(R.id.edittext_login_user_id);
        mUserNicknameEditText = (TextInputEditText) findViewById(R.id.edittext_login_user_nickname);

        mUserIdConnectEditText.setText(PreferenceUtils.getUserId());
        mUserNicknameEditText.setText(PreferenceUtils.getNickname());
//login button
        findViewById(R.id.button_login_connect).setOnClickListener(v -> {
            String userId = mUserIdConnectEditText.getText().toString();
            // Remove all spaces from userID
            userId = userId.replaceAll("\\s", "");
            String userNickname = mUserNicknameEditText.getText().toString();
            url=url_Login;

            PreferenceUtils.setUserId(userId);
            PreferenceUtils.setNickname(userNickname);
            JSONObject json = new JSONObject();
            try {
                json.put("username", userId);
                json.put("password", userNickname);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            //测试！！！
            //以下为模拟
//            ArrayList<Integer> id = new ArrayList<>();
//            ArrayList<String> username = new ArrayList<>();
//            id.add(1);
//            id.add(2);
//            id.add(3);
//            username.add("aaa");
//            username.add("Jerry");
//            username.add("Tom");
//            connect(id,username);

            new LoginTask().execute(json);

        });

//register button
        findViewById(R.id.button_login_connect2).setOnClickListener(v -> {
            String userId = mUserIdConnectEditText.getText().toString();
            // Remove all spaces from userID
            userId = userId.replaceAll("\\s", "");
            String userNickname = mUserNicknameEditText.getText().toString();
            url=url_Register;

            PreferenceUtils.setUserId(userId);
            PreferenceUtils.setNickname(userNickname);
            PreferenceUtils.setUserId(userId);
            PreferenceUtils.setNickname(userNickname);
            JSONObject json = new JSONObject();
            try {
                json.put("username", userId);
                json.put("password", userNickname);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            new LoginTask().execute(json);
        });

        mUserIdConnectEditText.setSelectAllOnFocus(true);
        mUserNicknameEditText.setSelectAllOnFocus(true);

        // Display current SendBird and app versions in a TextView
        String sdkVersion = String.format(getResources().getString(R.string.all_app_version),
                BaseApplication.VERSION, SendBird.getSDKVersion());
//        ((TextView) findViewById(R.id.text_login_versions)).setText(sdkVersion);
    }

    // Displays a Snackbar from the bottom of the screen
    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(mLoginLayout, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void connect(ArrayList id,ArrayList userName) {

        PreferenceUtils.setConnected(true);
        Intent intent = new Intent(LoginActivity.this, GroupChannelActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("userName",userName);

        startActivity(intent);
        finish();

    }




    private class LoginTask extends AsyncTask<JSONObject, Void, String> {

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
                ArrayList<String> username = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("msg");
                    //登录成功
                    if(status.equals("ok")) {
                        //判断是登录 注册
                        //登录
                        if (url==url_Login){

                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        System.out.println(message);
                        for (int i = 0; i < dataArray.length(); i++) {

                            JSONObject dataObject = dataArray.getJSONObject(i);
                            id.add(dataObject.getInt("id"));
                            username.add(dataObject.getString("username"));
                            System.out.println("Id: " + id.get(i) + ", Username: " + username.get(i));
                        }
                            connect(id,username);
                        }
                        //注册成功
                        else{
                            mUserIdConnectEditText.setText("");
                            mUserNicknameEditText.setText("");
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    message, Toast.LENGTH_LONG);
                            toast.show();

                        }
                    }
                    else{
                        //登录失败弹窗

                        System.out.println(message);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                message, Toast.LENGTH_LONG);
                        toast.show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }
        }
    }
}


