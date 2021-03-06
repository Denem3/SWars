package com.app.selfiewars.selfiewars;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuessItActivity extends AppCompatActivity {
    private ImageView userProfileImageView;
    private ImageView loadTempImageView;
    private TextView userNameTextView;
    private TextView joker1ValueTextView;
    private TextView joker2ValueTextView;
    private TextView timeValueTextView;
    private TextView scoreValueTextView;
    private TextView loadingFeedBackTextView;
    private TextView loseLAyoutRightOfGameTextView;
    private LottieAnimationView loseadsLottie;
    private LottieAnimationView joker1btn;
    private LottieAnimationView joker2btn;
    private LottieAnimationView btn1Lottie;
    private LottieAnimationView btn2Lottie;
    private LottieAnimationView btn3Lottie;
    private LottieAnimationView btn4Lottie;
    private LottieAnimationView healthAnimView;
    private LottieAnimationView lottieAnimationView;
    private TextView btn1TextView;
    private TextView btn2TextView;
    private TextView btn3TextView;
    private TextView btn4TextView;
    private TextView loseScore;
    private TextView loseAllScore;
    private TextView playAgainTextView;
    private TextView questionCounTextView;
    private TextView healthValueTextView;
    private Button   homeButton;
    private ConstraintLayout loadinglayout;
    private ConstraintLayout guessitlayout;
    private ConstraintLayout loselayout;
    private ConstraintLayout doubeleScoreloselayout;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mAllUsersRef;
    private DatabaseReference mCorrectUserRef;
    private Integer score = 0;
    private Long time;
    private boolean isGameStart;
    private Integer countQuestion = 1; //
    private List<GuessItUserData> listUserProperties;
    private Integer listUserPropertiesIndex = 0;
    private Integer allquestionsCount;
    private CountDownTimer countDowTimer;
    private List<Integer> optionsList;
    private Integer secondfiftyFityOption;
    private boolean isCalledJokerFiftyFifty = false;
    private boolean isCalledJokerDoubleDip = false;
    private boolean isCalledJokerHealth = false;
    private Integer joker1DecValue = 1;
    private Integer joker1UsingCount = 0;
    private Integer healthUsingCount = 0;
    private Integer joker2UsingCount = 0;
    private Integer joker2DecValue = 1;
    private Integer healthDecValue = 1;
    private Integer rightOfGame;
    private Handler healthHandler;
    private Runnable healthRunnable;
    private boolean isNextUserList = false;
    private boolean buttonClick = false;
    private boolean isBetaGame = false;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean adsrunning =false;
    private boolean interstitalAdsrunning =false;
    private InterstitialAd mInterstitialAd;


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_it);
        define();
        defineFirebase();
        setJokerClickListener();
        setButtonAnimationListener();
        setButtonOnClickListener();
        getUserListFromDatabase();
        setLoselayoutItemsClickListener();
        MobileAds.initialize(GuessItActivity.this,
                "ca-app-pub-7004761147200711~5104636223");
        setRewardAds();
    }
    private void defineFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAllUsersRef = mDatabase.getReference("Users");
        mCorrectUserRef = mDatabase.getReference(getResources().getString(R.string.CorrectUsers));
    }
    private void define(){
        userProfileImageView = findViewById(R.id.guessit_user_profile_imageView);
        loadTempImageView = findViewById(R.id.guess_it_load_imageView_TEMP);
        loseadsLottie = findViewById(R.id.guess_it_lose_ads_ImageView);
        userNameTextView = findViewById(R.id.guessit_user_profile_username_TextView);
        scoreValueTextView = findViewById(R.id.guessit_scoreValueTextView);
        timeValueTextView = findViewById(R.id.guessit_timeValueTextView);
        joker1btn = findViewById(R.id.guessit_joker1_lottieAnimationView);
        joker1ValueTextView = findViewById(R.id.guessit_joker1Value_TextView);
        joker2btn = findViewById(R.id.guessit_joker2_lottieAnimationView);
        joker2ValueTextView = findViewById(R.id.guessit_joker2Value_TextView);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        btn1Lottie = findViewById(R.id.guessit_btn1_lottieAnimationView);
        btn2Lottie = findViewById(R.id.guessit_btn2_lottieAnimationView);
        btn3Lottie = findViewById(R.id.guessit_btn3_lottieAnimationView);
        btn4Lottie = findViewById(R.id.guessit_btn4_lottieAnimationView);
        healthAnimView = findViewById(R.id.guess_it_health_lottieAnimView);
        btn1TextView = findViewById(R.id.guessit_btn1_TextView);
        btn2TextView = findViewById(R.id.guessit_btn2_TextView);
        btn3TextView = findViewById(R.id.guessit_btn3_TextView);
        btn4TextView = findViewById(R.id.guessit_btn4_TextView);
        playAgainTextView = findViewById(R.id.guess_it_lose_play_again_TextView);
        loseLAyoutRightOfGameTextView = findViewById(R.id.guess_it_lose_right_of_game_textView);
        questionCounTextView = findViewById(R.id.guess_it_question_count_TextView);
        homeButton = findViewById(R.id.guess_it_lose_home_Button);
        loseScore = findViewById(R.id.guess_it_lose_score_textView);
        loseScore.startAnimation(AnimationUtils.loadAnimation(GuessItActivity.this,R.anim.updown_guess_it));
        loseAllScore = findViewById(R.id.guess_it_lose_all_score_textView);
        loadingFeedBackTextView = findViewById(R.id.loading_feedback_TextView);
        loadinglayout = findViewById(R.id.guess_it_activity_loading_ConstrainLayout);
        guessitlayout = findViewById(R.id.guess_it_activity_guess_it_ConstrainLayout);
        doubeleScoreloselayout = findViewById(R.id.double_score_ConstraintLayout);
        loselayout = findViewById(R.id.guess_it_activity_lose_ConstrainLayout);
        healthValueTextView = findViewById(R.id.guess_it_health_TextView);
        btn1Lottie.setSpeed(3);
        btn2Lottie.setSpeed(3);
        btn3Lottie.setSpeed(3);
        btn4Lottie.setSpeed(3);
        scoreValueTextView.setText(""+0);
        joker1SetClickableAndVisible(false);
        joker2SetClickableAndVisible(false);
    }
    private void gameManagement(){
        if(isGameStart){
            if(listUserProperties != null && listUserPropertiesIndex < allquestionsCount){
                setUserImageAndUserName(listUserProperties.get(listUserPropertiesIndex));
                if(listUserPropertiesIndex < allquestionsCount-1){
                    loadNextImage(listUserPropertiesIndex + 1);
                }else {
                    getSecondUserListFromDatabase();
                }
            }else {
                isGameStart = false;
                MainActivity.showPopUpInfo(null,"Tüm kullanıcıların yaşları tahmin edildi","Veritbanındaki tüm onaylı kullanıcıların yaşını tahmin ettiniz. Yeni kullanıcılar kısa sürede eklenecektir.Puanların hesabına eklendi. Daha sonra tekrar gel",GuessItActivity.this);
                setScoreDataInFirebaseAndLoseLayoutOpen();

            }
        }
    }

    private void setButtonOnClickListener(){

        btn1Lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGameStart){
                    setButtonClickable(false);
                    selectOptionAndWaitCorrectAnswer(btn1TextView);
                    countDowTimer.cancel();
                }
            }
        });
        btn2Lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(isGameStart){
                   setButtonClickable(false);
                   selectOptionAndWaitCorrectAnswer(btn2TextView);
                   countDowTimer.cancel();
               }

            }
        });
        btn3Lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGameStart){
                    setButtonClickable(false);
                    selectOptionAndWaitCorrectAnswer(btn3TextView);
                    countDowTimer.cancel();
                }

            }
        });
        btn4Lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGameStart){
                    setButtonClickable(false);
                    selectOptionAndWaitCorrectAnswer(btn4TextView);
                    countDowTimer.cancel();
                }

            }
        });
    }
    private void setButtonClickable(Boolean clickable){
        btn1Lottie.setClickable(clickable);
        btn2Lottie.setClickable(clickable);
        btn3Lottie.setClickable(clickable);
        btn4Lottie.setClickable(clickable);
    }
    private void setButtonsText(List<Integer> ıntegerList){
        optionsList = ıntegerList;
        btn1TextView.setText(""+ıntegerList.get(0).toString());
        btn2TextView.setText(""+ıntegerList.get(1).toString());
        btn3TextView.setText(""+ıntegerList.get(2).toString());
        btn4TextView.setText(""+ıntegerList.get(3).toString());
    }
    private void getUserListFromDatabase(){
        new Database().mReadDataAndGetUserListForGuessIt(mAuth, getResources().getString(R.string.ApprovedUser), getResources().getString(R.string.CorrectUsers), new OnGetUserlistDataListener() {
            @Override
            public void onStart() {
                loadinglayout.setVisibility(View.VISIBLE);
                guessitlayout.setVisibility(View.GONE);
                loselayout.setVisibility(View.GONE);

            }
            @Override
            public void onProgress(String string) {
                loadingFeedBackTextView.setText(""+string);
            }

            @Override
            public void onSuccess(final List<GuessItUserData> guessItUserDataList,Boolean isBeta) {
                isBetaGame = isBeta;
                listUserProperties = guessItUserDataList;
                allquestionsCount = listUserProperties.size();
                loadFirstUserImage(0);
            }

            @Override
            public void onFailed() {
                loadinglayout.setVisibility(View.GONE);
                MainActivity.showPopUpInfo(null,"Tüm kullanıcıların yaşları tahmin edildi","Veritbanındaki tüm onaylı kullanıcıların yaşını tahmin ettiniz. Yeni kullanıcılar kısa sürede eklenecektir. Daha sonra tekrar gel.",GuessItActivity.this);
                setScoreDataInFirebaseAndLoseLayoutOpen();
            }
        });
    }
    public void getSecondUserListFromDatabase(){
        new Database().mReadDataAndGetUserListForGuessIt(mAuth, getResources().getString(R.string.ApprovedUser), getResources().getString(R.string.CorrectUsers), new OnGetUserlistDataListener() {
            @Override
            public void onStart() {
                isNextUserList = false;
            }

            @Override
            public void onProgress(String string) {

            }
            @Override
            public void onSuccess(final List<GuessItUserData> guessItUserDataList,Boolean isBeta) {
                isBetaGame = isBeta;
                listUserProperties.addAll(guessItUserDataList);
                allquestionsCount = listUserProperties.size();
                isNextUserList = true;
            }

            @Override
            public void onFailed() {
                isNextUserList = false;
            }
        });
    }
    private void setUserImageAndUserName(final GuessItUserData guessItUserData){
        userNameTextView.setVisibility(View.INVISIBLE);
        Picasso.with(getApplicationContext()).load(guessItUserData.getPhotoUrl()).noFade().networkPolicy(NetworkPolicy.NO_STORE,NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_STORE,MemoryPolicy.NO_CACHE).into(userProfileImageView, new Callback() {
            @Override
            public void onSuccess() {
                setButtonsText(options(listUserProperties.get(listUserPropertiesIndex).getAge()));
                playAnimationButtons();
                //userNameTextView.setText(guessItUserData.getUserName());
                userNameTextView.setVisibility(View.VISIBLE);
                startCountDownTimer();
                setJokerReset();
                questionCounTextView.setText(""+countQuestion);
            }

            @Override
            public void onError() {
                listUserPropertiesIndex++;
               // Toast.makeText(GuessItActivity.this, "Resim Yüklenemedi :" +guessItUserData.getUserName()   , Toast.LENGTH_SHORT).show();
                gameManagement();
            }
        });
    }
    private void loadNextImage(final Integer index){
        Picasso.with(getApplicationContext()).load(listUserProperties.get(index).getPhotoUrl()).noFade().into(loadTempImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getApplicationContext()).load(listUserProperties.get(index).getPhotoUrl()).noFade().into(loadTempImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
    }
    private void selectOptionAndWaitCorrectAnswer(final TextView btnTextView){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(isAnswerCorrect(btnTextView.getText().toString())){
                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            setScore();
                            countQuestion++;
                            listUserPropertiesIndex++;
                            gameManagement();
                        }
                    };
                    if (!isBetaGame){
                        mCorrectUserRef.child(mAuth.getUid()).child(listUserProperties.get(listUserPropertiesIndex).getUid()).setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                handler.postDelayed(runnable,600);
                                btnTextView.setBackgroundResource(R.drawable.guess_it_true_answer);
                            }
                        });
                    }else {
                        handler.postDelayed(runnable,600);
                        btnTextView.setBackgroundResource(R.drawable.guess_it_true_answer);
                    }
                    joker1btn.setClickable(false);
                    joker2btn.setClickable(false);

                }else {
                    isGameStart = false;
                    if(isCalledJokerDoubleDip){
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                isGameStart = true;
                                eliminateOneOption(btnTextView);
                            }
                        };
                        handler.postDelayed(runnable,500);
                        btnTextView.setBackgroundResource(R.drawable.guess_it_false_answer);
                    }else {
                        btnTextView.setBackgroundResource(R.drawable.guess_it_false_answer);
                        healthHandler = new Handler();
                        healthRunnable = new Runnable() {
                            @Override
                            public void run() {
                                healthSetClickableAndVisible(false);
                                setScoreDataInFirebaseAndLoseLayoutOpen();
                            }
                        };
                        if(MainActivity.wildcards.getHealthValue() == 0){
                            healthHandler.postDelayed(healthRunnable,300);
                        }else {
                            healthSetClickableAndVisible(true);
                            healthHandler.postDelayed(healthRunnable,4000);

                        }

                    }
                }
                buttonClick = false;
            }
        };
        if(!buttonClick) {
            buttonClick = true;
            handler.postDelayed(runnable,1000);
            btnTextView.setBackgroundResource(R.drawable.guess_it_selected_answer);
        }

    }
    private void setScoreDataInFirebaseAndLoseLayoutOpen(){
        if(score!=0){
            MainActivity.myScoreRef.child(mAuth.getUid()).runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer dbscore = mutableData.getValue(Integer.class);
                    dbscore +=score;
                    mutableData.setValue(dbscore);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable final DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    if (b){
                        MainActivity.myWalletScoreRef.child(mAuth.getUid()).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                Integer dbscore = mutableData.getValue(Integer.class);
                                if (dbscore == null){
                                    dbscore = score;
                                }else {
                                    dbscore +=score;
                                }
                                mutableData.setValue(dbscore);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                if (b){
                                    loseScore.setText("" + score + " Puan");
                                    loseAllScore.setText("Toplam Puan: "+MainActivity.UserScore);
                                    if (MainActivity.myRigtOfGame == 9 || MainActivity.myRigtOfGame == 1){
                                        if (mInterstitialAd.isLoaded()) {
                                            mInterstitialAd.show();
                                            interstitalAdsrunning = true;

                                        }else {
                                            //Toast.makeText(GuessItActivity.this, "Yüklenmedi", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    loadinglayout.setVisibility(View.GONE);
                                    guessitlayout.setVisibility(View.GONE);
                                    loselayout.setVisibility(View.VISIBLE);
                                }else {
                                    loseScore.setText("" + score + " Puan");
                                    loseAllScore.setText("Toplam Puan: "+MainActivity.UserScore);
                                    loadinglayout.setVisibility(View.GONE);
                                    guessitlayout.setVisibility(View.GONE);
                                    loselayout.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }else {
                        if(score !=0){
                            MainActivity.myScoreRef.child(mAuth.getUid()).setValue(MainActivity.UserScore+score).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    MainActivity.myWalletScoreRef.child(mAuth.getUid()).runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                            Integer dbscore = mutableData.getValue(Integer.class);
                                            if (dbscore == null){
                                                dbscore = score;
                                            }else {
                                                dbscore +=score;
                                            }
                                            mutableData.setValue(dbscore);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                            loseScore.setText("" + score + " Puan");
                                            loseAllScore.setText("Toplam Puan: "+MainActivity.UserScore);
                                            if (MainActivity.myRigtOfGame == 9 || MainActivity.myRigtOfGame == 1){
                                                if (mInterstitialAd.isLoaded()) {
                                                    mInterstitialAd.show();
                                                    interstitalAdsrunning = true;
                                                    // Toast.makeText(GuessItActivity.this, "Açıldı", Toast.LENGTH_SHORT).show();

                                                }else
                                                {
                                                    //Toast.makeText(GuessItActivity.this, "Yüklenmedi", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                            loadinglayout.setVisibility(View.GONE);
                                            guessitlayout.setVisibility(View.GONE);
                                            loselayout.setVisibility(View.VISIBLE);
                                            Toast.makeText(GuessItActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();


                                        }
                                    });
                                }
                            });
                        }else {
                            Toast.makeText(GuessItActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            loseScore.setText("" + score + " Puan");
                            loseAllScore.setText("Toplam Puan: "+MainActivity.UserScore);
                            if (MainActivity.myRigtOfGame == 9 || MainActivity.myRigtOfGame == 1){
                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                    interstitalAdsrunning = true;

                                    // Toast.makeText(GuessItActivity.this, "Açıldı", Toast.LENGTH_SHORT).show();

                                }else {
                                    // Toast.makeText(GuessItActivity.this, "Yüklenmedi", Toast.LENGTH_SHORT).show();
                                }
                            }
                            loadinglayout.setVisibility(View.GONE);
                            guessitlayout.setVisibility(View.GONE);
                            loselayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }else {
            loseScore.setText("" + score + " Puan");
            loseAllScore.setText("Toplam Puan: "+MainActivity.UserScore);
            if (MainActivity.myRigtOfGame == 9 || MainActivity.myRigtOfGame == 1){
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    interstitalAdsrunning = true;
                    // Toast.makeText(GuessItActivity.this, "Açıldı", Toast.LENGTH_SHORT).show();

                }else {
                    //Toast.makeText(GuessItActivity.this, "Yüklenmedi", Toast.LENGTH_SHORT).show();
                }
            }
            loadinglayout.setVisibility(View.GONE);
            guessitlayout.setVisibility(View.GONE);
            loselayout.setVisibility(View.VISIBLE);
        }
    }
    private void setLoselayoutItemsClickListener(){
        loseLAyoutRightOfGameTextView.setText("Kalan Tahmin: "+MainActivity.myRigtOfGame+"/10");

        playAgainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isGameStart){
                    isGameStart = true;
                    MainActivity.myRefUser.child("nowtimestamp").child(getResources().getString(R.string.timestamp)).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            mutableData.setValue(ServerValue.TIMESTAMP);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            if (b) {
                                final Long nowtimeStamp = dataSnapshot.getValue(Long.class);
                                if (nowtimeStamp < MainActivity.endTimeStamp) {
                                    MainActivity.myRigtOfGameRef.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                            Integer i = mutableData.getValue(Integer.class);
                                            if (i == 0) {
                                                return null;
                                            } else {
                                                i--;
                                                mutableData.setValue(i);
                                                return Transaction.success(mutableData);
                                            }
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                            if (b) {
                                                if (MainActivity.myRigtOfGame == 9) {
                                                    MainActivity.myRefUser.child(getResources().getString(R.string.timestamp)).child("guessItMilisecond").setValue(nowtimeStamp + 86400000).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Intent i = new Intent(GuessItActivity.this, GuessItActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    Intent i = new Intent(GuessItActivity.this, GuessItActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            } else {
                                                MainActivity.showPopUpInfo(null, "Günlük tahmin hakkınız bitmiştir!!", null, GuessItActivity.this);
                                                isGameStart = false;
                                            }
                                        }
                                    });
                                } else {
                                    MainActivity.showPopUpInfo(null, "Hafta yenilenmemiştir!!",
                                            "Sistem bu haftanın yapılandırmasını tamamladıktan sonra yeni hafta başlayacaktır. Başladığında bildirim gönderilecektir. \n \n ~Selfie Wars Tavsiyesi~ \n Yeni hafta için joker hazırlığı yapmak sıralamada avantaj sağlayacaktır."
                                            , GuessItActivity.this);
                                    isGameStart = false;
                                }
                            }else {
                                MainActivity.showPopUpInfo(null, "Sunucda Hata Oluştu!",
                                        "Tekrar Deneyin."
                                        , GuessItActivity.this);
                                isGameStart = false;
                            }
                        }
                    });
                }
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isGameStart){
                    HomeFragment.getRank();
                    GuessItActivity.super.onBackPressed();
                    finish();
                }
            }
        });
    }
    private void onClickListenerAds(){
        loseadsLottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loseadsLottie.setClickable(false);
                if(mRewardedVideoAd.isLoaded()) {
                    adsrunning = true;
                    mRewardedVideoAd.show();
                    loseadsLottie.setClickable(true);
                }
                else
                {
                    mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                            new AdRequest.Builder().build());
                    mRewardedVideoAd.show();
                }

            }
        });
    }

    private void setRewardAds(){
        //Geçiş Reklam
        mInterstitialAd = new InterstitialAd(GuessItActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7004761147200711/8808242123");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //Toast.makeText(GuessItActivity.this, "Geçiş Reklamı Yüklendi.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                //Toast.makeText(GuessItActivity.this, "Geçiş Reklamı Fail.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });

        //Video Reklam
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(GuessItActivity.this);
        mRewardedVideoAd.loadAd("ca-app-pub-7004761147200711/1490368779",
                new AdRequest.Builder().build());
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                //Toast.makeText(getApplicationContext(), "Reklam Yüklendi", Toast.LENGTH_SHORT).show();
                onClickListenerAds();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.d("RewardItemFB","Rewarded");
                MainActivity.myScoreRef.child(mAuth.getUid()).runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer dbscore = mutableData.getValue(Integer.class);
                        dbscore+=score;
                        mutableData.setValue(dbscore);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (b){
                            loseScore.setText("" + 2*score + " Puan");
                            doubeleScoreloselayout.setVisibility(View.GONE);
                        }
                        adsrunning = false;
                    }
                });
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });

    }

    private void setButtonAnimationListener(){
        btn1Lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setButtonClickable(false);
                btn1TextView.setVisibility(View.INVISIBLE);
                btn1TextView.setBackgroundResource(R.drawable.guess_it_transparent_answer);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setButtonClickable(true);
                btn1TextView.setVisibility(View.VISIBLE);
                if(isCalledJokerFiftyFifty || isCalledJokerDoubleDip){
                    btn1Lottie.reverseAnimationSpeed();
                    btn1Lottie.setClickable(false);
                    btn1TextView.setVisibility(View.INVISIBLE);
                    isCalledJokerDoubleDip = false;
                    joker2btn.setClickable(true);
                    joker1btn.setClickable(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                //Toast.makeText(GuessItActivity.this, "İptal", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        btn2Lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setButtonClickable(false);
                btn2TextView.setVisibility(View.INVISIBLE);
                btn2TextView.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setButtonClickable(true);
                btn2TextView.setVisibility(View.VISIBLE);
                if(isCalledJokerFiftyFifty || isCalledJokerDoubleDip){
                    btn2Lottie.reverseAnimationSpeed();
                    btn2Lottie.setClickable(false);
                    btn2TextView.setVisibility(View.INVISIBLE);
                    isCalledJokerDoubleDip = false;
                    joker2btn.setClickable(true);
                    joker1btn.setClickable(true);

                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        btn3Lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setButtonClickable(false);
                btn3TextView.setVisibility(View.INVISIBLE);
                btn3TextView.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // isOnScreenbnt3 = !isOnScreenbnt3;
                setButtonClickable(true);
                btn3TextView.setVisibility(View.VISIBLE);
                if(isCalledJokerFiftyFifty || isCalledJokerDoubleDip){
                    btn3Lottie.reverseAnimationSpeed();
                    btn3Lottie.setClickable(false);
                    btn3TextView.setVisibility(View.INVISIBLE);
                    isCalledJokerDoubleDip = false;
                    joker2btn.setClickable(true);
                    joker1btn.setClickable(true);


                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        btn4Lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setButtonClickable(false);
                btn4TextView.setVisibility(View.INVISIBLE);
                btn4TextView.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setButtonClickable(true);
                btn4TextView.setVisibility(View.VISIBLE);
                if(isCalledJokerFiftyFifty || isCalledJokerDoubleDip){
                    btn4Lottie.reverseAnimationSpeed();
                    btn4Lottie.setClickable(false);
                    btn4TextView.setVisibility(View.INVISIBLE);
                    isCalledJokerDoubleDip = false;
                    joker2btn.setClickable(true);
                    joker1btn.setClickable(true);

                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    private void setJokerClickListener(){
        joker1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonClick && isGameStart){
                    joker1SetClickableAndVisible(false);
                    joker2SetClickableAndVisible(false);

                    MainActivity.myRefUser.child("wildcards").child("fiftyFiftyValue").runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Integer value = mutableData.getValue(Integer.class);
                            value -= joker1DecValue;
                            if(value<0){
                                return null;
                            }else {
                                mutableData.setValue(value);
                                return Transaction.success(mutableData);
                            }
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            if(b){
                                isCalledJokerFiftyFifty = true;
                                joker1UsingCount++;
                                eliminateTwoOption();
                            }else {
                                MainActivity.showPopUpInfo(null,"Yeterli jokeriniz bulunmamaktadır.",null,GuessItActivity.this);
                                joker2SetClickableAndVisible(true);
                            }
                        }
                    });
                }

            }
        });
        joker2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonClick && isGameStart){
                    joker2SetClickableAndVisible(false);
                    joker1SetClickableAndVisible(false);
                    MainActivity.myRefUser.child("wildcards").child("doubleDipValue").runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Integer value = mutableData.getValue(Integer.class);
                            value -=joker2DecValue;
                            if(value<0){
                                return null;
                            }else {
                                mutableData.setValue(value);
                                return Transaction.success(mutableData);
                            }
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            if(b){
                                isCalledJokerDoubleDip = true;
                                joker2UsingCount++;
                            }else {
                                MainActivity.showPopUpInfo(null,"Yeterli jokeriniz bulunmamaktadır.",null,GuessItActivity.this);
                                joker1SetClickableAndVisible(true);
                            }
                        }
                    });
                }
            }
        });
        healthAnimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                healthSetClickableAndVisible(false);
                if(!buttonClick){
                    MainActivity.myRefUser.child("wildcards").child("healthValue").runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Integer value = mutableData.getValue(Integer.class);
                            value -= healthDecValue;
                            if(value<0){
                                return null;
                            }else {
                                mutableData.setValue(value);
                                return Transaction.success(mutableData);
                            }
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            if(b){
                                healthHandler.removeCallbacks(healthRunnable);
                                healthSetClickableAndVisible(false);
                                healthUsingCount++;
                                countQuestion++;
                                listUserPropertiesIndex++;
                                isGameStart = true;
                                gameManagement();
                            }else {
                                isGameStart = false;
                                MainActivity.showPopUpInfo(null,"Yeterli jokeriniz bulunmamaktadır.",null,GuessItActivity.this);
                                guessitlayout.setVisibility(View.GONE);
                                loselayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

    }
    private void setJokerReset(){
        healthDecValue = (int) Math.pow(2,healthUsingCount);
        joker1DecValue = (int) Math.pow(2,joker1UsingCount);
        joker2DecValue = (int) Math.pow(2,joker2UsingCount);
        isCalledJokerFiftyFifty = false;
        isCalledJokerDoubleDip = false;
        joker1SetClickableAndVisible(true);
        joker2SetClickableAndVisible(true);
        healthSetClickableAndVisible(false);
        joker1ValueTextView.setText("" + joker1DecValue);
        joker2ValueTextView.setText("" + joker2DecValue);
        healthValueTextView.setText("" + healthDecValue);

    }
    private void healthSetClickableAndVisible(Boolean visibility){
        if(visibility){
            healthValueTextView.setVisibility(View.VISIBLE);
            healthValueTextView.setClickable(true);
            healthAnimView.setClickable(true);
            healthAnimView.setVisibility(View.VISIBLE);
            healthAnimView.playAnimation();
        }else {
            healthValueTextView.setVisibility(View.GONE);
            healthValueTextView.setClickable(false);
            healthAnimView.setVisibility(View.GONE);
            healthAnimView.setClickable(false);
        }
    }
    private void joker1SetClickableAndVisible(Boolean clickable){
        if (clickable){
            joker1btn.setClickable(true);
            joker1btn.setVisibility(View.VISIBLE);
            joker1ValueTextView.setVisibility(View.VISIBLE);
        }else {
            joker1btn.setClickable(false);
            joker1btn.setVisibility(View.INVISIBLE);
            joker1ValueTextView.setVisibility(View.INVISIBLE);
        }
    }
    private void joker2SetClickableAndVisible(Boolean clickable){
        if (clickable){
            joker2btn.setClickable(true);
            joker2btn.setVisibility(View.VISIBLE);
            joker2ValueTextView.setVisibility(View.VISIBLE);
        }else {
            joker2btn.setClickable(false);
            joker2btn.setVisibility(View.INVISIBLE);
            joker2ValueTextView.setVisibility(View.INVISIBLE);
        }
    }
    private void eliminateOneOption(TextView textView){
        for (int i = 0; i<4;i++){
            if(textView.getText().equals(optionsList.get(i).toString())){
                switch (i){
                    case 0:{
                        btn1Lottie.reverseAnimationSpeed();
                        btn1Lottie.playAnimation();
                        btn1Lottie.setClickable(false);
                        break;
                    }
                        /*btn1Lottie.setVisibility(View.INVISIBLE);
                        btn1Lottie.setClickable(false);
                        btn1TextView.setVisibility(View.INVISIBLE);*/
                    case 1: {
                        btn2Lottie.reverseAnimationSpeed();
                        btn2Lottie.playAnimation();
                        btn2Lottie.setClickable(false);
                        break;
                    }
                    case 2:{
                        btn3Lottie.reverseAnimationSpeed();
                        btn3Lottie.playAnimation();
                        btn3Lottie.setClickable(false);
                        break;
                    }
                    case 3:{
                        btn4Lottie.reverseAnimationSpeed();
                        btn4Lottie.playAnimation();
                        btn4Lottie.setClickable(false);
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
        }
    }
    private void eliminateTwoOption(){
        for (int i = 0; i<4;i++){
            if(!listUserProperties.get(listUserPropertiesIndex).getAge().equals(optionsList.get(i)) && !secondfiftyFityOption.equals(optionsList.get(i))){
                switch (i){
                    case 0:{
                        btn1Lottie.reverseAnimationSpeed();
                        btn1Lottie.playAnimation();
                        break;
                    }
                        /*btn1Lottie.setVisibility(View.INVISIBLE);
                        btn1Lottie.setClickable(false);
                        btn1TextView.setVisibility(View.INVISIBLE);*/
                    case 1: {
                        btn2Lottie.reverseAnimationSpeed();
                        btn2Lottie.playAnimation();
                        break;
                    }
                        /*btn2Lottie.setVisibility(View.INVISIBLE);
                        btn2Lottie.setClickable(false);
                        btn2TextView.setVisibility(View.INVISIBLE);*/
                    case 2:{
                        btn3Lottie.reverseAnimationSpeed();
                        btn3Lottie.playAnimation();
                        break;
                    }

                        /*btn3Lottie.setVisibility(View.INVISIBLE);
                        btn3Lottie.setClickable(false);
                        btn3TextView.setVisibility(View.INVISIBLE);*/
                    case 3:{
                        btn4Lottie.reverseAnimationSpeed();
                        btn4Lottie.playAnimation();
                        break;
                    }
                        /*btn4Lottie.setVisibility(View.INVISIBLE);
                        btn4Lottie.setClickable(false);
                        btn4TextView.setVisibility(View.INVISIBLE);*/
                        default:
                            break;
                }
            }
        }
    }
    private void playAnimationButtons(){
        btn1Lottie.playAnimation();
        btn2Lottie.playAnimation();
        btn3Lottie.playAnimation();
        btn4Lottie.playAnimation();
    }
    private void loadFirstUserImage(final Integer index){
        Picasso.with(getApplicationContext()).load(listUserProperties.get(index).getPhotoUrl()).noFade().into(loadTempImageView, new Callback() {
            @Override
            public void onSuccess() {
                loselayout.setVisibility(View.GONE);
                loadinglayout.setVisibility(View.GONE);
                guessitlayout.setVisibility(View.VISIBLE);
                isGameStart = true;
                gameManagement();
            }

            @Override
            public void onError() {
                Picasso.with(getApplicationContext()).load(listUserProperties.get(index).getPhotoUrl()).noFade().into(loadTempImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        loselayout.setVisibility(View.GONE);
                        loadinglayout.setVisibility(View.GONE);
                        guessitlayout.setVisibility(View.VISIBLE);
                        isGameStart = true;
                        gameManagement();
                    }

                    @Override
                    public void onError() {
                        Picasso.with(getApplicationContext()).load(listUserProperties.get(index).getPhotoUrl()).noFade().into(loadTempImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                loselayout.setVisibility(View.GONE);
                                loadinglayout.setVisibility(View.GONE);
                                guessitlayout.setVisibility(View.VISIBLE);
                                isGameStart = true;
                                gameManagement();
                            }

                            @Override
                            public void onError() {
                                if (isNetworkAvailable()){
                                    Intent i = new Intent(GuessItActivity.this, GuessItActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(i);
                                    finish();
                                }else {
                                    setScoreDataInFirebaseAndLoseLayoutOpen();
                                    isNextUserList = false;
                                    isGameStart = false;
                                }
                            }
                        });
                    }
                });
            }
        });
    }
    private void startCountDownTimer(){
        try{
            if(countDowTimer !=null){
                countDowTimer.cancel();
                countDowTimer = new CountDownTimer(15000,1000) {
                    @Override
                    public void onTick(long l) {
                        timeValueTextView.setText(""+l/1000);
                        if(l/1000 < 6){
                            timeValueTextView.setTextColor(Color.RED);
                        }else {
                            timeValueTextView.setTextColor(Color.WHITE);
                        }
                    }

                    @Override
                    public void onFinish() {
                        isGameStart = false;
                        setScoreDataInFirebaseAndLoseLayoutOpen();
                    }
                }.start();
            }else {
                countDowTimer = new CountDownTimer(15000,1000) {
                    @Override
                    public void onTick(long l) {
                        timeValueTextView.setText(""+l/1000);
                        if(l/1000 < 6){
                            timeValueTextView.setTextColor(Color.RED);
                        }else {
                            timeValueTextView.setTextColor(Color.WHITE);
                        }
                    }
                    @Override
                    public void onFinish() {
                        isGameStart = false;
                        setScoreDataInFirebaseAndLoseLayoutOpen();

                    }
                }.start();
            }
        }catch (Exception e){

        }
    }
    private void setScore(){
        score += (countQuestion * 25);
        scoreValueTextView.setText(""+score);
    }
    private List<Integer> getShuffleThreeOptions(List<Integer> alloptions){
        Collections.shuffle(alloptions);
        List<Integer> ıntegerList =  new ArrayList<>();
        ıntegerList.add(alloptions.get(0));
        ıntegerList.add(alloptions.get(1));
        ıntegerList.add(alloptions.get(2));
        return ıntegerList;
    }
    private List<Integer> options(Integer answer) {
        List<Integer> options = new ArrayList<>();
        if (MainActivity.UserScore != null && MainActivity.UserScore > 10000) {
            if (countQuestion >= 1 && countQuestion <= 3) {
                if (answer >= 15 && answer < 27) {
                    options =optionsAlgorith(answer,6,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options = optionsAlgorith(answer,6,2);
                    return options;

                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,6,2);
                    return options;

                }
            } else if (countQuestion >= 4 && countQuestion <= 6) {
                if (answer >= 15 && answer < 27) {
                    options= optionsAlgorith(answer,5,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options= optionsAlgorith(answer,5,1);
                    return options;

                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,5,2);
                    return options;
                }
            } else if (countQuestion >= 7 && countQuestion <= 10) {
                if (answer >= 15 && answer < 27) {
                    options= optionsAlgorith(answer,4,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options= optionsAlgorith(answer,4,1);
                    return options;

                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,4,1);
                    return options;
                }
            } else {
                if (answer >= 15 && answer < 27) {
                    options= optionsAlgorith(answer,3,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options= optionsAlgorith(answer,3,1);
                    return options;

                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,3,1);
                    return options;
                }
            }
        }else if(MainActivity.UserScore != null && MainActivity.UserScore <= 10000){
            if (countQuestion >= 1 && countQuestion <= 3) {
                if (answer >= 15 && answer < 27) {
                    options= optionsAlgorith(answer,6,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options= optionsAlgorith(answer,6,2);
                    return options;
                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,6,4);
                    return options;

                }
            } else if (countQuestion >= 4 && countQuestion <= 6) {
                if (answer >= 15 && answer < 27) {
                    options= optionsAlgorith(answer,5,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options= optionsAlgorith(answer,5,2);
                    return options;

                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,5,3);
                    return options;

                }
            } else if (countQuestion >= 7 && countQuestion <= 10) {
                if (answer >= 15 && answer < 27) {
                    options=  optionsAlgorith(answer,4,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options= optionsAlgorith(answer,4,2);
                    return options;

                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,4,2);
                    return options;

                }
            } else {
                if (answer >= 15 && answer < 27) {
                    options =  optionsAlgorith(answer,3,1);
                    return options;

                } else if (answer >= 27 && answer < 45) {
                    options= optionsAlgorith(answer,3,1);
                    return options;

                }else if (answer >= 45 && answer < 75){
                    options= optionsAlgorith(answer,3,1);
                    return options;
                }
            }
        }
        return optionsAlgorith(answer,3,1);
    }
    private List<Integer> optionsAlgorith(Integer answer,Integer scale,Integer incvalue){
        List<Integer> allOptions = new ArrayList<>();
        List<Integer> options = new ArrayList<>();
        for (int i = 1; i < scale; i++) {
            ///Toast.makeText(this, "i nin değeri: "+i, Toast.LENGTH_SHORT).show();
            allOptions.add(answer - (i*incvalue)); //20 19
        }
        for (int i = 1; i < scale; i++) {
            allOptions.add(answer + (i*incvalue)); //22 23 all options // 19 20 22 23
        }
        if (allOptions.size() == (scale-1)*2) {
            options = getShuffleThreeOptions(allOptions);
            secondfiftyFityOption = options.get(0);
            options.add(answer);
            Collections.shuffle(options);
            return options;
        }return null;
    }
    private boolean isAnswerCorrect(String buttonText){
        if (buttonText.equalsIgnoreCase(listUserProperties.get(listUserPropertiesIndex).getAge().toString())){
            return true;
        }else {
            return false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!adsrunning && !interstitalAdsrunning){
            HomeFragment.getRank();
            finish();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SelfieWars.context = GuessItActivity.this;
    }
}
