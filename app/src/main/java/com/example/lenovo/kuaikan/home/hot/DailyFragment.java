package com.example.lenovo.kuaikan.home.hot;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lenovo.kuaikan.HomeActivity;
import com.example.lenovo.kuaikan.R;
import com.example.lenovo.kuaikan.base.LazyFragmen;
import com.example.lenovo.kuaikan.home.comicread.view.ReadActivity;
import com.example.lenovo.kuaikan.home.hot.bean.BeanHomeHot;
import com.example.lenovo.kuaikan.home.hot.reqhot.ReqHot;
import com.example.lenovo.kuaikan.utils.ListUtil;
import com.example.lenovo.kuaikan.utils.ToastUtil;
import com.example.lenovo.kuaikan.utils.Urls;
import com.example.lenovo.kuaikan.utils.netutil.NetAsynTask;
import com.example.lenovo.kuaikan.widget.XRecyclerview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Zhanglibin on 2017/3/28.
 */

public class DailyFragment extends LazyFragmen {
    @BindView(R.id.sun_xRecyclerview)
    XRecyclerview mXRecyclerview;
    @BindView(R.id.imge_data_null)
    ImageView mImgeDataNull;
    Unbinder unbinder;
    private List<String> mList;
    private LinearLayoutManager mManager;
    private List<BeanHomeHot.DataBean.ComicsBean> mComicsBeen;
    private int mPageSize;
    private int mPage = 0;
    private String mTabId;
    private DailyAdapter mDailyAdapter;
    private boolean mIsPrepared;

    @Override
    public int getLayoutId() {
        return R.layout.daily_fragment;
    }

    @Override
    protected void lazyLoad() {
        if (!mIsPrepared || !isVisible) {
            return;
        }
        getServerData(false, false);

    }


    public static DailyFragment newInstance(String tabId) {
        DailyFragment fragment = new DailyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tabId", tabId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        mTabId = getArguments().getString("tabId");
        //实例化，参数为context上下文
        mManager = new LinearLayoutManager(getActivity());
        //设置布局管理器
        mXRecyclerview.setLayoutManager(mManager);
        //设置动画
        mXRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mIsPrepared = true;
        lazyLoad();
        mXRecyclerview.setOnRefreshLoadMore(new XRecyclerview.OnRefreshLoadMore() {
            @Override
            public void onRefresh() {
                //每次刷新总是请求第一页的数据
                mPage = 0;
                getServerData(true, false);
            }

            @Override
            public void onLoadMore() {
                if (mPageSize == 20) {
                    ToastUtil.showMessage("正在拼命加载n(*≧▽≦*)n");
                    getServerData(false, true);
                }
            }
        });
        mComicsBeen = new ArrayList<>();
        mDailyAdapter = new DailyAdapter(mComicsBeen, getActivity());
        mDailyAdapter.setHotAdapterEvent(new DailyAdapter.HotAdapterEvent() {
            @Override
            public void event() {
                ReadActivity.toRead(getActivity(),mDailyAdapter.getComicsId());
            }
        });
        mXRecyclerview.setAdapter(mDailyAdapter);
    }

    private void getServerData(final Boolean refresh, final Boolean loadMore) {
        if (mXRecyclerview != null) {
            mXRecyclerview.showLoading(true);
        }
        final ReqHot req = new ReqHot();
        String url = Urls.parse(Urls.HOME_HOT_SUNDAY, mTabId, mPage);
        NetAsynTask.connectByGet(url, null, req, new NetAsynTask.CallBack() {
            @Override
            public void onGetSucc() {
                if (req.code == 200) {
                    ((HomeActivity)getActivity()).mIamgeSplash.setVisibility(View.GONE);
                    ((HomeActivity)getActivity()).mBottomNavigationBar.setVisibility(View.VISIBLE);
                    if (refresh) {
                        mComicsBeen.clear();
                        mXRecyclerview.refreshFinish();
                    }
                    if (loadMore) {
                        mXRecyclerview.loadMoreFinish();
                    }
                    //初始化一个bean用保存本次请求下来的数据
                    List<BeanHomeHot.DataBean.ComicsBean> bean;
                    BeanHomeHot beanHomeHot = req.getT();
                    bean = beanHomeHot.getData()
                            .getComics();
                    if (ListUtil.isEmpty(bean)) {
                        mImgeDataNull.setVisibility(View.VISIBLE);
                        mXRecyclerview.setVisibility(View.GONE);
                        return;
                    }
                    //是否有下一页
                    mPageSize = bean.size();
                    if (mPageSize == 20) {
                        mPage++;
                    }
                    // 将每次网络请求的数据添加到总的List
                    mComicsBeen.addAll(bean);
                    //清空为下次请求做准备
                    bean.clear();
                    //设置adapter
                    mDailyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetFinished() {
                mXRecyclerview.showLoading(false);
            }

            @Override
            public void onGetFaild() {

            }

            @Override
            public void onGetError() {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
