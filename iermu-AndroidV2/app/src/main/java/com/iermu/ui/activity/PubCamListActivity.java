package com.iermu.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.iermu.R;
import com.iermu.ui.fragment.MainPublicFragment;
import com.iermu.ui.util.ShareUtil;
import com.viewinject.ViewHelper;

/**
 * 公共频道列表页面(体验入口)
 *
 */
public class PubCamListActivity extends BaseActionBarActivity {

    /**
     * 启动公共频道列表页面(体验入口)
     * @param ctx
     */
    public static void actionPubCamList(Activity ctx) {
        Intent intent = new Intent(ctx, PubCamListActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubcamlist);
        ShareUtil.initShareSdk(this);
        ViewHelper.inject(this);
        com.iermu.ui.view.updateApp.updateAppUtils.checkToUpdate(this, true);

        Fragment fragment = MainPublicFragment.actionPuCamList(this);
        switchFragment(R.id.pubcamlist_fragment, fragment);
    }

    private void switchFragment(int containerId, Fragment target) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (target.isDetached()) {
            transaction.attach(target);
        } else if (target.isHidden()) {
            transaction.show(target);
        } else if (!target.isAdded()) {
            transaction.add(containerId, target, String.valueOf(containerId));
        }
        transaction.commitAllowingStateLoss();
    }

}
