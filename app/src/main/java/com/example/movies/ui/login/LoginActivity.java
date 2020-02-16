package com.example.movies.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movies.R;
import com.example.movies.utilities.AnimationsManager;
import com.example.movies.utilities.DialogsManager;
import com.example.movies.utilities.KeyboardManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private void initComponents() {
        findViewById(R.id.login_btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        findViewById(R.id.profile_btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        findViewById(R.id.signIn_btnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        findViewById(R.id.signIn_btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSingIn();
            }
        });
        findViewById(R.id.login_btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
        ((TextView) findViewById(R.id.login_txtPassword)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                }
                return false;
            }
        });
        ((TextView) findViewById(R.id.signIn_txtPassword)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    register();
                }
                return false;
            }
        });
    }

    private void login() {
        final Resources resources = getResources();
        final Context context = getApplicationContext();
        String email = ((TextInputEditText) findViewById(R.id.login_txtEmail)).getText().toString();
        String password = ((TextInputEditText) findViewById(R.id.login_txtPassword)).getText().toString();
        if (email.matches(" *")) {
            DialogsManager.showToast(context, resources.getString(R.string.d_emptyEmail));
        } else if (password.matches(" *")) {
            DialogsManager.showToast(context, resources.getString(R.string.d_emptyPassword));
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        updateUI(mAuth.getCurrentUser(), null);

                    } else {
                        DialogsManager.showToast(context, resources.getString(R.string.d_loginDataIsIncorrect));
                    }
                }
            });
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        updateUI(null, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        mAuth = FirebaseAuth.getInstance();
        setTitle(getResources().getString(R.string.login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user, null);
        Picasso.get()
                .load("https://venngage-wordpress.s3.amazonaws.com/uploads/2018/09/Colorful-Geometric-Simple-Background-Image.jpg")
                .into((ImageView) findViewById(R.id.profile_ivBanner));
    }

    private void openLogin() {
        AnimationsManager.expand(findViewById(R.id.signIn_btnOpen));
        AnimationsManager.expand(findViewById(R.id.login_container));
        AnimationsManager.collapse(findViewById(R.id.signIn_container));
        AnimationsManager.collapse(findViewById(R.id.login_btnOpen));
    }

    private void openProfile(boolean condition) {
        if (condition) {
            findViewById(R.id.login_btnOpen).setVisibility(View.GONE);
            findViewById(R.id.signIn_btnOpen).setVisibility(View.GONE);
            findViewById(R.id.login_container).setVisibility(View.GONE);
            findViewById(R.id.signIn_container).setVisibility(View.GONE);
            findViewById(R.id.profile_container).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.login_btnOpen).setVisibility(View.GONE);
            findViewById(R.id.signIn_container).setVisibility(View.GONE);
            findViewById(R.id.signIn_btnOpen).setVisibility(View.VISIBLE);
            findViewById(R.id.profile_container).setVisibility(View.GONE);
            findViewById(R.id.login_container).setVisibility(View.VISIBLE);
        }
    }

    private void openSingIn() {
        AnimationsManager.collapse(findViewById(R.id.login_container));
        AnimationsManager.collapse(findViewById(R.id.signIn_btnOpen));
        AnimationsManager.expand(findViewById(R.id.signIn_container));
        AnimationsManager.expand(findViewById(R.id.login_btnOpen));
    }

    private void register() {
        final Context context = getApplicationContext();
        final Resources resources = getResources();
        final String username = ((TextInputEditText) findViewById(R.id.signIn_txtUserName)).getText().toString();
        String email = ((TextInputEditText) findViewById(R.id.signIn_txtEmail)).getText().toString();
        String password = ((TextInputEditText) findViewById(R.id.signIn_txtPassword)).getText().toString();
        if (username.matches(" *")) {
            DialogsManager.showToast(context, resources.getString(R.string.d_emptyUsername));
        } else if (email.matches(" *")) {
            DialogsManager.showToast(context, resources.getString(R.string.d_emptyEmail));
        } else if (password.matches(" *")) {
            DialogsManager.showToast(context, resources.getString(R.string.d_emptyPassword));
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username).build());
                        updateUI(user, username);
                    } else {
                        DialogsManager.showToast(context, resources.getString(R.string.d_userAlreadyRegistered));
                    }
                }
            });
        }
    }

    private void setUser(FirebaseUser user, String username) {
        Uri userPhotoURL = user.getPhotoUrl();
        Picasso.get().load(userPhotoURL != null ? userPhotoURL.getPath() : "https://yt3.ggpht.com/a/AGF-l7-AGSs_oCvyzyJVcw6-UTSzYHQXPhYeXYso1A=s900-c-k-c0xffffffff-no-rj-mo").into((ImageView) findViewById(R.id.profile_ivPhoto));
        ((TextView) findViewById(R.id.profile_txtUsername)).setText(username == null ? user.getDisplayName() : username);
        ((TextView) findViewById(R.id.profile_txtEmail)).setText(user.getEmail());
        ((TextView) findViewById(R.id.profile_txtRegistrationDate)).setText(new SimpleDateFormat("yyyy-MM-dd h:mm a").format(new Date(user.getMetadata().getCreationTimestamp())));
    }

    private void updateUI(FirebaseUser user, String username) {
        KeyboardManager.closeKeyboard(this);
        openProfile(user != null);
        if (user != null) {
            openProfile(true);
            setUser(user, username);
        }
    }
}
