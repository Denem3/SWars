package com.app.selfiewars.selfiewars;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database {
    FirebaseDatabase mdatabase;
    DatabaseReference myDataBaseRef;
    DatabaseReference myDataBaseRefUser;
    int i = 0;
    public Database() {
        mdatabase = FirebaseDatabase.getInstance();
        myDataBaseRef = mdatabase.getReference();
        myDataBaseRefUser = mdatabase.getReference();
        myDataBaseRef.keepSynced(true);
    }

    public void mReadDataOnce(String child, final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }
    public void mReadDataRealTime(String child, final OnGetDataListener listener) {
        listener.onStart();

        myDataBaseRefUser.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }
    public void mReadDataAndGetUserListForGuessIt(final FirebaseAuth mAuth, final String ApprovedUsers, final String CorrectUsers, final OnGetUserlistDataListener listener){
        listener.onStart();
        final List<GuessItUserData> guessItUserDataList = new ArrayList<>();
        final List<String> userPropertiesUidList = new ArrayList<>();
        if(mAuth != null){
            listener.onProgress("Veriler çekiliyor.");
            //local yap beta olup olmadığını
            myDataBaseRef.child("AppType").child("isBeta").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class)){
                            myDataBaseRef.child("Dataset").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                                        i++;
                                        //Log.d("Veri adedi","**"+i+"**");
                                        String username = ds.child("displayName").getValue(String.class);
                                        username = "SelfieWars Player "+i;
                                        String photoUrl = ds.child("photoUrl").getValue(String.class);
                                        Integer age = ds.child("age").getValue(Integer.class);
                                        String uid = ds.getKey();
                                        GuessItUserData guessItUserData = new GuessItUserData(uid,age,photoUrl,username);
                                        guessItUserDataList.add(guessItUserData);
                                        if(guessItUserDataList.size() == dataSnapshot.getChildrenCount()){
                                            Collections.shuffle(guessItUserDataList);
                                            listener.onSuccess(guessItUserDataList,true);
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else
                        {
                            myDataBaseRef.child(ApprovedUsers).limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshotApproved) {
                                                            myDataBaseRef.child(CorrectUsers).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshotCorrect) {
                                                                    if(!dataSnapshotCorrect.exists()){
                                                                        if(dataSnapshotApproved.exists()){
                                                                            for (DataSnapshot ds:dataSnapshotApproved.getChildren()){
                                                                                i++;
                                                                                if(guessItUserDataList.size() <= dataSnapshotApproved.getChildrenCount() - 1 && !ds.getKey().equals(mAuth.getUid())){
                                                                                    guessItUserDataList.add(ds.getValue(GuessItUserData.class));
                                                                                }else if(ds.getKey().equals(mAuth.getUid())){
                                                                                    continue;
                                                                                }
                                                                                if(guessItUserDataList.size() == dataSnapshotApproved.getChildrenCount() - 1){
                                                                                    listener.onProgress("Yarışma ayarları yapılandırılıyor.");
                                                                                    listener.onProgress("Yarışma Hazır...");
                                                                                    listener.onSuccess(guessItUserDataList,false);
                                                                                    break;
                                                                                }
                                                                            }
                                                }
                                            }else {
                                                if(dataSnapshotApproved.exists()){
                                                    listener.onProgress("Uygunsuz kullanıcılar ayıklandı.");
                                                    for (DataSnapshot ds:dataSnapshotApproved.getChildren()){
                                                        if(dataSnapshotCorrect.exists() && !dataSnapshotCorrect.hasChild(ds.getKey())){
                                                            if(guessItUserDataList.size() <= dataSnapshotApproved.getChildrenCount()-1 &&!ds.getKey().equals(mAuth.getUid())){
                                                                guessItUserDataList.add(ds.getValue(GuessItUserData.class));
                                                            }else {
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                    if(guessItUserDataList.size() == 0){
                                                        myDataBaseRef.child(ApprovedUsers).limitToLast(100).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull final DataSnapshot dataSnapshotApproved) {
                                                                myDataBaseRef.child(CorrectUsers).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotCorrect) {
                                                                        if(!dataSnapshotCorrect.exists()){
                                                                            if(dataSnapshotApproved.exists()){
                                                                                for (DataSnapshot ds:dataSnapshotApproved.getChildren()){
                                                                                    if(guessItUserDataList.size() < dataSnapshotApproved.getChildrenCount() - 1 && !ds.getKey().equals(mAuth.getUid())){
                                                                                        guessItUserDataList.add(ds.getValue(GuessItUserData.class));
                                                                                    }else if(ds.getKey().equals(mAuth.getUid())){
                                                                                        continue;
                                                                                    }
                                                                                    if(guessItUserDataList.size() == dataSnapshotApproved.getChildrenCount() - 1){
                                                                                        listener.onProgress("Yarışma ayarları yapılandırılıyor.");
                                                                                        listener.onProgress("Yarışma Hazır...");
                                                                                        listener.onSuccess(guessItUserDataList,false);
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }else {
                                                                            if(dataSnapshotApproved.exists()){
                                                                                listener.onProgress("Uygunsuz kullanıcılar ayıklandı.");
                                                                                for (DataSnapshot ds:dataSnapshotApproved.getChildren()){
                                                                                    if(dataSnapshotCorrect.exists()&&!dataSnapshotCorrect.hasChild(ds.getKey())){
                                                                                        if(guessItUserDataList.size() <= dataSnapshotApproved.getChildrenCount() - 1 &&!ds.getKey().equals(mAuth.getUid())){
                                                                                            guessItUserDataList.add(ds.getValue(GuessItUserData.class));
                                                                                        }else {
                                                                                            continue;
                                                                                        }
                                                                                    }
                                                                                }
                                                                                if (guessItUserDataList.size() !=0)
                                                                                listener.onSuccess(guessItUserDataList,false);
                                                                                else listener.onFailed();
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                        listener.onFailed();
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                listener.onFailed();
                                                            }
                                                        });
                                                    }else {
                                                        listener.onSuccess(guessItUserDataList,false);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            listener.onFailed();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    listener.onFailed();
                                }
                            });

                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }else listener.onFailed();

    }

}
