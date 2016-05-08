package com.nakayama.myself_ai;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by nakayama on 2016/02/09.
 */
public class ContentActivity extends FragmentActivity {

    public static String SUMMARY = "text";
    public static String VIDEO = "movie";
    public static String PHOTO = "image";
    public static String AI = "ai";
    public static String CONTENTS_TYPE = "contents_type";

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Bundle mBundle;
    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        Intent intent = getIntent();
        mBundle = new Bundle();
        mBundle.putString(CONTENTS_TYPE,intent.getStringExtra(CONTENTS_TYPE));
        mBundle.putString("result",intent.getStringExtra("result"));
        setFragment(intent.getStringExtra(CONTENTS_TYPE));

    }

    public void setFragment(String type){
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        if(type.equals(SUMMARY)){
            mFragment = new SummaryFragment();
            mFragment.setArguments(mBundle);
            mFragmentTransaction.add(R.id.container,mFragment);
            mFragmentTransaction.commit();
        }else if(type.equals(VIDEO)){
            mFragment = new VideoFragment();
            mFragment.setArguments(mBundle);
            mFragmentTransaction.add(R.id.container,mFragment);
            mFragmentTransaction.commit();
        }else if(type.equals(PHOTO)){
            mFragment = new PhotoFragment();
            mFragment.setArguments(mBundle);
            mFragmentTransaction.add(R.id.container,mFragment);
            mFragmentTransaction.commit();
        }else if(type.equals(AI)){
            mFragmentTransaction.add(R.id.container,new AIFragment());
            mFragmentTransaction.commit();
        }
    }


}
