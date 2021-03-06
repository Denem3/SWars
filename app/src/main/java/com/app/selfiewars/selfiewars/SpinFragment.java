package com.app.selfiewars.selfiewars;


import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpinFragment extends Fragment{
    private FirebaseDatabase mydatabase;
    private DatabaseReference myRefUser;
    private DatabaseReference myRefRightOfGame;
    private FirebaseAuth mAuth;
    private LuckyWheelView luckyWheelView;
    private List<LuckyItem> data;
    private Button spin;
    private boolean isrunnigluckywheel;
    private Long nowTimestamp;
    private Long rightofspinMilis;
    private Integer rightofSpin;
    private LottieAnimationView spinAdsAnimView;
    private TextView righofSpinText;
    private TextView endtimeText;
    private CountDownTimer countDownTimer;
    private ConstraintLayout adsConstraintLayout;
    private final int idDiamond = 0, idJoker1 = 1, idJoker2 = 2, idReSpin = 3, idHealth = 4, idGreenGem = 5;
    private final String color1 = "#ffffff", color2 = "#e3f2fd", color3 = "#bbdefb";
    private RewardedVideoAd mRewardedVideoAd;
    public SpinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spin, container, false);
        MobileAds.initialize(getActivity(), "ca-app-pub-7004761147200711~5104636223");
        define(view);
        getTimeStampControlOfSpin();
        spinClick();
        setupLuckyWheel();
        setRewardAds();
        return view;
    }

    private void define(View view) {
        isrunnigluckywheel = false;
        mAuth = FirebaseAuth.getInstance();
        mydatabase = FirebaseDatabase.getInstance();
       // myRefUser = mydatabase.getReference("Users/" + mAuth.getUid());
       // myRefRightOfGame = mydatabase.getReference("RightOfGame/"+mAuth.getUid());
        luckyWheelView = view.findViewById(R.id.luckyWheel);
        spin = view.findViewById(R.id.button3);
        righofSpinText = view.findViewById(R.id.home_profile_rightOfGame_textView);
        spinAdsAnimView = view.findViewById(R.id.lottieAdsSpin);
        endtimeText = view.findViewById(R.id.endtimSpinTextView);
        adsConstraintLayout =view.findViewById(R.id.spin_adsConstraintLayout);
        adsConstraintLayout.setVisibility(View.GONE);
    }

    private void setRewardAds(){
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.loadAd("ca-app-pub-7004761147200711/2472277956",
                new AdRequest.Builder().build());
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                //Toast.makeText(getContext(), "Reklam Yüklendi", Toast.LENGTH_SHORT).show();
                setClickAdsListener();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                getTimeStampControlOfSpin();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                MainActivity.myRefUser.child("rightofspin").child("spinValue").runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer spinValue = mutableData.getValue(Integer.class);
                        spinValue++;
                        mutableData.setValue(spinValue);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (b){
                            getTimeStampControlOfSpin();
                            MainActivity.showPopUpInfo(null,"Tebrikler tekrar çevirebilirsiniz.",null,getContext());
                        }
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

    private void getTimeStampControlOfSpin() {
        MainActivity.myRefUser.child("nowtimestamp").child("timestamp").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.setValue(ServerValue.TIMESTAMP);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    Long nowtimestamp = dataSnapshot.getValue(Long.class);
                    if (nowtimestamp !=null){
                        Integer controlrighofSpin = MainActivity.rightOfSpinDataSnapShot.child(getResources().getString(R.string.spinValue)).getValue(Integer.class);
                        Log.d("ControlOfSpin","" + controlrighofSpin);
                        Integer controlspinAdsCount = MainActivity.rightOfSpinDataSnapShot.child(getResources().getString(R.string.spicAdsCount)).getValue(Integer.class);
                        rightofSpin = controlrighofSpin;
                        righofSpinText.setText("" + controlrighofSpin);
                        rightofspinMilis = MainActivity.rightOfSpinDataSnapShot.child(getResources().getString(R.string.timestamp)).getValue(Long.class);
                        if (nowtimestamp < rightofspinMilis) {
                            if (controlrighofSpin == 0) {
                                startOrRefresCountTime(nowtimestamp);
                                endtimeText.setVisibility(View.VISIBLE);
                                if(controlspinAdsCount == 1)
                                    adsConstraintLayout.setVisibility(View.VISIBLE);
                                else
                                    adsConstraintLayout.setVisibility(View.GONE);
                            } else {
                                endtimeText.setVisibility(View.GONE);
                                adsConstraintLayout.setVisibility(View.GONE);
                            }
                        } else {
                            if (controlrighofSpin == 0) {
                                endtimeText.setVisibility(View.GONE);
                                adsConstraintLayout.setVisibility(View.GONE);
                                MainActivity.myRefUser.child("rightofspin").child(getResources().getString(R.string.spinValue)).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        righofSpinText.setText("1");
                                        rightofSpin = 1;
                                    }
                                });
                                MainActivity.myRefUser.child("rightofspin").child(getResources().getString(R.string.spicAdsCount)).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                            else {
                                endtimeText.setVisibility(View.GONE);
                                adsConstraintLayout.setVisibility(View.GONE);
                                righofSpinText.setText("" + controlrighofSpin);
                            }
                        }
                    }
            }
        });
    }

    private void startOrRefresCountTime(Long nowTimestamp) {
        try {

            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = new CountDownTimer((Math.abs(rightofspinMilis - nowTimestamp)), 1000) {
                    @Override
                    public void onTick(long l) {
                        endtimeText.setText(getResources().getString(R.string.Endtime) + TimeUnit.MILLISECONDS.toHours(l) + getResources().getString(R.string.hours)
                                + (TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l))) + getResources().getString(R.string.minute)
                                + (TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))) + getResources().getString(R.string.second));
                    }

                    @Override
                    public void onFinish() {
                            getTimeStampControlOfSpin();
                    }
                }.start();
            } else {
                countDownTimer = new CountDownTimer((Math.abs(rightofspinMilis - nowTimestamp)), 1000) {
                    @Override
                    public void onTick(long l) {
                        endtimeText.setText(getResources().getString(R.string.Endtime) + TimeUnit.MILLISECONDS.toHours(l) + getResources().getString(R.string.hours)
                                + (TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l))) + getResources().getString(R.string.minute)
                                + (TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))) + getResources().getString(R.string.second));
                    }

                    @Override
                    public void onFinish() {
                        getTimeStampControlOfSpin();
                    }
                }.start();
            }
        } catch (Exception e) {

        }
    }

    private void setupLuckyWheel() {
        setSpinItem();
        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                if (isrunnigluckywheel == true) {
                    if (index > 0) {
                        isrunnigluckywheel = false;
                        increaseTheReturnValue(index-1);
                    } else {
                        increaseTheReturnValue(index);
                        isrunnigluckywheel = false;
                    }
                }
            }
        });
    }

    private void spinClick() {
        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isrunnigluckywheel) {
                    isrunnigluckywheel = true;
                    //Toast.makeText(getContext(), "Tıklandı", Toast.LENGTH_SHORT).show();
                    MainActivity.myRefUser.child("rightofspin").child(getResources().getString(R.string.spinValue)).runTransaction(new Transaction.Handler() {
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
                                Integer i = dataSnapshot.getValue(Integer.class);
                                righofSpinText.setText("" + i);
                                if (i == 0) {
                                    MainActivity.myRefUser.child(getResources().getString(R.string.nowtimestamp)).child(getResources().getString(R.string.timestamp)).runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                            mutableData.setValue(ServerValue.TIMESTAMP);
                                            return Transaction.success(mutableData);
                                        }
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                            Long nowtimeStamp = dataSnapshot.getValue(Long.class);
                                            rightofspinMilis = MainActivity.rightOfSpinDataSnapShot.child(getResources().getString(R.string.timestamp)).getValue(Long.class);
                                            if(nowtimeStamp > rightofspinMilis){
                                                MainActivity.myRefUser.child("rightofspin").child(getResources().getString(R.string.timestamp)).setValue(nowtimeStamp + 86400000).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        MainActivity.myRefUser.child("rightofspin").child("spinAdsCount").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                getTimeStampControlOfSpin();
                                                                luckyWheelView.startLuckyWheelWithTargetIndex(new Random().nextInt(12));
                                                            }
                                                        });

                                                        //Toast.makeText(getContext(), "Tıklandı", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }else{
                                                getTimeStampControlOfSpin();
                                                luckyWheelView.startLuckyWheelWithTargetIndex(new Random().nextInt(12));
                                            }
                                        }
                                    });
                                } else {
                                    //Toast.makeText(getContext(), "Tıklandı", Toast.LENGTH_SHORT).show();
                                    luckyWheelView.startLuckyWheelWithTargetIndex(new Random().nextInt(12));
                                }
                            }   else {
                                MainActivity.showPopUpInfo(null, "Uyarı!", "Çark çevirme hakkınız dolmuştur", getContext());
                            }
                        }
                    });
                }
            }

        });

    }
    private void setClickAdsListener(){
        adsConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isrunnigluckywheel){
                    if(mRewardedVideoAd.isLoaded()){
                        MainActivity.myRefUser.child("rightofspin").child(getResources().getString(R.string.spicAdsCount)).setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mRewardedVideoAd.show();
                            }
                        });
                    }
                }
            }
        });
        spinAdsAnimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isrunnigluckywheel){
                    if(mRewardedVideoAd.isLoaded()){
                    MainActivity.myRefUser.child("rightofspin").child(getResources().getString(R.string.spicAdsCount)).setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                                mRewardedVideoAd.show();
                        }
                    });
                    }
                }
            }
        });
    }

    private void increaseTheReturnValue(Integer index) {
        final Integer value = data.get(index).valueNumber;
        Integer itemId = data.get(index).id;
        if (itemId == idDiamond) {
            MainActivity.myRefUser.child("token").child("diamondValue").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer diamondValue = mutableData.getValue(Integer.class);
                    diamondValue += value;
                    mutableData.setValue(diamondValue);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    if(b){
                        MainActivity.showPopUpInfo(R.drawable.diamond, "Tebrikler!", value + " " + "Adet elmas kazandınız", getContext());
                    }
                }
            });
        }
        else if(itemId == idJoker1) {
            MainActivity.myRefUser.child("wildcards").child("fiftyFiftyValue").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer fiftyFiftyValue = mutableData.getValue(Integer.class);
                    fiftyFiftyValue += value;
                    mutableData.setValue(fiftyFiftyValue);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    if(b){
                        MainActivity.showPopUpInfo(R.drawable.joker1, "Tebrikler!", value + " " + "Adet joker kazandınız", getContext());
                    }
                }
            });
        }
        else if(itemId == idJoker2) {
            MainActivity.myRefUser.child("wildcards").child("doubleDipValue").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer doubleDipValue = mutableData.getValue(Integer.class);
                    doubleDipValue += value;
                    mutableData.setValue(doubleDipValue);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    if(b){
                        MainActivity.showPopUpInfo(R.drawable.joker2, "Tebrikler!", value + " " + "Adet joker kazandınız", getContext());
                    }
                }
            });

        }
        else if(itemId == idReSpin) {
            MainActivity.myRefUser.child("rightofspin").child("spinValue").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer spinValue = mutableData.getValue(Integer.class);
                    spinValue += value;
                    mutableData.setValue(spinValue);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    if(b){
                        MainActivity.showPopUpInfo(R.drawable.spin, "Tebrikler!", value + " " + "Adet çark çevirme hakkı kazandınız", getContext());
                    }
                }
            });

        }
        else if(itemId == idHealth) {
            MainActivity.myRefUser.child("wildcards").child("healthValue").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer healthValue = mutableData.getValue(Integer.class);
                    healthValue += value;
                    mutableData.setValue(healthValue);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    if(b){
                        MainActivity.showPopUpInfo(R.drawable.health, "Tebrikler!", value + " " + "Adet can kazandınız", getContext());
                    }
                }
            });
        }
        else if(itemId == idGreenGem) {
            MainActivity.myRigtOfGameRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Integer rightOfGame = mutableData.getValue(Integer.class);
                    rightOfGame += value;
                    mutableData.setValue(rightOfGame);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    if(b){
                        MainActivity.showPopUpInfo(R.drawable.greengem, "Tebrikler!", value + " " + "Adet tahmin etme hakkı kazandınız", getContext());
                    }
                }
            });
        }
        getTimeStampControlOfSpin();
    }

    private void setSpinItem() {

        data = new ArrayList<>();
        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = "x1";
        luckyItem1.valueNumber = 1;
        luckyItem1.icon = R.drawable.health;
        luckyItem1.color = Color.parseColor(color1);
        luckyItem1.id = idHealth;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = "x3";
        luckyItem2.valueNumber = 3;
        luckyItem2.icon = R.drawable.joker2;
        luckyItem2.color = Color.parseColor(color2);
        luckyItem2.id = idJoker2;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = "x1";
        luckyItem3.valueNumber = 1;
        luckyItem3.icon = R.drawable.joker1;
        luckyItem3.color = Color.parseColor(color3);
        luckyItem3.id = idJoker1;
        data.add(luckyItem3);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = "x5";
        luckyItem4.valueNumber = 5;
        luckyItem4.id = idDiamond;
        luckyItem4.icon = R.drawable.diamond;
        luckyItem4.color = Color.parseColor(color1);
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = "x1";
        luckyItem5.valueNumber = 1;
        luckyItem5.id = idGreenGem;
        luckyItem5.icon = R.drawable.greengem;
        luckyItem5.color = Color.parseColor(color2);
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = "x2";
        luckyItem6.id = idReSpin;
        luckyItem6.valueNumber = 2;
        luckyItem6.icon = R.drawable.spin;
        luckyItem6.color = Color.parseColor(color3);
        data.add(luckyItem6);
        //////////////////

        //////////////////////
        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = "x2";
        luckyItem7.valueNumber = 2;
        luckyItem7.id = idHealth;
        luckyItem7.icon = R.drawable.health;
        luckyItem7.color = Color.parseColor(color1);
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = "x1";
        luckyItem8.id = idJoker2;
        luckyItem8.valueNumber = 1;
        luckyItem8.icon = R.drawable.joker2;
        luckyItem8.color = Color.parseColor(color2);
        data.add(luckyItem8);

        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.text = "x3";
        luckyItem9.id = idJoker1;
        luckyItem9.valueNumber = 3;
        luckyItem9.icon = R.drawable.joker1;
        luckyItem9.color = Color.parseColor(color3);
        data.add(luckyItem9);

        LuckyItem luckyItem10 = new LuckyItem();
        luckyItem10.text = "x10";
        luckyItem10.id = idDiamond;
        luckyItem10.valueNumber = 10;
        luckyItem10.icon = R.drawable.diamond;
        luckyItem10.color = Color.parseColor(color1);
        data.add(luckyItem10);

        LuckyItem luckyItem11 = new LuckyItem();
        luckyItem11.text = "x2";
        luckyItem11.id = idGreenGem;
        luckyItem11.valueNumber = 2;
        luckyItem11.icon = R.drawable.greengem;
        luckyItem11.color = Color.parseColor(color2);
        data.add(luckyItem11);

        LuckyItem luckyItem12 = new LuckyItem();
        luckyItem12.text = "x1";
        luckyItem12.id = idReSpin;
        luckyItem12.valueNumber = 1;
        luckyItem12.icon = R.drawable.spin;
        luckyItem12.color = Color.parseColor(color3);
        data.add(luckyItem12);

        luckyWheelView.setData(data);
        luckyWheelView.setRound(7);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mRewardedVideoAd !=null){
            mRewardedVideoAd.resume(getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRewardedVideoAd.destroy(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mRewardedVideoAd !=null)
        mRewardedVideoAd.pause(getActivity());
        if (countDownTimer != null) {
            //Toast.makeText(getContext(),"Sayaç Bekletildi" , Toast.LENGTH_SHORT).show();
            countDownTimer.cancel();
        }
        if (isrunnigluckywheel == true) isrunnigluckywheel = false;

    }

}
