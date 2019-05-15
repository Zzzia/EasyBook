package com.zia.easybookmodule.rx;

public interface Observer<T> extends Disposable{
    Disposable subscribe(Subscriber<T> subscriber);

    T getSync() throws Exception;

    void dispose();
}