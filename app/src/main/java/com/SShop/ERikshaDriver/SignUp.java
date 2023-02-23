package com.SShop.ERikshaDriver;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class SignUp extends AppCompatActivity {





    DatabaseReference df;
    String ValidChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    String orgImg="";
    String userImgF = "me";
    ImageView img;



    //some attributes
    TextInputLayout name, address, landMark, mobileNo, password, second;
    LinearLayout otpbox;
    NestedScrollView formbox;

    //this is all for otp
    String otpid;
    FirebaseAuth mAuth;
    TextInputLayout GetOtp;

    int Code = 0;


    String finalPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        address = findViewById(R.id.address);

        img = findViewById(R.id.imgProfile);


        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.mobNo);
        password = findViewById(R.id.pass);
        second = findViewById(R.id.repass);

        //here we initiallizing the mAuth otp
        mAuth = FirebaseAuth.getInstance();

    }




    //these are all for checking all input field

    private boolean validateName() {
        String nam = name.getEditText().getText().toString().trim();
        if (nam.isEmpty()) {
            name.setError("Field can't be empty");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    private boolean validateMohalla() {
        String add = address.getEditText().getText().toString().trim();
        if (add.isEmpty()) {
            address.setError("Field can't be empty");
            return false;
        } else {
            address.setError(null);
            return true;
        }
    }

    private boolean validateLandMark() {
        String add = landMark.getEditText().getText().toString().trim();
        if (add.isEmpty()) {
            landMark.setError("Field can't be empty");
            return false;
        } else {
            landMark.setError(null);
            return true;
        }
    }

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

        String pass = password.getEditText().getText().toString();
        boolean check = true;

//        ArrayList<Character> ValidChar=new ArrayList<>();
//        ValidChar.add('A');


        if (pass.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        } else {
            //this is for checking the password is outside the valid chars
            int matchTime = 0;
            for (int i = 0; i < pass.length(); i++) {

                for (int j = 0; j < ValidChar.length(); j++) {
                    if (pass.charAt(i) == ValidChar.charAt(j)) {
                        matchTime++;
                    }
                }
            }

            //condition
            if (matchTime == pass.length()) {
                password.setError(null);
                return true;
            } else {
                Toast.makeText(this, "Password must be in A-Z , a-z ,0-9", Toast.LENGTH_SHORT).show();
                return false;
            }

        }


    }

    private boolean validateRePass() {

        String repass = second.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();

        if (repass.isEmpty()) {
            second.setError("Field can't be empty");
            return false;
        } else {
            if (pass.equals(repass)) {
                second.setError(null);
                return true;
            } else {
                second.setError("doesn't matched");
                return false;
            }

        }
    }


    public void submit(View view) {

        if (!validateName() || !validatePhone() || !validatePass() || !validateRePass()  ) {
            //this is for any error
            Toast.makeText(this, "Your data is incorrect", Toast.LENGTH_SHORT).show();


        } else {

            submitGo();

        }


    }

    private void submitGo() {

        //this is for submit
        //now we check both  password are same or not
        String pass = password.getEditText().getText().toString().trim();
        String secon = second.getEditText().getText().toString().trim();
        //this is for password mateches in between them;
        if (pass.equals(secon)) {
            //then submit it on database
            Toast.makeText(this, "Your data is recieved", Toast.LENGTH_SHORT).show();

            //this is for setting up the Code
            String mobile = mobileNo.getEditText().getText().toString().trim();
            int mLen = mobile.length();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mobile.charAt(mLen - 2));
            stringBuilder.append(mobile.charAt(mLen - 1));
            //initiallize the Code
            Code = Integer.parseInt(stringBuilder.toString());


            //this is for checking the mobile number is not pre register or not
            String mobT = mobileNo.getEditText().getText().toString().trim();

            //here we check for the number is pre attached or not
            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverCon).equalTo(mobT, KeyCon.ERikshaDriver.MobNo).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "MobileNo already exists", Toast.LENGTH_SHORT).show();
                    } else {

                        //Now we are work on otp

                        //this is all for display otp reciever and not display form
                        formbox = findViewById(R.id.formBox);
                        otpbox = findViewById(R.id.otpBox);
//                            ViewGroup.LayoutParams formP=formbox.getLayoutParams();
//                            ViewGroup.LayoutParams otpP=otpbox.getLayoutParams();
//                            formP.width=1;
//                            formbox.setMinimumWidth(1);
//                            otpbox.setMinimumWidth(0);
//                            otpP.width=0;
                        formbox.setVisibility(View.GONE);
                        otpbox.setVisibility(View.VISIBLE);

                        //here we sending the otp
                        initiateotp();


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_SHORT).show();



                }
            });
