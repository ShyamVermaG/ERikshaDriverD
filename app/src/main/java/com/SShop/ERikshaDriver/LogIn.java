package com.SShop.ERikshaDriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LogIn extends AppCompatActivity {


    TextView timer, ResendBtn;

    TextInputLayout mobileNo, password;
    LinearLayout formbox, otpbox, loginbox;

    //this is for otp
    String otpid;
    FirebaseAuth mAuth;
    TextInputLayout GetOtp;

    String userId;
    int Code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        timer = findViewById(R.id.timer);
        ResendBtn = findViewById(R.id.otpResend);

        //this is for Action bar
//        getSupportActionBar().hide();
        mobileNo = findViewById(R.id.mobNo);
        password = findViewById(R.id.password);

        ResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateotp();
            }
        });

        //here we initiallizing the mAuth otp
        mAuth = FirebaseAuth.getInstance();
    }

    //these all for validation
    private boolean validatePhone() {

        //its only
        String mobile = "91+";
        mobile += mobileNo.getEditText().getText().toString().trim();
        if (mobile.isEmpty()) {
            mobileNo.setError("Field can't be empty");
            return false;
        } else if (Patterns.PHONE.matcher(mobile).matches()) {
            mobileNo.setError("Please Enter only Phone number");
            return false;
        } else {
            mobileNo.setError(null);
            return true;
        }
    }

    private boolean validatePass() {
        String pass = password.getEditText().getText().toString().trim();

        if (pass.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean validateInternet(LogIn login) {
        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(LogIn.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
//            Toast.makeText(this,"internet is going on",Toast.LENGTH_SHORT).show();

            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage("Please Connect to internet to proceed further")
                    .setCancelable(false)
                    .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //here we open wifi setting
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), SplashScreen.class));    //there is slash screen
                        }
                    });
//            AlertDialog theme=builder.create();
//            theme.show();


            Toast.makeText(this, "Please check the internet connection", Toast.LENGTH_SHORT).show();

            return false;
        }
    }
    //this is the end of validation


    public void clickSignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }


    public void login(View view) {
        if (!validatePhone() || !validatePass() || !validateInternet(LogIn.this)) {

        } else {

            String mobT = mobileNo.getEditText().getText().toString().trim();


//            Toast.makeText(this, ""+mobT, Toast.LENGTH_SHORT).show();
            //here we check for the number is pre attached or not
            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverCon).orderByChild(KeyCon.ERikshaDriver.MobNo).equalTo(mobT).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

//                }
//            });
//            Query checkuser = df.;
//            checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String mobT = mobileNo.getEditText().getText().toString().trim();

                    int mlen = mobT.length();

                    //this is for setting up the Code
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(mobT.charAt(mlen - 2));
                    stringBuilder.append(mobT.charAt(mlen - 1));
                    //initiallize the Code
                    Code = Integer.parseInt(stringBuilder.toString());

                    String UserPass = password.getEditText().getText().toString().trim();

                    if (dataSnapshot.exists()) {

                        int i = 0;
                        //for run it only one time
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (i == 0) {
                                String passDb = snapshot.child("password").getValue(String.class);
                                UserPass = encode(UserPass);

                                if (passDb != null && passDb.equals(UserPass)) {
//                                    Toast.makeText(getApplicationContext(), "In", Toast.LENGTH_SHORT).show();


                                    //here we take userId
                                    userId = snapshot.getKey();
                                    //this isfor checking the value is matched or not


                                    //Now we are work on otp

                                    //this is all for display otp reciever and not display form
                                    formbox = findViewById(R.id.formBox);
                                    otpbox = findViewById(R.id.otpBox);
                                    loginbox = findViewById(R.id.loginBtn);
//                                    ViewGroup.LayoutParams formP = formbox.getLayoutParams();
//                                    ViewGroup.LayoutParams loginP = loginbox.getLayoutParams();

//                                    ViewGroup.LayoutParams otpP = otpbox.getLayoutParams();
//                                    formP.width = 1;
//                                    formbox.setMinimumWidth(1);
//                                    loginP.height = 1;
//                                    loginbox.setMinimumHeight(1);
//                                    otpbox.setMinimumWidth(0);
//                                    otpP.width = 0;
                                    formbox.setVisibility(View.GONE);
                                    loginbox.setVisibility(View.GONE);
                                    otpbox.setVisibility(View.VISIBLE);

                                    //here we sending the otp
//                            Toast.makeText(getApplicationContext(), "before otp", Toast.LENGTH_SHORT).show();

                                    initiateotp();
//                            Toast.makeText(getApplicationContext(), "after", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();

                                }

                            }
                            i++;
                        }

                    } else {

                        Toast.makeText(getApplicationContext(), "This Number is not Register", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), SignUp.class));
                        finish();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "Error occured // Please try again", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), e.getMessage()+"", Toast.LENGTH_SHORT).show();

                }
            });

