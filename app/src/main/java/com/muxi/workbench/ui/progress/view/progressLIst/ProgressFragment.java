package com.muxi.workbench.ui.progress.view.progressLIst;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.muxi.workbench.R;
import com.muxi.workbench.commonUtils.AppExecutors;
import com.muxi.workbench.ui.progress.contract.ProgressContract;
import com.muxi.workbench.ui.progress.ProgressFilterType;
import com.muxi.workbench.ui.progress.model.Progress;
import com.muxi.workbench.ui.progress.model.progressList.ProgressListRemoteAndLocalDataSource;
import com.muxi.workbench.ui.progress.model.progressList.ProgressListRepository;
import com.muxi.workbench.ui.progress.model.StickyProgressDatabase;
import com.muxi.workbench.ui.progress.presenter.ProgressListPresenter;
import com.muxi.workbench.ui.progress.view.progressLIst.ProgressListAdapter.ProgressItemListener;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment implements ProgressContract.View {

    private ProgressContract.Presenter mPresenter;

    private ProgressTitleBar mProgressTitleBar;

    private RecyclerView mProgressListRv;

    private SwipeRefreshLayout mProgressSrl;

    private ProgressListAdapter mAdapter;

    private ProgressFilterType lastProgressFilterType = ProgressFilterType.ALL_PROGRESS;

    private int lastPage = 1;


    ProgressItemListener mItemListener = new ProgressItemListener() {
        @Override
        public void onItemClick(Progress clickedProgress) {
            mPresenter.openProgressDetails(clickedProgress);
        }

        @Override
        public void onMoreClick() {
            Log.e("progressfragment","addmore to load");
            mPresenter.loadProgressList(false);
        }

        @Override
        public void onUserClick(int uid) {
            mPresenter.openUserInfo(uid);
        }

        @Override
        public void onLikeClick(Progress likeProgress, int position) {
            if ( likeProgress.getIfLike() == 1 )
                mPresenter.cancelLikeProgress(likeProgress.getSid(), position);
            else
                mPresenter.likeProgress(likeProgress.getSid(), position);
        }

        @Override
        public void onCommentClick(Progress commentProgress) {
            if ( commentProgress.getCommentCount() == 0 ) {
                String comment ="";
                ///todo 去往详情页 获取评论焦点
            } else {
                mPresenter.openProgressDetails(commentProgress);
            }
        }

        @Override
        public void onEditClick(Progress editProgress) {
            Toast.makeText(getContext(), "去编辑进度",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("progressfragment","oncreate");
        super.onCreate(savedInstanceState);
        mPresenter = new ProgressListPresenter(
                ProgressListRepository.getInstance(
                    ProgressListRemoteAndLocalDataSource.getInstance(
                            StickyProgressDatabase.getInstance(getContext()).ProgressDao(),
                        new AppExecutors()
                    )
                ), this);
    }

    @Override
    public void onPause() {
        Log.e("progressfragment","onpause");
        super.onPause();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e("progressfragment","onsaveInstanceState");
        outState.putInt("ProgressFilerType",mPresenter.getFiltering());
        outState.putInt("page",mPresenter.getPage());
    }

    @Override
    public void onResume() {
        Log.e("progressfragment","onresume");
        super.onResume();
        mPresenter.start(lastProgressFilterType, lastPage);
    }

    @Override
    public void onStart() {
        Log.e("progressfragment","onstart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.e("progressfragment","onstop");
        super.onStop();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.e("progressfragment","onviewStateRestroed");
        super.onViewStateRestored(savedInstanceState);
        if ( savedInstanceState != null ) {
            switch (savedInstanceState.getInt("ProgressFilerType", 0)) {
                case 0:
                    lastProgressFilterType = ProgressFilterType.ALL_PROGRESS;
                    break;
                case 1:
                    lastProgressFilterType = ProgressFilterType.PRODUCT_PROGRESS;
                    break;
                case 2:
                    lastProgressFilterType = ProgressFilterType.FRONTEND_PROGRESS;
                    break;
                case 3:
                    lastProgressFilterType = ProgressFilterType.ANDROID_PROGRESS;
                    break;
                case 4:
                    lastProgressFilterType = ProgressFilterType.BACKEND_PROGRESS;
                    break;
                case 5:
                    lastProgressFilterType = ProgressFilterType.DESIGN_PROGRESS;
                    break;
            }
            lastPage = savedInstanceState.getInt("page", 1);
        }
    }

    @Override
    public void onDestroyView() {
        Log.e("progressfragment","ondestoryview");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e("progressfragment","ondestory");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.e("progressfragment","ondetach");
        super.onDetach();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("progressfragment","oncreateview");
        if ( savedInstanceState != null ) {
            switch (savedInstanceState.getInt("ProgressFilerType", 0)) {
                case 0:
                    lastProgressFilterType = ProgressFilterType.ALL_PROGRESS;
                    break;
                case 1:
                    lastProgressFilterType = ProgressFilterType.PRODUCT_PROGRESS;
                    break;
                case 2:
                    lastProgressFilterType = ProgressFilterType.FRONTEND_PROGRESS;
                    break;
                case 3:
                    lastProgressFilterType = ProgressFilterType.ANDROID_PROGRESS;
                    break;
                case 4:
                    lastProgressFilterType = ProgressFilterType.BACKEND_PROGRESS;
                    break;
                case 5:
                    lastProgressFilterType = ProgressFilterType.DESIGN_PROGRESS;
                    break;
            }
            lastPage = savedInstanceState.getInt("page", 1);
        }

        View root = inflater.inflate(R.layout.fragment_progress, container, false);

        mProgressSrl = root.findViewById(R.id.srl_progress);
        mProgressSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("progressfragment","swipeRefreshLayout refresh to load");
                mPresenter.loadProgressList(true);
            }
        });
        mProgressSrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgressSrl.isRefreshing()) {
                    mProgressSrl.setRefreshing(false);
                    Toast.makeText(getContext(), "已停止更新", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter = new ProgressListAdapter(mPresenter, getContext(), new ArrayList<>(), mItemListener);
        mProgressListRv = root.findViewById(R.id.rv_progress);
        mProgressListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgressListRv.setAdapter(mAdapter);

        mProgressTitleBar = root.findViewById(R.id.ptb_progress);
        mProgressTitleBar.setOptionSp(getContext());
        mProgressTitleBar.setBackgroundColor(Color.parseColor("#ffffff"));
        mProgressTitleBar.setElevation(2);
        mProgressTitleBar.setOptionSelectListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("progressfragment","onItemSelected"+position);
                mProgressTitleBar.adapter.setSelectedPosition(position);
                switch (position) {
                    case 0:
                        mPresenter.setProgressFilterType(ProgressFilterType.ALL_PROGRESS);
                        Log.e("progressfragment","change spinner to all to load");
                        mPresenter.loadProgressList(true);
                        break;
                    case 1:
                        mPresenter.setProgressFilterType(ProgressFilterType.PRODUCT_PROGRESS);
                        Log.e("progressfragment","change spinner to product to load");
                        mPresenter.loadProgressList(true);
                        break;
                    case 2:
                        mPresenter.setProgressFilterType(ProgressFilterType.DESIGN_PROGRESS);
                        Log.e("progressfragment","change spinner to design to load");
                        mPresenter.loadProgressList(true);
                        break;
                    case 3:
                        mPresenter.setProgressFilterType(ProgressFilterType.FRONTEND_PROGRESS);
                        Log.e("progressfragment","change spinner to frontend to load");
                        mPresenter.loadProgressList(true);
                        break;
                    case 4:
                        mPresenter.setProgressFilterType(ProgressFilterType.ANDROID_PROGRESS);
                        Log.e("progressfragment","change spinner to android to load");
                        mPresenter.loadProgressList(true);
                        break;
                    case 5:
                        mPresenter.setProgressFilterType(ProgressFilterType.BACKEND_PROGRESS);
                        Log.e("progressfragment","change spinner to backend to load");
                        mPresenter.loadProgressList(true);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mProgressTitleBar.setAddListener(v -> {
             ///TODO  to Progress-editing Fragment
        });

        return root;
    }

    @Override
    public void setPresenter(ProgressContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showProgressList(List<Progress> progressList) {
        mAdapter.replaceData(progressList);
        mProgressSrl.setRefreshing(false);
    }

    @Override
    public void showCommentView() {
        Toast.makeText(getContext(),"去评论",Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDetail(int sid) {
        Toast.makeText(getContext(),"去详情页",Toast.LENGTH_LONG).show();
    }

    @Override
    public void refreshLikeProgress(int position, int iflike) {
        mAdapter.notifyProgress(position, iflike);
    }
/*
    @Override
    public void showSelectAllFilter() {
        mProgressTitleBar.setSpinnerLabel(0);
    }

    @Override
    public void showSelectProductFilter() {
        mProgressTitleBar.setSpinnerLabel(1);
    }

    @Override
    public void showSelectBackendFilter() {
        mProgressTitleBar.setSpinnerLabel(5);
    }

    @Override
    public void showSelectFrontendFilter() {
        mProgressTitleBar.setSpinnerLabel(3);
    }

    @Override
    public void showSelectAndroidFilter() {
        mProgressTitleBar.setSpinnerLabel(4);
    }

    @Override
    public void showSelectDesignFilter() {
        mProgressTitleBar.setSpinnerLabel(2);
    }*/

    @Override
    public void showMoreProgress(List<Progress> progresses) {
        mAdapter.addMoreProgress(progresses);
    }

    @Override
    public void showUserInfo(int uid) {
        Toast.makeText(getContext(),"去往个人主页",Toast.LENGTH_LONG).show();
        ///todo intent to info
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), "失败辽", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showAddNewProgress() {
        Toast.makeText(getContext(),"去往新进度编辑页",Toast.LENGTH_LONG).show();
        ///todo intent to empty edit-fragment
    }

    @Override
    public void showDeleteProgress(int position) {
        mAdapter.removeData(position);
    }

    @Override
    public void moveNewStickyProgress(int position) {
        mAdapter.moveProgress(position, 0);
    }

    @Override
    public void moveDeleteStickyProgress(int position) {
        mAdapter.moveProgress(position, 1);
    }
}