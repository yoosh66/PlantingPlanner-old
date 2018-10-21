package com.yoonbae.plantingplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MyInfoActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView id;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ListView listView;
    private ArrayList<HashMap<String,String>> Data = new ArrayList<HashMap<String, String>>();
    private HashMap<String,String> InputData1 = new HashMap<>();
    private HashMap<String,String> InputData2 = new HashMap<>();
    private String token;
    private String provider;
    private AuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Uri photoUrl = mFirebaseUser.getPhotoUrl();
        imageViewLayout(photoUrl);

        bottomNavigationView();
        listViewLayout();

        id = findViewById(R.id.id);
        id.setText(mFirebaseUser.getEmail());
    }

    private void bottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(R.id.action_myInfo);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.action_calendar:
                        intent = new Intent(MyInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_list:
                        intent = new Intent(MyInfoActivity.this, ListActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void listViewLayout() {
        listView = findViewById(R.id.List_view);

        //데이터 초기화
        InputData1.put("menu","로그아웃");
        InputData2.put("menu", "계정삭제");
        Data.add(InputData1);
        Data.add(InputData2);

        //simpleAdapter 생성
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,Data, android.R.layout.simple_list_item_1, new String[]{"menu"}, new int[]{android.R.id.text1});
        listView.setAdapter(simpleAdapter);

        //onItemClickListener를 추가
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    String items[] = {"예", "아니오"};
                    Context context = view.getContext();
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    ab.setTitle("로그아웃 하시겠습니까?");
                    ab.setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                        public void onClick(DialogInterface dialog, int index) {
                            if(index == 0) {
                                mFirebaseAuth.signOut();

                                LoginManager loginManager = LoginManager.getInstance();
                                if(loginManager != null) {
                                    loginManager.logOut();
                                }

                                Intent intent = new Intent(MyInfoActivity.this, AuthActivity.class);
                                startActivity(intent);
                            } else if(index == 1) {
                                dialog.dismiss();
                            }

                            dialog.dismiss();
                        }
                    });
                    ab.show();
                } else if(position == 1) {
                    String items[] = {"예", "아니오"};
                    Context context = view.getContext();
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    ab.setTitle("계정을 삭제하면 식물정보도 모두 삭제됩니다. 계정을 삭제 하시겠습니까?");
                    ab.setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                        public void onClick(DialogInterface dialog, int index) {
                            if(index == 0) {
                                // get Token
                                provider = mFirebaseUser.getProviders().get(0);

                                if(mFirebaseUser.getProviders() != null) {
                                    if(provider.equals("google.com")) {
                                        token = "677847193937-qr7av5jubvngm6j73cc5oh6mebp2qcua.apps.googleusercontent.com";
                                        credential = GoogleAuthProvider.getCredential(token, null);

/*                                        mFirebaseUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                            @Override
                                            public void onSuccess(GetTokenResult getTokenResult) {
                                                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
                                                token = getTokenResult.getToken();
                                                credential = GoogleAuthProvider.getCredential(token, null);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MyInfoActivity.this, "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });*/
                                    } else if(provider.equals("facebook.com")) {
                                        token = AccessToken.getCurrentAccessToken().getToken();
                                        credential = FacebookAuthProvider.getCredential(token);
                                    }
                                }

                                mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(provider.equals("google.com")) {
                                            //mFirebaseAuth.signOut();
                                        } else if(provider.equals("facebook.com")) {
                                            //mFirebaseAuth.signOut();

                                            LoginManager loginManager = LoginManager.getInstance();
                                            if(loginManager != null) {
                                                loginManager.logOut();
                                            }
                                        }

                                        // 사용자 삭제
                                        mFirebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    if(provider.equals("google.com")) {
                                                        mFirebaseAuth.signOut();
                                                    }

                                                    startActivity(new Intent(MyInfoActivity.this, AuthActivity.class));
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                });
                            } else if(index == 1) {
                                dialog.dismiss();
                            }

                            dialog.dismiss();
                        }
                    });

                    ab.show();
                }
            }
        });
    }

    private void imageViewLayout(Uri photoUri) {
        imageView = findViewById(R.id.imageView);
        imageView.setBackground(new ShapeDrawable(new OvalShape()));
        imageView.setClipToOutline(true);
        Glide.with(imageView.getContext()).load(photoUri).into(imageView);
    }

}