//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//            });
        }
    }


    int timerT = 30;

    //here is the otp seder function
    private void initiateotp() {

        //hide resend start timer
        timerT = 30;
        ResendBtn.setTextColor(getResources().getColor(R.color.teal_200));
        ResendBtn.setClickable(false);
        startTimer();

        String mobileNum = "+91";
        mobileNum += mobileNo.getEditText().getText().toString().trim();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNum,
                30,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    //this is for manually
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid = s;
                    }

                    //this is for automatically otp picker
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        //this is for displaying errors
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        );
    }


    private void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timerT != 0) {
                    //for loop
                    timerT--;
                    startTimer();

                } else {
                    ResendBtn.setTextColor(getResources().getColor(R.color.teal_700));
                    ResendBtn.setClickable(true);
                }

                //print time
                timer.setText(String.valueOf(timerT) + " Sec");

            }
        }, 1000);

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

//                    Old Data
//                            this is all for signin successfull
                    //if any ways the otp is successfull than add the data into db
                    String mob = mobileNo.getEditText().getText().toString().trim();

                    //now we creating session with a single mobileNumber
                    //that is used for further information

                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.createLoginSession(userId);

                    //store session
                    FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverCon).child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String name = dataSnapshot.child(KeyCon.ERikshaDriver.Name).getValue(String.class);
                                sessionManager.createLoginSession(
                                        userId,
                                        "",
                                        name,
                                        dataSnapshot.child(KeyCon.ERikshaDriver.Address).getValue(String.class),
                                        dataSnapshot.child(KeyCon.ERikshaDriver.MobNo).getValue(String.class),
                                        dataSnapshot.child(KeyCon.ERikshaDriver.UsrImg).getValue(String.class),
                                        dataSnapshot.child(KeyCon.ERikshaDriver.OrgImg).getValue(String.class),
                                        4,
                                        "",
                                        KeyCon.ERikshaDriver.Value.LNG_ENG

                                );
                                Toast.makeText(LogIn.this, "Welcome Back Mr " + name, Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(LogIn.this, "Please try again", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(LogIn.this, "Please try again", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });

                    //now we starting another activity
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();


                } else {
                    Toast.makeText(getApplicationContext(), "Error occured during authe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {

//                    }
//                });
//    }


    //this function for otp verification onclick method

    public void verifyOtp(View view) {
        //check for empty field
        GetOtp = (TextInputLayout) findViewById(R.id.OTP);

        //check for empty
        if (GetOtp.getEditText().getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Field can't be empty ", Toast.LENGTH_SHORT).show();
        } else if (GetOtp.getEditText().getText().toString().trim().length() != 6) {
            Toast.makeText(getApplicationContext(), "Invalid Otp", Toast.LENGTH_SHORT).show();

        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, GetOtp.getEditText().getText().toString().trim());
            signInWithPhoneAuthCredential(credential);
        }

    }

    //this is for goto forgotten
    public void gotoForgotten(View view) {
//        startActivity(new Intent(getApplicationContext(), ForgottenPassword.class));
    }

    private String encode(String pas) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pas.length(); i++) {
            char x = pas.charAt(i);
            int asc = (int) x;
            asc += Code;
            while (asc > 126) {
                asc = 32 + (asc - 126);
            }


            x = (char) asc;
            stringBuilder.append(x);
        }
        return stringBuilder.toString();
    }


    //this is the end of all
}