//        addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//            });
//

        }

    }


    //here is the otp seder function
    private void initiateotp() {

        String mobileNum = "+91";
        mobileNum += mobileNo.getEditText().getText().toString().trim();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNum,
                60,
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


//                            this is all for signin successfull
                            //here first we taking all the data
                            Calendar calendar = Calendar.getInstance();
                            String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
                            String nam = name.getEditText().getText().toString().trim();
                            String mob = mobileNo.getEditText().getText().toString().trim();
                            String pass = password.getEditText().getText().toString().trim();
                            String addr = address.getEditText().getText().toString().trim();

                            pass = encode(pass);  //data will incoded


                            //here we first upload image if available
                            //now we first upload image than we update data

                            String userId = String.valueOf(System.currentTimeMillis());


                            if (userImgF.equals("me")) {
                                //this is for if user doesn't select Img


                                //this is for creating data class instance
                                SignUpData refDB = new SignUpData(nam, "4",addr,"12321","0","1212",mob,"me","me",pass,currentDate);

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverCon);
                                //before going to the activity we set shared preferences for stay logged in
                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                sessionManager.createLoginSession(userId,"",nam,addr,mob,userImgF,orgImg,4,"", KeyCon.ERikshaDriver.Value.LNG_ENG);
                                //here we are storing the value
                                db.child(userId).setValue(refDB);
                                //this is all completed to sending Data



                                //now we starting another activity
                                startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                                finish();


                            } else {
                                //if user  select img

                                StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("ERikshaImg").child(System.currentTimeMillis() + "." + getFileExtension(Uri.parse(userImgF)));
                                fileRef.putFile(Uri.parse(userImgF)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                //if image upload successfull than updata data

                                                //here first we taking all the data
                                                Calendar calendar = Calendar.getInstance();
                                                String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
                                                String nam = name.getEditText().getText().toString().trim();
                                                String mob = mobileNo.getEditText().getText().toString().trim();
                                                String pass = password.getEditText().getText().toString().trim();

                                                pass = encode(pass);  //data will incoded

                                                //here we adding device id
                                                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                                                //this is for creating data class instance
                                                SignUpData refDB = new SignUpData(nam,"4",addr,"123321","0","",mob,String.valueOf(uri),orgImg,pass,currentDate);
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");

                                                //here we are storing the value
                                                db.child(userId).setValue(refDB);
                                                //this is all completed to sending Data

                                                //before going to the activity we set shared preferences for stay logged in
                                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                                sessionManager.createLoginSession(userId);


                                                //now we starting another activity
                                                startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                                                finish();


                                            }
                                        });
                                    }
                                });
                            }




                        } else {
                            Toast.makeText(getApplicationContext(), "Error occured during authe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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


    public void selectImg(View view) {
        //two permission are required for taking image
        //read, write
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

        } else {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        }


        //imageNum increased when we get an image


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri;
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
//            addImage(imageUri);
//            InputStream inputStream=null;
//            try{
//                assert  imageUri!=null;
//                inputStream=getContentResolver().openInputStream(imageUri);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            BitmapFactory.decodeStream(inputStream);
            //in getActivity() func imageNum will increased

            //here we compress the Image
            try {
                //this is for orgImg

                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),imageUri);
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] bytes=stream.toByteArray();
                String txt= Base64.encodeToString(bytes,Base64.DEFAULT);

                orgImg=txt;

                //this is for compression

                Bitmap imageBitmap = SiliCompressor.with(this).getCompressBitmap(String.valueOf(imageUri), false);

                //this is for converting bitmap into String
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), imageBitmap, "val", null);
//                this is for converting String into Uri
                imageUri = Uri.parse(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            store data in
            userImgF = String.valueOf(imageUri);

            //here we add img to display
            Picasso.get().load(userImgF).into(img);

        }

    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }




}
