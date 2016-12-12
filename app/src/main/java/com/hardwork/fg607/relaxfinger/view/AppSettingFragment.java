package com.hardwork.fg607.relaxfinger.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.adapter.AppAdapter;
import com.hardwork.fg607.relaxfinger.adapter.MyPagerAdapter;
import com.hardwork.fg607.relaxfinger.adapter.ShortcutAdapter;
import com.hardwork.fg607.relaxfinger.adapter.ToolAdapter;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.model.ShortcutInfo;
import com.hardwork.fg607.relaxfinger.model.ToolInfo;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.MENU_A;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.MENU_B;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.MENU_C;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.MENU_D;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.MENU_E;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.TYPE_APP;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.TYPE_DEFAULT;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.TYPE_FOLDER;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.TYPE_SHORTCUT;
import static com.hardwork.fg607.relaxfinger.view.MenuViewProxy.TYPE_SWITCH_BUTTON;


public class AppSettingFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.app1_layout)
    RelativeLayout mLayout1;
    @BindView(R.id.app2_layout)
    RelativeLayout mLayout2;
    @BindView(R.id.app3_layout)
    RelativeLayout mLayout3;
    @BindView(R.id.app4_layout)
    RelativeLayout mLayout4;
    @BindView(R.id.app5_layout)
    RelativeLayout mLayout5;
    @BindView(R.id.app1_name)
    TextView mAppTextView1;
    @BindView(R.id.app2_name)
    TextView mAppTextView2;
    @BindView(R.id.app3_name)
    TextView mAppTextView3;
    @BindView(R.id.app4_name)
    TextView mAppTextView4;
    @BindView(R.id.app5_name)
    TextView mAppTextView5;
    @BindView(R.id.icon_app1)
    ImageView mAppIcon1;
    @BindView(R.id.icon_app2)
    ImageView mAppIcon2;
    @BindView(R.id.icon_app3)
    ImageView mAppIcon3;
    @BindView(R.id.icon_app4)
    ImageView mAppIcon4;
    @BindView(R.id.icon_app5)
    ImageView mAppIcon5;
    private TextView mCurrentTextView;
    private ImageView mCurrentIcon;
    private FunctionDialog mFuncDialog;

    private HashMap<String, MenuDataSugar> menu1Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String, MenuDataSugar> menu2Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String, MenuDataSugar> menu3Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String, MenuDataSugar> menu4Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String, MenuDataSugar> menu5Map = new HashMap<String, MenuDataSugar>();

    static String mCurrentMenu;
    static HashMap<String, MenuDataSugar> currentMenuMap = null;
    static ArrayList<AppInfo> appList = null;
    static ArrayList<ToolInfo> toolList = null;
    static ArrayList<ShortcutInfo> shortcutList = null;


    public AppSettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_app_setting, container, false);

        ButterKnife.bind(this, fragmentView);

        initMenuData();

        intView();

        return fragmentView;
    }

    private void initMenuData() {

        List<MenuDataSugar> menuDataList = MenuDataSugar.listAll(MenuDataSugar.class);

        for (MenuDataSugar menuData : menuDataList) {

            switch (menuData.getWhichMenu()) {

                case MENU_A:
                    menu1Map.put(menuData.getAction(), menuData);
                    break;
                case MENU_B:
                    menu2Map.put(menuData.getAction(), menuData);
                    break;
                case MENU_C:
                    menu3Map.put(menuData.getAction(), menuData);
                    break;
                case MENU_D:
                    menu4Map.put(menuData.getAction(), menuData);
                    break;
                case MENU_E:
                    menu5Map.put(menuData.getAction(), menuData);
                    break;
                default:
                    break;
            }
        }

    }

    private void intView() {

        mLayout1.setOnClickListener(this);
        mLayout2.setOnClickListener(this);
        mLayout3.setOnClickListener(this);
        mLayout4.setOnClickListener(this);
        mLayout5.setOnClickListener(this);

        generateMenu(menu1Map, mAppTextView1, mAppIcon1);
        generateMenu(menu2Map, mAppTextView2, mAppIcon2);
        generateMenu(menu3Map, mAppTextView3, mAppIcon3);
        generateMenu(menu4Map, mAppTextView4, mAppIcon4);
        generateMenu(menu5Map, mAppTextView5, mAppIcon5);

    }

    /**
     * 初始化快捷菜单设置
     *
     * @param map
     * @param textView
     * @param imageView
     */
    private void generateMenu(HashMap<String, MenuDataSugar> map, TextView textView, ImageView imageView) {

        if (map.size() == 0) {//没有内容

            textView.setText("");

            imageView.setBackground(null);
            imageView.setImageDrawable(null);

        } else if (map.size() == 1) {//仅有一个项目

            MenuDataSugar dataSugar = map.get(map.keySet().iterator().next());

            textView.setText(dataSugar.getName());

            int type = dataSugar.getType();

            switch (type) {

                case 0:
                    imageView.setBackground(null);
                    imageView.setImageDrawable(AppUtils.getAppIcon(dataSugar.getAction()));
                    break;
                case 1:
                    imageView.setBackgroundResource(R.drawable.path_blue_oval);
                    imageView.setImageDrawable(FloatingBallUtils.getSwitcherIcon(dataSugar.getName()));
                    break;
                case 2:
                    imageView.setBackground(null);
                    imageView.setImageDrawable(AppUtils.getShortcutIcon(dataSugar.getName()));
                    break;
                default:
                    break;
            }

        } else if (map.size() > 1) {//有多个项目,生成快捷文件夹

            textView.setText("快捷文件夹");

            ArrayList<Bitmap> list = new ArrayList<>();

            for (String key : map.keySet()) {

                MenuDataSugar dataSugar = map.get(key);

                int type = dataSugar.getType();

                Drawable drawable = null;

                switch (type) {

                    case TYPE_APP:
                        drawable = AppUtils.getAppIcon(dataSugar.getAction());
                        break;
                    case TYPE_SWITCH_BUTTON:
                        drawable = FloatingBallUtils.getSwitcherIcon(dataSugar.getName());
                        break;
                    case TYPE_SHORTCUT:
                        drawable = AppUtils.getShortcutIcon(dataSugar.getName());
                        break;
                    default:
                        break;
                }

                if (drawable != null) {

                    list.add(ImageUtils.drawable2Bitmap(drawable));
                }

                if (list.size() >= 9) {

                    break;
                }
            }

            Bitmap bi = FloatingBallUtils.createCombinationImage(list);

            imageView.setBackground(new ColorDrawable(this.getResources().getColor(R.color.folder)));
            imageView.setImageBitmap(bi);

        }
    }

    private void updateCurrentMenu() {


        generateMenu(currentMenuMap, mCurrentTextView, mCurrentIcon);

        MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where WHICH_MENU='" + mCurrentMenu + "'");

        for (String key : currentMenuMap.keySet()) {

            MenuDataSugar sugar = currentMenuMap.get(key);

            sugar.save();
        }

        sendMsg(Config.UPDATE_APP, "which", mCurrentMenu);
    }

    private void initDialog() {

        mFuncDialog = FunctionDialog.newInstance();

        mFuncDialog.setOperateFinishListener(new OnOperateFinishListener() {

            @Override
            public void onOperateFinish() {

                updateCurrentMenu();

            }
        });

    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.app1_layout:
                mCurrentTextView = mAppTextView1;
                mCurrentIcon = mAppIcon1;
                mCurrentMenu = MENU_A;
                currentMenuMap = menu1Map;
                break;
            case R.id.app2_layout:
                mCurrentTextView = mAppTextView2;
                mCurrentIcon = mAppIcon2;
                mCurrentMenu = MENU_B;
                currentMenuMap = menu2Map;
                break;
            case R.id.app3_layout:
                mCurrentTextView = mAppTextView3;
                mCurrentIcon = mAppIcon3;
                mCurrentMenu = MENU_C;
                currentMenuMap = menu3Map;
                break;
            case R.id.app4_layout:
                mCurrentTextView = mAppTextView4;
                mCurrentIcon = mAppIcon4;
                mCurrentMenu = MENU_D;
                currentMenuMap = menu4Map;
                break;
            case R.id.app5_layout:
                mCurrentTextView = mAppTextView5;
                mCurrentIcon = mAppIcon5;
                mCurrentMenu = MENU_E;
                currentMenuMap = menu5Map;
                break;
            default:
                break;
        }

        popupFunctionDialog(currentMenuMap);

    }


    private void popupFunctionDialog(HashMap<String, MenuDataSugar> currentMenuMap) {


        if (mFuncDialog == null) {

            initDialog();
        }

        FunctionDialog.setCheckedList(currentMenuMap);

        if (mFuncDialog.getDialog() != null) {

            mFuncDialog.getDialog().show();

            mFuncDialog.initMenuChecked();


        } else {

            mFuncDialog.show(getActivity().getFragmentManager(), "dialogFragment");
        }

    }

    public void sendMsg(int what, String name, String msg) {
        Message message = Message.obtain();

        message.what = what;

        Bundle bundle = new Bundle();

        bundle.putString(name, msg);

        message.setData(bundle);


        try {
            if (SettingActivity.sMessenger != null) {

                SettingActivity.sMessenger.send(message);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void hideFuncDialog() {

        if (mFuncDialog != null) {

            mFuncDialog.getDialog().hide();
        }
    }


    public static class FunctionDialog extends DialogFragment {

        private static ArrayList<String> sMenuChoosedList;
        private static int sType = 0;

        @BindView(R.id.viewPager)
        ViewPager mViewPager;
        @BindView(R.id.tabs)
        TabLayout mTabs;

        private View mDilagView;
        private View mAppView;
        private View mButtonView;
        private View mShortcutView;
        private MyPagerAdapter mPagerAdapter;
        private AppAdapter adapter;
        private ToolAdapter mToolAdapter;
        private ShortcutAdapter mShortcutAdapter;
        private OnOperateFinishListener mClickListener;

        private Handler mHandler;

        private static FunctionDialog mInstance = null;

        static FunctionDialog newInstance() {

            if (mInstance == null) {

                FunctionDialog f = new FunctionDialog();

                return f;

            } else {
                return mInstance;
            }

        }

        private static void setCheckedList(HashMap<String, MenuDataSugar> currentMenuMap) {

            if (sMenuChoosedList == null) {

                sMenuChoosedList = new ArrayList<>();
            }

            sType = TYPE_DEFAULT;

            sMenuChoosedList.clear();

            for (String action : currentMenuMap.keySet()) {

                sMenuChoosedList.add(action);
            }

            if (sMenuChoosedList.size() == 1) {

                MenuDataSugar menuDataSugar = currentMenuMap.get((currentMenuMap.keySet().iterator().next()));
                sType = menuDataSugar.getType();
            }
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mHandler = new Handler();

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            if (mDilagView == null) {

                LayoutInflater inflater = getActivity().getLayoutInflater();

                mDilagView = inflater.inflate(R.layout.function_dialog_layout, null);

                ButterKnife.bind(this, mDilagView);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BottomDialog);

            builder.setView(mDilagView);

            Dialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);

            dialog.setTitle("选择快捷功能");

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        getDialog().hide();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                mClickListener.onOperateFinish();
                            }
                        });

                        return true;
                    }

                    return false;
                }
            });

            //调用FunctionDialog.show()后会触发,调用getDialog().hide后
            // dialog的mShowing不会改变,所以再次调用getDialog().show()不会触发showListener.
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mAppView == null) {

                                initAppView();
                                initButtonView();
                                initShotcutView();
                                setupViewPager();
                            }

                            initMenuChecked();
                        }
                    });

                }
            });

            // 设置宽度为屏宽、靠近屏幕底部。
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);

            return dialog;
        }


        private void setOperateFinishListener(OnOperateFinishListener listener) {

            mClickListener = listener;
        }


        public void initMenuChecked() {

            if (sType == TYPE_DEFAULT) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(0);
                }

            } else if (sType == TYPE_APP) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(0);
                }


            } else if (sType == TYPE_SWITCH_BUTTON) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(1);
                }

            } else if (sType == TYPE_SHORTCUT) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(2);
                }
            }

            if (adapter != null) {

                adapter.setAppChecked(sMenuChoosedList);
                adapter.notifyDataSetChanged();
            }

            if (mToolAdapter != null) {

                mToolAdapter.setToolChecked(sMenuChoosedList);
                mToolAdapter.notifyDataSetChanged();
            }

            if (mShortcutAdapter != null) {

                mShortcutAdapter.setShortcutChecked(sMenuChoosedList);
                mShortcutAdapter.notifyDataSetChanged();
            }


        }


        private void initShotcutView() {

            new AsyncTask<Void, Void, Void>() {

                ListView listView;
                ProgressBar loading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    mShortcutView = View.inflate(getActivity(), R.layout.activity_choose_app, null);

                    listView = (ListView) mShortcutView.findViewById(R.id.lv_app);

                    loading = (ProgressBar) mShortcutView.findViewById(R.id.loading);

                    mShortcutAdapter = new ShortcutAdapter(getActivity());
                    mShortcutAdapter.setShortcutChecked(sMenuChoosedList);
                }

                @Override
                protected Void doInBackground(Void... voids) {


                    try {

                        shortcutList = AppUtils.getShortcuts();

                    } catch (SecurityException e) {

                        e.printStackTrace();


                    } catch (SQLiteException e) {

                        e.printStackTrace();

                        shortcutList = new ArrayList<ShortcutInfo>();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    mShortcutAdapter.addList(shortcutList);

                    listView.setAdapter(mShortcutAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                            checkBox.setChecked(!checkBox.isChecked());

                            TextView textView = (TextView) view.findViewById(R.id.text);

                            String name = textView.getText().toString();

                            String action = shortcutList.get(i).getShortcutIntent();

                            if (checkBox.isChecked()) {

                                MenuDataSugar data = new MenuDataSugar(mCurrentMenu,
                                        name, 2, action);

                                currentMenuMap.put(action, data);
                                sMenuChoosedList.add(action);

                            } else {

                                currentMenuMap.remove(action);
                                sMenuChoosedList.remove(action);
                            }

                            mShortcutAdapter.setShortcutChecked(sMenuChoosedList);

                        }
                    });

                    loading.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }

            }.execute();

        }

        private void initButtonView() {

            new AsyncTask<Void, Void, Void>() {

                ListView listView;
                ProgressBar loading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    mButtonView = View.inflate(getActivity(), R.layout.activity_choose_app, null);

                    listView = (ListView) mButtonView.findViewById(R.id.lv_app);

                    loading = (ProgressBar) mButtonView.findViewById(R.id.loading);

                    mToolAdapter = new ToolAdapter(getActivity());
                    mToolAdapter.setToolChecked(sMenuChoosedList);

                }

                @Override
                protected Void doInBackground(Void... voids) {

                    toolList = FloatingBallUtils.getToolInfos();

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    mToolAdapter.addList(toolList);

                    listView.setAdapter(mToolAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                            checkBox.setChecked(!checkBox.isChecked());

                            TextView textView = (TextView) view.findViewById(R.id.text);

                            String name = textView.getText().toString();

                            if (checkBox.isChecked()) {

                                MenuDataSugar data = new MenuDataSugar(mCurrentMenu,
                                        name, 1, name);

                                currentMenuMap.put(name, data);
                                sMenuChoosedList.add(name);
                            } else {

                                currentMenuMap.remove(name);
                                sMenuChoosedList.remove(name);
                            }

                            mToolAdapter.setToolChecked(sMenuChoosedList);


                        }
                    });

                    loading.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }.execute();


        }


        private void initAppView() {

            new AsyncTask<Void, Void, Void>() {

                ListView listView;
                ProgressBar loading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    mAppView = View.inflate(getActivity(), R.layout.activity_choose_app, null);

                    listView = (ListView) mAppView.findViewById(R.id.lv_app);

                    loading = (ProgressBar) mAppView.findViewById(R.id.loading);


                    adapter = new AppAdapter(getActivity());
                    adapter.setAppChecked(sMenuChoosedList);
                }

                @Override
                protected Void doInBackground(Void... voids) {

                    appList = AppUtils.getLauncherAppInfos();

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    adapter.addList(appList);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                            checkBox.setChecked(!checkBox.isChecked());

                            TextView textView = (TextView) view.findViewById(R.id.text);

                            String name = textView.getText().toString();

                            String action = appList.get(i).getAppPackage();

                            if (checkBox.isChecked()) {

                                MenuDataSugar data = new MenuDataSugar(mCurrentMenu,
                                        name, 0, action);

                                currentMenuMap.put(action, data);

                                sMenuChoosedList.add(action);

                            } else {

                                currentMenuMap.remove(action);

                                sMenuChoosedList.remove(action);
                            }

                            adapter.setAppChecked(sMenuChoosedList);

                        }
                    });

                    loading.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                }
            }.execute();


        }


        public void setupViewPager() {

            mPagerAdapter = new MyPagerAdapter();

            if (mAppView != null) {

                mPagerAdapter.addView(mAppView, "应用程序");
                mPagerAdapter.addView(mButtonView, "快捷开关");
                mPagerAdapter.addView(mShortcutView, "快捷方式");
            }


            mViewPager.setAdapter(mPagerAdapter);

            mTabs.setupWithViewPager(mViewPager);

            mViewPager.setOffscreenPageLimit(3);

            switch (sType) {

                case TYPE_APP:
                    mViewPager.setCurrentItem(0);
                    break;
                case TYPE_SWITCH_BUTTON:
                    mViewPager.setCurrentItem(1);
                    break;
                case TYPE_SHORTCUT:
                    mViewPager.setCurrentItem(2);
                    break;
                default:
                    break;
            }

        }


    }

    public interface OnOperateFinishListener {

        public void onOperateFinish();

    }

}
