package com.zia.easybookmodule.rx;

import androidx.annotation.NonNull;

/**
 * Created by zia on 2019-05-15.
 * Subscriber只有Finish传输最终结果，这个接口增加了一个onPart方法，把中间的结果依次传递出来
 * 如果涉及并发，这个方法的onPart方法不能保证顺序
 */
public interface StepSubscriber<T> extends Subscriber<T> {
    void onFinish(@NonNull T t);

    void onError(@NonNull Exception e);

    void onMessage(@NonNull String message);

    void onProgress(int progress);

    /**
     * 部分下载的结果
     *
     * @param t
     */
    void onPart(@NonNull T t);
}
