package com.summertaker.sakamichiguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.common.BaseFragmentInterface;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.OshimenManager;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.member.MemberDetailActivity;

public class MainOshimenFragment extends BaseFragment implements BaseFragmentInterface {

    private OshimenManager mOshimenManager;

    private CardView mEmpty;
    //private LinearLayout mEmpty;
    private MainOshimenAdapter mAdapter;
    private ListView mListView;
    private GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.main_oshimen_fragment, container, false);

        // *** in Fragment...
        // http://stackoverflow.com/questions/11741270/android-sharedpreferences-in-fragment
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
        mOshimenManager = new OshimenManager(sharedPreferences);
        //mMemberDataList = new ArrayList<>();

        mEmpty = (CardView) rootView.findViewById(R.id.empty);
        //mEmpty = (LinearLayout) rootView.findViewById(R.id.empty);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        //mGridView = (GridView) rootView.findViewById(R.id.gridView);

        return rootView;
    }

    @Override
    public void onResume() {
        //Log.e(mTag, "onResume()......................");
        super.onResume();

        if (mAdapter == null) {
            initAdapter();
        } else {
            // http://stackoverflow.com/questions/14503006/android-listview-not-refreshing-after-notifydatasetchanged
            // 데이터 어답터와 데이터 리스트를 elegant 하게 연결해줘야 notifyDataSetChanged()가 제대로 동작한다.
            mAdapter.refresh(mOshimenManager.load());
        }

        if (mListView.getCount() > 0) {
            mEmpty.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private void initAdapter() {
        mAdapter = new MainOshimenAdapter(mContext, mOshimenManager.load());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemberData memberData = (MemberData) parent.getItemAtPosition(position);

                GroupData groupData = new GroupData();
                groupData.setId(memberData.getGroupId());
                groupData.setName(memberData.getGroupName());

                Intent intent = new Intent(mContext, MemberDetailActivity.class);
                intent.putExtra("from", Config.MAIN_ACTION_OSHIMEN);
                intent.putExtra("groupData", groupData);
                intent.putExtra("teamName", memberData.getTeamName());
                intent.putExtra("memberData", memberData);

                startActivityForResult(intent, 100);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        /*mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemberData memberData = (MemberData) parent.getItemAtPosition(position);

                GroupData groupData = new GroupData();
                groupData.setGroupId(memberData.getGroupId());
                groupData.setName(memberData.getGroupName());

                Intent intent = new Intent(mContext, MemberDetailActivity.class);
                intent.putExtra("groupData", groupData);
                intent.putExtra("teamName", memberData.getTeamName());
                intent.putExtra("memberData", memberData);

                startActivityForResult(intent, 100);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        if (mGridView.getCount() > 0) {
            mEmpty.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void refresh(String data) {
        //Log.e(mTag, "refresh(): " + data);
        mGridView.setSelection(0);
    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }
}