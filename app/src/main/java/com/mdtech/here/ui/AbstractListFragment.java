package com.mdtech.here.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.mdtech.here.R;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.User;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Xiaoniu on 2015/12/25.
 */
public abstract class AbstractListFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_API = "api";
    private static final String ARG_ID = "id";

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

    public static Fragment newInstance(AbstractListFragment fragment, HereApi api, BigInteger id){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_API, api);
        bundle.putSerializable(ARG_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = (Button) view.findViewById(R.id.btn_load_more);
        button.setOnClickListener(this);

        mApi = (HereApi) getArguments().getSerializable(ARG_API);
        mId = (BigInteger) getArguments().getSerializable(ARG_ID);

        mRecyclerView.addOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onLoadMore(int current_page) {
                loadMore();
            }
        });

        picasso = new Picasso.Builder(getActivity()).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_more:
                loadMore();
                break;
        }
    }

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
