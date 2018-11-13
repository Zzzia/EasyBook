package com.zia.easybookmodule.rx;

public interface Observer<T> {
    Disposable subscribe(Subscriber<T> subscriber);
}