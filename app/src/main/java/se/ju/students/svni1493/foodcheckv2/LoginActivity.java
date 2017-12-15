package se.ju.students.svni1493.foodcheckv2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    //UI stuff
    private EditText mEmail,mPassword;
    private Button btnSignIn,btnRegister,btnForgotPass;
    private ProgressBar loginProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        btnSignIn = (Button) findViewById(R.id.login_sign_in_button);
        btnRegister = (Button) findViewById(R.id.login_sign_up_button);
        btnForgotPass = (Button) findViewById(R.id.login_reset_password_button);
        loginProgressBar = (ProgressBar) findViewById(R.id.login_progressBar);

        mAuth = FirebaseAuth.getInstance();

        //sign in action
        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                hideSoftKeyBoard();
                String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    toastMessage("Enter Your Email Adress!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    toastMessage("Enter A Password!!");
                    return;
                }
                loginProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                loginProgressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    toastMessage("Autication failed");
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

            }
        });
        //register action
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
        //forgot password action
        btnForgotPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });

    }
    //toastmessage function
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    //hide keyboard function
    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
