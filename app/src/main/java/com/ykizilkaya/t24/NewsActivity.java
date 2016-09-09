package com.ykizilkaya.t24;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ykizilkaya.t24.fragments.MainNewsFragment;
import com.ykizilkaya.t24.utils.Navigator;

/**
 * Created by YasirKizilkaya on 8.09.16.
 */

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        /** start transaction for main news fragment **/
        startTransactionFromActivity(new MainNewsFragment(), false, true);

    }

    public void startTransactionFromActivity(Fragment fragment, boolean addToBackStack, boolean isReplaceOrAdd) {
        /** start some transaction **/
        Navigator navigator = new Navigator(getSupportFragmentManager(), this);
        navigator.startFragmentTransaction(fragment, addToBackStack, isReplaceOrAdd);
    }


}
