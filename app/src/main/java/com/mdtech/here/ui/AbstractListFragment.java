package com.mdtech.here.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.mdtech.here.AppApplication;
import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.service.Recorder;
import com.mdtech.here.util.AccountUtils;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.User;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.mdtech.here.util.LogUtils.LOGI;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/25/2015.
 */
public abstract class AbstractListFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_API = "api";

    protected BigInteger mId;
    protected HereApi mApi;

    protected Picasso picasso;

    protected Integer pageSize = 10;
    protected Integer pageNo = 0;
    protected boolean pageEnd = false;
    protected boolean isLoading = false;

    protected RecyclerView mRecyclerView;
    protected MyAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    public static Fragment newInstance(AbstractListFragment fragment, Bundle bundle, BigInteger id){
        bundle.putSerializable(Config.ARG_ENTITY_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(AbstractListFragment fragment, BigInteger id){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Config.ARG_ENTITY_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

//        Button button = (Button) view.findViewById(R.id.btn_load_more);
//        button.setOnClickListener(this);
        if(null != savedInstanceState) {
            mId = (BigInteger) savedInstanceState.getSerializable(Config.ARG_ENTITY_ID);
        }else {
            mId = (BigInteger) getArguments().getSerializable(Config.ARG_ENTITY_ID);
        }

        if(null != mId && mId.toString().equals(AccountUtils.getHereProfileId(getActivity()))) {
            mFab.setVisibility(View.VISIBLE);
            mFab.setOnClickListener(this);
        }
        mRecyclerView.addOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onLoadMore(int current_page) {
                loadMore();
            }
        });

        picasso = new Picasso.Builder(getActivity()).build();
        AppApplication app = (AppApplication)getActivity().getApplicationContext();
        mApi = app.getConnectionRepository().getPrimaryConnection(HereApi.class).getApi();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadMore();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Config.ARG_ENTITY_ID, mId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

//    protected static final int MESSAGE_UPDATE = 1;
//    protected Handler uiHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MESSAGE_UPDATE:
//                    mAdapter.notifyItemChanged(msg.arg1);
//                    break;
//            }
//        }
//    };

    /**
     * 加载更多
     */
    protected void loadMore() {
        if(pageEnd || isLoading) {
            return;
        }
        isLoading = true;
        new LoadMoreTask().execute();
    }

    protected abstract int execLoadMoreTask();

    public Integer pageDown() {
        return ++pageNo;
    }

    public Integer pageUp() {
        return --pageNo;
    }

    protected class LoadMoreTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                return execLoadMoreTask();
            }catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Integer size) {
            isLoading = false;
            if(null != size) {
                if(size < pageSize) {
                    pageEnd = true;
                }else {
                    pageDown();
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    protected static abstract class MyAdapter<E extends Object, VH extends MyAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {
        protected List<E> mDataset = new ArrayList<E>();
        protected Context mContext;
        protected Picasso picasso;

        public static abstract class ViewHolder<E extends Object> extends RecyclerView.ViewHolder implements View.OnClickListener {
            public View mView;
            public E mEntity;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mView.setOnClickListener(this);
                ButterKnife.bind(this, itemView);
            }
        }

        public MyAdapter(Context context, Picasso picasso) {
            mContext = context;
            this.picasso = picasso;
        }

        public void addAll(Collection<E> entitys) {
            mDataset.addAll(entitys);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
