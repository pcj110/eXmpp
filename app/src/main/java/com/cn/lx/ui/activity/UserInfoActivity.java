package com.cn.lx.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.base.BaseActivity;
import com.cn.lx.util.FileUtil;
import com.cn.lx.util.UserInfoUtil;
import com.cn.lx.xmpp.XmppManager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.cn.lx.util.GifSizeFilter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import estar.com.xmpptest.R;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class UserInfoActivity extends BaseActivity {

    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final int REQUEST_CHOOSE_PHOTO = 1101;
    private static final int REQUEST_CROP_PHOTO = 1102;

    @BindView(R.id.backdrop)
    ImageView backdrop;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.describe)
    TextView describe;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    private String currentUser;
    private UserInfoEntrie userInfo;

    public static Intent callingIntent(Context context, String userName) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("userName", userName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        currentUser = getIntent().getStringExtra("userName");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayAdapter adapter = new ArrayAdapter(UserInfoActivity.this, android.R.layout.simple_list_item_1, new String[]{"修改头像", "修改昵称"}) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (view instanceof AppCompatTextView) {
                            if (position == 0) {
                                ((AppCompatTextView) view).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_change_avatar), null, null, null);
                            } else {
                                ((AppCompatTextView) view).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_change_name), null, null, null);
                            }
                        }
                        return view;
                    }
                };
                new AlertDialog.Builder(UserInfoActivity.this).setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Matisse.from(UserInfoActivity.this)
                                    .choose(MimeType.ofAll())
                                    .countable(false)
                                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                    .gridExpectedSize(
                                            getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new GlideEngine())
                                    .capture(true).captureStrategy(new CaptureStrategy(true, "com.lx.fileprovider"))
                                    .forResult(REQUEST_CODE_CHOOSE);
                        } else {
                            changeNickName();
                        }
                    }
                })
                        .show();
            }
        });
        getDefaultObservable(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                UserInfoEntrie userInfoEntrie = XmppManager.getXmppManager().getClient().getUserInfo(currentUser);
                e.onNext(userInfoEntrie);
            }
        }).subscribe(new Consumer<UserInfoEntrie>() {
            @Override
            public void accept(UserInfoEntrie o) throws Exception {
                userInfo = o;
                collapsingToolbarLayout.setTitle(o.getNiceName());
                userName.setText(o.getUserName());
                describe.setText(o.getDescribe());
                sex.setText(o.getSex());
                UserInfoUtil.loadAvatar(UserInfoActivity.this,backdrop,userInfo.getUserName());
            }
        });
        if (!currentUser.equals(getUserName())) {
            fab.setVisibility(View.GONE);
            ((View) describe.getParent()).setOnClickListener(null);
            sex.setOnClickListener(null);
        } else {
            findViewById(R.id.sendMsgBtn).setVisibility(View.GONE);
            findViewById(R.id.addUser).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Uri uri = Matisse.obtainResult(data).get(0);
            startActivityForResult(CropActivity.callingIntent(this, uri), REQUEST_CROP_PHOTO);

//            mAdapter.setData(Matisse.obtainResult(data));
        } else if (requestCode == REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
            String dataStr = data.getStringExtra("data");
            changeAvator(dataStr);

        }
    }

    @OnClick(R.id.describeLayout)
    public void describeClick() {
        final EditText editText = new EditText(this);
        editText.setText(describe.getText().toString());
        new AlertDialog.Builder(this).setTitle("个人签名").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String describeText = editText.getText().toString();
                if (TextUtils.isEmpty(describeText)) {
                    Toast.makeText(UserInfoActivity.this, "输入为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                showDilog("正在修改个人签名");
                getDefaultObservable(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(ObservableEmitter e) throws Exception {
                        UserInfoEntrie userInfoEntrie = new UserInfoEntrie();
                        userInfoEntrie.setUserName(currentUser);
                        userInfoEntrie.setDescribe(describeText);
                        boolean bl =  manager.getClient().saveUserInfo(userInfoEntrie);
                        e.onNext(bl);

                    }
                }).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        dissmissDialog();
                        if (o) {
                            describe.setText(describeText);
                        }else{
                            Toast.makeText(UserInfoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).setNegativeButton("取消", null).show();
    }

    public void changeNickName(){
        final EditText editText = new EditText(this);
        editText.setText(collapsingToolbarLayout.getTitle());
        new AlertDialog.Builder(this).setTitle("昵称").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String nickName = editText.getText().toString();
                if (TextUtils.isEmpty(nickName)) {
                    Toast.makeText(UserInfoActivity.this, "输入为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDilog("正在修改昵称");
                getDefaultObservable(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(ObservableEmitter e) throws Exception {
                        UserInfoEntrie userInfoEntrie = new UserInfoEntrie();
                        userInfoEntrie.setUserName(currentUser);
                        userInfoEntrie.setNiceName(nickName);
                       boolean bl =  manager.getClient().saveUserInfo(userInfoEntrie);
                        e.onNext(bl);

                    }
                }).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        dissmissDialog();
                        if (o) {
                            collapsingToolbarLayout.setTitle(nickName);
                        }else{
                            Toast.makeText(UserInfoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }).setNegativeButton("取消", null).show();
    }
    @OnClick(R.id.sendMsgBtn)
    public void toChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userInfo", userInfo);
        startActivity(intent);
    }

    @OnClick(R.id.sex)
    public void changeSex() {

        new AlertDialog.Builder(this).setTitle("性别").setSingleChoiceItems(new String[]{"男", "女"}, "男".equals(sex.getText().toString()) ? 0 : 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final String sexStr = which==0?"男":"女";
                showDilog("正在修改性别");
                getDefaultObservable(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(ObservableEmitter e) throws Exception {
                        UserInfoEntrie userInfoEntrie = new UserInfoEntrie();
                        userInfoEntrie.setUserName(currentUser);
                        userInfoEntrie.setSex(sexStr);
                        boolean bl =  manager.getClient().saveUserInfo(userInfoEntrie);
                        e.onNext(bl);

                    }
                }).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        dissmissDialog();
                        if (o) {
                            sex.setText(sexStr);
                        }else{
                            Toast.makeText(UserInfoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).show();
    }

    public void changeAvator(final String file) {
        showDilog("正在修改头像");
        getDefaultObservable(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                XmppManager manager = XmppManager.getXmppManager();
                boolean bl = manager.getClient().changeAvator(FileUtil.fileToByte(file));
                e.onNext(bl);
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean o) throws Exception {
                dissmissDialog();
                if (o) {
                    Glide.with(UserInfoActivity.this).load(new File(file)).into(backdrop);
                } else {
                    Toast.makeText(UserInfoActivity.this, "图像修改失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
