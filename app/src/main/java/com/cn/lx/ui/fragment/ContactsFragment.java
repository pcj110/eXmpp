package com.cn.lx.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cn.lx.entries.SectionUserInfoEntrie;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.activity.ChatActivity;
import com.cn.lx.ui.activity.NewFriendListActivity;
import com.cn.lx.ui.activity.UserInfoActivity;
import com.cn.lx.ui.adapter.SectionAdapter;
import com.cn.lx.util.PinyinUtils;
import com.cn.lx.widget.SideBarView;
import com.cn.lx.xmpp.XmppManager;
import com.cn.lx.xmpp.XmppService;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import estar.com.xmpptest.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xueliang on 2017/4/9.
 */

public class ContactsFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.sideBar)
    SideBarView sideBar;

    private Handler handler = new Handler();
    private OverlayThread overlayThread = new OverlayThread();

    WindowManager windowManager;
    private TextView overlay;
    private HashMap<String, Integer> indexMap = new HashMap<>();
    List<SectionUserInfoEntrie> newlist = new ArrayList<SectionUserInfoEntrie>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Observable.create(new ObservableOnSubscribe<List<SectionUserInfoEntrie>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SectionUserInfoEntrie>> e) throws Exception {

                List<UserInfoEntrie> userInfoEntries = DataSupport.findAll(UserInfoEntrie.class);
                newlist.clear();
                newlist.addAll(arrangeData(userInfoEntries));
                e.onNext(newlist);
                XmppManager manager = XmppManager.getXmppManager();
                if (manager != null) {
                    if (!manager.getClient().isAuthenticated()) {
//                        manager.getClient().login("","");
                    }
                    userInfoEntries = manager.getClient().getAllEntries();
                    DataSupport.deleteAll(UserInfoEntrie.class);
                    DataSupport.saveAllAsync(userInfoEntries).listen(new SaveCallback() {
                        @Override
                        public void onFinish(boolean success) {

                        }
                    });

                    newlist.clear();
                    newlist.addAll(arrangeData(userInfoEntries));
                    e.onNext(newlist);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<SectionUserInfoEntrie>>() {

            @Override
            public void accept(List<SectionUserInfoEntrie> o) throws Exception {

                initAdapter(o);

            }

        });

        sideBar.setOnTouchingLetterChangedListener(new SideBarView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                overlay.setText(s);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // Уoverlay
                handler.postDelayed(overlayThread, 1500);
                if ("↑".equals(s)) {
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                } else {
                    if (indexMap.containsKey(s)) {
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(indexMap.get(s), 0);
                    }
                }

            }
        });
        initOverlay();
    }

    private List<SectionUserInfoEntrie> arrangeData(List<UserInfoEntrie> userInfoEntries) {
        HashMap<String, List<SectionUserInfoEntrie>> listHashMap = new HashMap<String, List<SectionUserInfoEntrie>>();
        SectionUserInfoEntrie sectionUserInfoEntrie = null;
        for (UserInfoEntrie userInfoEntrie : userInfoEntries) {
            sectionUserInfoEntrie = new SectionUserInfoEntrie(false, "");
            sectionUserInfoEntrie.setUserInfo(userInfoEntrie);
            String fristS = PinyinUtils.fristStrByUpp(sectionUserInfoEntrie.getUserInfo().getNiceName());
            if (listHashMap.get(fristS) == null) {
                listHashMap.put(fristS, new ArrayList<SectionUserInfoEntrie>());
            }
            listHashMap.get(fristS).add(sectionUserInfoEntrie);

        }

        List<SectionUserInfoEntrie> newlist = new ArrayList<SectionUserInfoEntrie>();
        String index = "ABCDEFGHIJKLMNOPKRSTUVWXYZ";
        char[] c = index.toCharArray();
        int cLength = c.length;
        for (int i = 0; i < cLength; i++) {
            List<SectionUserInfoEntrie> list = listHashMap.get(c[i] + "");
            if (list != null) {
                indexMap.put(c[i] + "", newlist.size());
                sectionUserInfoEntrie = new SectionUserInfoEntrie(true, c[i] + "");
                newlist.add(sectionUserInfoEntrie);
                newlist.addAll(list);
                listHashMap.remove(c[i] + "");
            }
        }
        for (String key : listHashMap.keySet()) {
            indexMap.put("#", newlist.size());
            sectionUserInfoEntrie = new SectionUserInfoEntrie(true, "#");
            newlist.add(sectionUserInfoEntrie);
            newlist.addAll(listHashMap.get(key));
        }
        return newlist;
    }

    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this.getActivity());
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) this.getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);


    }

    public void initAdapter(final List<SectionUserInfoEntrie> list) {
        if (recyclerView.getAdapter() == null) {

            SectionAdapter sectionAdapter = new SectionAdapter(R.layout.item_contact_list, R.layout.item_contact_head, list);
            sectionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    startActivity(UserInfoActivity.callingIntent(getActivity(), list.get(position).getUserInfo().getUserName()));
                }
            });
            View header = LayoutInflater.from(getActivity()).inflate(R.layout.contact_head, recyclerView, false);
            sectionAdapter.setHeaderView(header);
            recyclerView.setAdapter(sectionAdapter);
            RelativeLayout newFriendLayout = (RelativeLayout) header.findViewById(R.id.newfriend);
            newFriendLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), NewFriendListActivity.class));
                }
            });
        }else{
            recyclerView.getAdapter().notifyDataSetChanged();
        }

    }


    private class OverlayThread implements Runnable {

        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }

    }

}
