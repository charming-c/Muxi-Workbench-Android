package com.muxi.workbench.ui.progress.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.muxi.workbench.commonUtils.AppExecutors;
import com.muxi.workbench.commonUtils.NetUtil;
import com.muxi.workbench.ui.login.model.UserWrapper;
import com.muxi.workbench.ui.progress.model.net.CommentStautsBean;
import com.muxi.workbench.ui.progress.model.net.GetStatusListResponse;
import com.muxi.workbench.ui.progress.model.net.IfLikeStatusBean;
import com.muxi.workbench.ui.progress.model.net.LikeStatusResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ProgressDataSource implements DataSource {

    private static ProgressDataSource INSTANCE;

    private StickyProgressDao mStickyProgressDao;

    private AppExecutors mAppExecutors;

    private final String token = UserWrapper.getInstance().getToken();

    private final int uid = UserWrapper.getInstance().getUser().getUid();

    public static ProgressDataSource getInstance(StickyProgressDao stickyProgressDao, AppExecutors mAppExecutors) {
        if (INSTANCE == null) {
            synchronized (ProgressDataSource.class) {
                if ( INSTANCE == null ) {
                    INSTANCE = new ProgressDataSource(stickyProgressDao, mAppExecutors);
                }
            }
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private ProgressDataSource(StickyProgressDao stickyProgressDao, AppExecutors appExecutors) {
        mStickyProgressDao = stickyProgressDao;
        mAppExecutors = appExecutors;
    }

    @Override
    public void getProgressList(int page, @NonNull LoadProgressListCallback callback) {

        List<Progress> progressList = new ArrayList<>();

        NetUtil.getInstance().getApi().getStatusList(token, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( new Observer<GetStatusListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GetStatusListResponse getStatusListResponse) {
                        for (GetStatusListResponse.StatuListBean statuListBean : getStatusListResponse.getStatuList() ) {
                            progressList.add(new Progress(statuListBean.getSid(),
                                    statuListBean.getUid(), statuListBean.getAvatar(),
                                    statuListBean.getUsername(), statuListBean.getTime(),
                                    statuListBean.getContent(), statuListBean.isIflike(),
                                    statuListBean.getCommentCount(), statuListBean.getLikeCount()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        callback.onDataNotAvailable();
                    }

                    @Override
                    public void onComplete() {
                        callback.onProgressListLoaded(progressList);
                    }
                });
    }

    @Override
    public void commentProgress(int sid, String comment, CommentProgressCallback callback) {
///todo 详情页
        NetUtil.getInstance().getApi().commentStatus(token, sid, new CommentStautsBean())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void ifLikeProgress(int sid, boolean iflike, SetLikeProgressCallback callback) {

            NetUtil.getInstance().getApi().ifLikeStatus(token, sid, new IfLikeStatusBean(iflike?1:0))
                    .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LikeStatusResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(LikeStatusResponse likeStatusResponse) {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        callback.onFail();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("scuccess","like");
                        callback.onSuccessfulSet();
                    }
                });
    }

    @Override
    public void refreshProgressList() {
///todo
    }

    @Override
    public void deleteProgress(@NonNull int sid, DeleteProgressCallback callback) {
        NetUtil.getInstance().getApi().deleteStatus(token, sid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        callback.onFail();
                    }

                    @Override
                    public void onComplete() {
                        callback.onSuccessfulDelete();
                    }
                });
    }

    @Override
    public void setStickyProgress(@NonNull Progress progress) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mStickyProgressDao.addStickyProgress(new StickyProgress(progress));
            }
        };
        mAppExecutors.diskIO().execute(r);

    }

    @Override
    public void getAllStickyProgress(@NonNull LoadStickyProgressCallback callback) {
        Log.e("ddd","");///todo   not active.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<StickyProgress> list = mStickyProgressDao.getStickyProgressList();
                List<Progress> progressList = new ArrayList<>();
                for ( int i = 0 ; i < list.size() ; i++ )
                    progressList.add(new Progress(list.get(i)));
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if ( !list.isEmpty() ) {
                            callback.onStickyProgressLoaded(progressList);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteStickyProgress(int sid) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mStickyProgressDao.deleteStickyProgress(sid);

            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getProgress(int sid, LoadProgressCallback callback) {

    }
}
