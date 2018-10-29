package com.github.johntinashe.bakingapp.utils;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"ALL", "unused"})
public class SimpleIdlingResource implements IdlingResource {

    @Nullable
    private volatile ResourceCallback mCallback;

    private final AtomicBoolean mIsIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        if (isIdleNow && mCallback != null) {
            Objects.requireNonNull(mCallback).onTransitionToIdle();
        }
    }

}

