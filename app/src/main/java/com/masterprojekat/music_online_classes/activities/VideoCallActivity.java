package com.masterprojekat.music_online_classes.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.api.TermAPI;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.User;

import io.agora.agorauikit_android.AgoraConnectionData;
import io.agora.agorauikit_android.AgoraVideoViewer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoCallActivity extends AppCompatActivity {
    private static final String TAG = "VideoCallActivity";
    private final RetrofitService retrofitService = new RetrofitService();
    private final TermAPI termApi = retrofitService.getRetrofit().create(TermAPI.class);
    private static final String APP_ID = "4a0fccae93d14e3f976b5597717edef1";
    private static final int PERMISSION_REQ_CODE = 1;

    private AgoraVideoViewer agoraView;
    private User loggedInUser;
    private Term selectedTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_call);

        Intent intent = getIntent();
        loggedInUser = (User) intent.getSerializableExtra("loggedInUser");
        selectedTerm = (Term) intent.getSerializableExtra("selectedTerm");
        if(loggedInUser == null)
            return;

        if (hasPermissions()) {
            try {
                initAgora();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        }, PERMISSION_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_CODE &&
                grantResults.length >= 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            try {
                initAgora();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            Toast.makeText(this, "Permisije za kameru i audio su neophodne za video call!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initAgora() throws Exception {
        AgoraConnectionData connectionData = new AgoraConnectionData(APP_ID);
        agoraView = new AgoraVideoViewer(this, connectionData);

        FrameLayout container = findViewById(R.id.agora_container);
        container.addView(agoraView);

//        agoraView.join("MusicClass", null, null);
        getChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (agoraView != null) {
            agoraView.leaveChannel();
        }
    }

    private void getChannel() {
        termApi.getChannelName(selectedTerm.getTermId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful() && response.body() != null) {
                    String channelName = response.body();
                    System.out.println("Channel name " + channelName);
                    agoraView.join(channelName, null, null);
                }
                else {
                    Log.w(TAG, "Dohvatanje imena kanala nije uspesno: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanjem imena kanala nije uspeo!", throwable);
            }
        });
    }
}
