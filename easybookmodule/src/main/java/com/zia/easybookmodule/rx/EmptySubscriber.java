package com.zia.easybookmodule.rx;

import androidx.annotation.NonNull;

/**
 * Created by zia on 2019-05-14.
 */
public class EmptySubscriber implements Subscriber {
    @Override
    public void onFinish(@NonNull Object o) {

    }

    @Override
    public void onError(@NonNull Exception e) {

    }

    @Override
    public void onMessage(@NonNull String message) {

    }

    @Override
    public void onProgress(int progress) {

    }
}
