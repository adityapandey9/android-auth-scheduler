package com.example.adityapandey.applogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.adityapandey.applogin.Custom.MyTextView_Roboto_Regular;
import com.example.adityapandey.applogin.model.Login;
import com.example.adityapandey.applogin.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.orm.SugarContext;
import com.orm.SugarRecord;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /*
    UserLogin or Not base
 */
    public static final String PREFS_NAME = "MyAppLogin";
    public static final String USER = "user";


    private MyTextView_Roboto_Regular normal_login,google_login;
    private ImageView imageView;
    private FrameLayout frameLayout;

    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        normal_login = (MyTextView_Roboto_Regular) findViewById(R.id.login_normal);
        google_login = (MyTextView_Roboto_Regular) findViewById(R.id.google_text);
        imageView = (ImageView) findViewById(R.id.main_img);
        frameLayout = (FrameLayout) findViewById(R.id.main_body);
        applyBlur();
        SugarContext.init(getApplicationContext());
        SugarRecord.executeQuery(" CREATE TABLE IF NOT EXISTS LOGIN ( " +
                "ID INTEGER PRIMARY KEY, " +
                "EMAIL TEXT NOT NULL, " +
                "PASSWORD TEXT NOT NULL " +
                "); ");
        SugarRecord.executeQuery(" CREATE TABLE IF NOT EXISTS USER ( " +
                "ID INTEGER PRIMARY KEY, " +
                "USERID INTEGER NOT NULL, " +
                "NAME TEXT NOT NULL, " +
                "SPORTS INTEGER NOT NULL, " +
                "RESEARCH INTEGER NOT NULL, " +
                "TECH INTEGER NOT NULL, " +
                "ENTERTAINMENT INTEGER NOT NULL, " +
                "STARTDATE TEXT NOT NULL, " +
                "ENDDATE TEXT NOT NULL, " +
                "FOREIGN KEY(USERID) REFERENCES LOGIN(ID)"+
                "); ");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(SugarRecord.count(Login.class)==0){
            Login login = new Login("foo1@exa.com","1234");
            Login login1 = new Login("foo2@exa.com","1234");
            Login login2 = new Login("foo3@exa.com","1234");
            SugarRecord.saveInTx(login, login1, login2);
        }
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int user = settings.getInt(USER, -1);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if(account != null)
//            signOut();
        if(user != -1){
            Intent intent = new Intent(getApplicationContext(), UserForm.class);
            startActivity(intent);
        }
        normal_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void signOut() {
       mGoogleSignInClient.signOut();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct != null) {
                String email = acct.getEmail();
                ArrayList<Login> loginlist = new ArrayList<>(Login.find(Login.class, "EMAIL = ?", new String[]{email}, null, null, "1"));
                if(loginlist.size()>0){
                    // Storing user id
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(USER, loginlist.get(0).id);
                    editor.apply();
                } else {
                    Login login = new Login(email, "default1234");
                    login.save();
                    ArrayList<Login> loginlist1 = new ArrayList<>(Login.find(Login.class, "EMAIL = ?", new String[]{email}, null, null, "1"));
                    if(loginlist1.size()>0){
                        // Storing user id
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(USER, loginlist1.get(0).id);
                        editor.apply();
                    }
                }
                Intent intent = new Intent(getApplicationContext(), UserForm.class);
                startActivity(intent);
            }
            signOut();
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(getApplicationContext(), "Some error Occured, Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyBlur() {
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                imageView.buildDrawingCache();

                Bitmap bmp = imageView.getDrawingCache();
                blur(bmp, frameLayout);
                return true;
            }
        });
    }

    private void blur(Bitmap bkg, View view) {
        float radius = 20;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()),
                (int) (view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);

        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bkg, 0, 0, null);

        RenderScript rs = RenderScript.create(MainActivity.this);

        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);

        view.setBackground(new BitmapDrawable(
                getResources(), overlay));

        rs.destroy();
    }


}
