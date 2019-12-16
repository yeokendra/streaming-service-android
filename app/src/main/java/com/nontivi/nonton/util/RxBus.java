package com.nontivi.nonton.util;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by mac on 11/16/17.
 */

public class RxBus {
    public static final String KEY_CHANNEL_CLICKED = "key_channel_clicked";
    public static final String KEY_ADS_LOADED = "key_ads_loaded";

    private volatile static RxBus sInstance;
    private ConcurrentMap<Object, List<Subject>> mSubjectMapper = new ConcurrentHashMap<>();

    private RxBus() {
    }

    public static RxBus get() {
        if (sInstance == null) {
            synchronized (RxBus.class) {
                if (sInstance == null) {
                    sInstance = new RxBus();
                }
            }
        }
        return sInstance;
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
        List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            mSubjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        //KLog.e("{register}subjectMapper: " + mSubjectMapper);
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        final List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null != subjectList) {
            subjectList.remove(observable);
            if (subjectList.isEmpty()) {
                mSubjectMapper.remove(tag);
            }
        }
        //KLog.e("{unregister}subjectMapper: " + mSubjectMapper);
    }

    @SuppressWarnings("unchecked")
    public void post(@NonNull Object tag, @NonNull Object content) {
        final List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null != subjectList && !subjectList.isEmpty()) {
            for (Subject subject : subjectList) {
                try {
                    subject.onNext(content);
                } catch (Exception e){
                    subject.onError(e);
                }
            }
        }
    }
}
