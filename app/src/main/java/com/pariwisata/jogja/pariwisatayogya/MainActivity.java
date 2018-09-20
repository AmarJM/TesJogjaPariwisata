package com.pariwisata.jogja.pariwisatayogya;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.pariwisata.jogja.pariwisatayogya.Adapter.WisataAdapter;
import com.pariwisata.jogja.pariwisatayogya.Api.MyService;
import com.pariwisata.jogja.pariwisatayogya.Api.UtilsApi;
import com.pariwisata.jogja.pariwisatayogya.Model.ListTempat;
import com.pariwisata.jogja.pariwisatayogya.Model.SemuaTempat;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    List<SemuaTempat> mListTempat = new ArrayList<>();
    WisataAdapter mWisataAdapter;
    MyService myService;
    RecyclerView myRv;
    ProgressDialog loading;
    ImageView btnOut;
    TextView tvUser;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_main);
        btnOut = findViewById(R.id.imgSignout);
        tvUser = findViewById(R.id.tvUser);
        myRv = findViewById(R.id.rvTempat);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(MainActivity.this, 2);
        myRv.setLayoutManager(recyce);
        myRv.setItemAnimator(new DefaultItemAnimator());
        if (user.getDisplayName() == null) {
            tvUser.setText("Hai, " + user.getEmail());
            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText etName;
                    final ImageView btnSave;

                    dialog = new AlertDialog.Builder(view.getContext());
                    inflater = LayoutInflater.from(view.getContext());
                    final View dialogView = inflater.inflate(R.layout.name_dialog, null);
                    etName = dialogView.findViewById(R.id.etName);
                    dialog.setView(dialogView);
                    dialog.setTitle("");
                    dialog.setCancelable(false);
                    dialog.setIcon(R.drawable.userflat72);
                    dialog.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int which) {
                            // Do nothing but close the dialog

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString())
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Berhasil memperbarui profil", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Gagal " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                }
            });
        } else {
            tvUser.setText("Hai, " + user.getDisplayName());
        }
        getAllTempat();
        RelativeLayout myLayout = findViewById(R.id.activity_main);
        AnimationDrawable animationDrawable = (AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Keluar");
                builder.setMessage("Apakah anda yakin?");
                builder.setIcon(R.drawable.warningflat72);
                builder.setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        doLogout();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    private void doLogout() {
        auth.signOut();
    }

    private void getAllTempat() {


        loading = ProgressDialog.show(this, null, "Harap Tunggu...", true, false);
        myService = UtilsApi.getAPIService();
        Call<ListTempat> call = myService.getSemuaTempat();
        call.enqueue(new Callback<ListTempat>() {
            @Override
            public void onResponse(Call<ListTempat> call, Response<ListTempat> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    mListTempat = response.body().getData();
                    myRv.setAdapter(new WisataAdapter(MainActivity.this, mListTempat));
                }
            }

            @Override
            public void onFailure(Call<ListTempat> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }
}
