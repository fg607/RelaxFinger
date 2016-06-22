package com.hardwork.fg607.relaxfinger.view;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.BaseInputConnection;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.adapter.AppAdapter;
import com.hardwork.fg607.relaxfinger.adapter.MyPagerAdapter;
import com.hardwork.fg607.relaxfinger.adapter.ShortcutAdapter;
import com.hardwork.fg607.relaxfinger.adapter.ToolAdapter;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.model.ShortcutInfo;
import com.hardwork.fg607.relaxfinger.model.ToolInfo;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;
import com.orm.SugarRecord;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppSettingFragment extends Fragment implements View.OnClickListener{

    @Bind(R.id.app1_layout) RelativeLayout mLayout1;
    @Bind(R.id.app2_layout) RelativeLayout mLayout2;
    @Bind(R.id.app3_layout) RelativeLayout mLayout3;
    @Bind(R.id.app4_layout) RelativeLayout mLayout4;
    @Bind(R.id.app5_layout) RelativeLayout mLayout5;
    @Bind(R.id.app1_name) TextView mAppTextView1;
    @Bind(R.id.app2_name) TextView mAppTextView2;
    @Bind(R.id.app3_name) TextView mAppTextView3;
    @Bind(R.id.app4_name) TextView mAppTextView4;
    @Bind(R.id.app5_name) TextView mAppTextView5;
    @Bind(R.id.icon_app1) ImageView mAppIcon1;
    @Bind(R.id.icon_app2) ImageView mAppIcon2;
    @Bind(R.id.icon_app3) ImageView mAppIcon3;
    @Bind(R.id.icon_app4) ImageView mAppIcon4;
    @Bind(R.id.icon_app5) ImageView mAppIcon5;
    private String mAppName;
    private TextView mCurrentTextView;
    private ImageView mCurrentIcon;
    private TrayAppPreferences mPreferences;
    static String mCurrentApp;
    private FunctionDialog mFuncDialog;
    private Activity mActivity;

    private HashMap<String,Object> menu1Map = new HashMap<String, Object>();
    private HashMap<String,Object> menu2Map = new HashMap<String, Object>();
    private HashMap<String,Object> menu3Map = new HashMap<String, Object>();
    private HashMap<String,Object> menu4Map = new HashMap<String, Object>();
    private HashMap<String,Object> menu5Map = new HashMap<String, Object>();
    private ArrayList<String> mChoosedList = new ArrayList<>();
    static HashMap<String,Object> currentMenuMap = null;

    static ArrayList<AppInfo> appList = null;
    static ArrayList<ToolInfo> toolList = null;
    static ArrayList<ShortcutInfo> shortcutList = null;



    public AppSettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_app_setting, container, false);

        ButterKnife.bind(this,fragmentView);
        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        //initDialog();
        initMenuData();

        intView();

        return fragmentView;
    }

    private void initMenuData() {

        List<MenuDataSugar>  menuDataList = MenuDataSugar.listAll(MenuDataSugar.class);

        for(MenuDataSugar menuData:menuDataList){

            switch (menuData.getWhichMenu()){

                case "1":
                    menu1Map.put(menuData.getAction(),menuData);
                    break;
                case "2":
                    menu2Map.put(menuData.getAction(),menuData);
                    break;
                case "3":
                    menu3Map.put(menuData.getAction(),menuData);
                    break;
                case "4":
                    menu4Map.put(menuData.getAction(),menuData);
                    break;
                case "5":
                    menu5Map.put(menuData.getAction(),menuData);
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

        generateMenu(menu1Map,mAppTextView1,mAppIcon1);
        generateMenu(menu2Map,mAppTextView2,mAppIcon2);
        generateMenu(menu3Map,mAppTextView3,mAppIcon3);
        generateMenu(menu4Map,mAppTextView4,mAppIcon4);
        generateMenu(menu5Map,mAppTextView5,mAppIcon5);

       /* String name;
        int type;

        name = mPreferences.getString("app1","");
        type = mPreferences.getInt("type1",0);
        if(name != ""){

            if(type==0){
                mAppTextView1.setText(AppUtils.getAppName(name));
                mAppIcon1.setBackground(null);
                mAppIcon1.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView1.setText(name);
                mAppIcon1.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon1.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }else if(type==2){
                Drawable drawable = AppUtils.getShortcutIcon(name);

                if(drawable!=null){

                    mAppTextView1.setText(name);

                }else {

                    mAppTextView1.setText("");

                    mPreferences.put("app1","");

                    sendMsg(Config.UPDATE_APP, "which", "1");
                }

                mAppIcon1.setBackground(null);
                mAppIcon1.setImageDrawable(drawable);

            }


        }

        name = mPreferences.getString("app2","");
        type = mPreferences.getInt("type2",0);

        if(name != ""){

            if(type==0){
                mAppTextView2.setText(AppUtils.getAppName(name));
                mAppIcon2.setBackground(null);
                mAppIcon2.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView2.setText(name);
                mAppIcon2.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon2.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }else if(type==2){
                Drawable drawable = AppUtils.getShortcutIcon(name);

                if(drawable!=null){

                    mAppTextView2.setText(name);

                }else {

                    mAppTextView2.setText("");

                    mPreferences.put("app2","");

                    sendMsg(Config.UPDATE_APP, "which", "2");
                }
                mAppIcon2.setBackground(null);
                mAppIcon2.setImageDrawable(AppUtils.getShortcutIcon(name));
            }
        }


        name = mPreferences.getString("app3","");
        type = mPreferences.getInt("type3",0);

        if(name != ""){

            if(type==0){
                mAppTextView3.setText(AppUtils.getAppName(name));
                mAppIcon3.setBackground(null);
                mAppIcon3.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView3.setText(name);
                mAppIcon3.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon3.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }else if(type==2){
                Drawable drawable = AppUtils.getShortcutIcon(name);

                if(drawable!=null){

                    mAppTextView3.setText(name);

                }else {

                    mAppTextView3.setText("");

                    mPreferences.put("app3","");

                    sendMsg(Config.UPDATE_APP, "which", "3");
                }
                mAppIcon3.setBackground(null);
                mAppIcon3.setImageDrawable(AppUtils.getShortcutIcon(name));
            }
        }

        name = mPreferences.getString("app4","");
        type = mPreferences.getInt("type4",0);

        if(name != ""){

            if(type==0){
                mAppTextView4.setText(AppUtils.getAppName(name));
                mAppIcon4.setBackground(null);
                mAppIcon4.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView4.setText(name);
                mAppIcon4.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon4.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }else if(type==2){
                Drawable drawable = AppUtils.getShortcutIcon(name);

                if(drawable!=null){

                    mAppTextView4.setText(name);

                }else {

                    mAppTextView4.setText("");

                    mPreferences.put("app4","");

                    sendMsg(Config.UPDATE_APP, "which", "4");
                }
                mAppIcon4.setBackground(null);
                mAppIcon4.setImageDrawable(AppUtils.getShortcutIcon(name));
            }
        }

        name = mPreferences.getString("app5","");
        type = mPreferences.getInt("type5",0);

        if(name != ""){

            if(type==0){
                mAppTextView5.setText(AppUtils.getAppName(name));
                mAppIcon5.setBackground(null);
                mAppIcon5.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView5.setText(name);
                mAppIcon5.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon5.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }else if(type==2){
                Drawable drawable = AppUtils.getShortcutIcon(name);

                if(drawable!=null){

                    mAppTextView5.setText(name);

                }else {

                    mAppTextView5.setText("");

                    mPreferences.put("app5","");

                    sendMsg(Config.UPDATE_APP, "which", "5");


                }
                mAppIcon5.setBackground(null);
                mAppIcon5.setImageDrawable(AppUtils.getShortcutIcon(name));
            }
        }*/


    }

    private void generateMenu(HashMap<String,Object> map,TextView textView,ImageView imageView){

        if(map.size()==0){

            textView.setText("");

            imageView.setBackground(null);
            imageView.setImageDrawable(null);

        }else if(map.size()==1){

            MenuDataSugar dataSugar = (MenuDataSugar) map.get(map.keySet().iterator().next());

            textView.setText(dataSugar.getName());

            int type = dataSugar.getType();

            switch (type){

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

        }else if(map.size()>1){

            textView.setText("快捷文件夹");

            ArrayList<Bitmap> list = new ArrayList<Bitmap>();

            for(String key:map.keySet()) {

                MenuDataSugar dataSugar = (MenuDataSugar) map.get(key);

                int type = dataSugar.getType();

                Drawable drawable = null;

                switch (type){

                    case 0:
                        drawable = AppUtils.getAppIcon(dataSugar.getAction());
                        break;
                    case 1:
                        drawable = FloatingBallUtils.getSwitcherIcon(dataSugar.getName());
                        break;
                    case 2:
                        drawable = AppUtils.getShortcutIcon(dataSugar.getName());
                        break;
                    default:
                        break;
                }

                if(drawable!=null){

                    list.add(ImageUtils.drawable2Bitmap(drawable));
                }

                if(list.size()>=9){

                    break;
                }
            }

            Bitmap bi = FloatingBallUtils.createCombinationImage(list);

            imageView.setBackground(new ColorDrawable(this.getResources().getColor(R.color.folder)));
            imageView.setImageBitmap(bi);

        }
    }

    private void generateMenu() {


        generateMenu(currentMenuMap,mCurrentTextView,mCurrentIcon);

       // byte[] menuData = FloatingBallUtils.serialize(currentMenuMap);

        MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where WHICH_MENU='" + mCurrentApp+"'");

        for(String key :currentMenuMap.keySet()){

            MenuDataSugar sugar = (MenuDataSugar) currentMenuMap.get(key);

            sugar.save();
        }

        sendMsg(Config.UPDATE_APP, "which", mCurrentApp);



       /* MenuDataSugar sugar = new MenuDataSugar(mCurrentApp,menuData);

        List<MenuDataSugar> list = MenuDataSugar.findWithQuery(MenuDataSugar.class,"select * from MENU_DATA_SUGAR" +
                " where WHICH_MENU='"+mCurrentApp+"'");

        if(list.size()>=1){

            sugar.update();

        }else {

            sugar.save();
        }*/





    }

    private void initDialog(ArrayList<String> choosedMenuList) {

        mFuncDialog = FunctionDialog.newInstance(choosedMenuList);

        mFuncDialog.setDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClick(final Intent intent) {

                generateMenu();

               /* if (intent != null) {

                    if (!intent.getStringExtra("name").equals("")) {

                        mCurrentTextView.setText(intent.getStringExtra("name"));
                        if (intent.getIntExtra("type", 0) == 1) {
                            mCurrentIcon.setBackgroundResource(R.drawable.path_blue_oval);


                        } else {
                            mCurrentIcon.setBackground(null);
                        }

                        mCurrentIcon.setImageDrawable(ImageUtils.Bytes2Drawable(intent.getByteArrayExtra("icon")));
                        if (intent.getIntExtra("type", 0) == 2) {
                            mPreferences.put("app" + mCurrentApp, intent.getStringExtra("name"));
                            mPreferences.put("shortcutIntent" + mCurrentApp, intent.getStringExtra("package"));
                        } else {
                            mPreferences.put("app" + mCurrentApp, intent.getStringExtra("package"));
                        }


                    } else {

                        mCurrentTextView.setText(intent.getStringExtra("name"));
                        mCurrentIcon.setBackground(null);
                        mCurrentIcon.setImageDrawable(null);
                        mPreferences.put("app" + mCurrentApp, "");

                    }

                    mPreferences.put("type" + mCurrentApp, intent.getIntExtra("type", 0));

                    sendMsg(Config.UPDATE_APP, "which", mCurrentApp);

                }else {

                    generateMenu();
                }*/


            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.app1_layout:
                mAppName = mAppTextView1.getText().toString();
                mCurrentTextView = mAppTextView1;
                mCurrentIcon = mAppIcon1;
                mCurrentApp = "1";
                currentMenuMap = menu1Map;
                break;
            case R.id.app2_layout:
                mAppName = mAppTextView2.getText().toString();
                mCurrentTextView = mAppTextView2;
                mCurrentIcon = mAppIcon2;
                mCurrentApp = "2";
                currentMenuMap = menu2Map;
                break;
            case R.id.app3_layout:
                mAppName = mAppTextView3.getText().toString();
                mCurrentTextView = mAppTextView3;
                mCurrentIcon = mAppIcon3;
                mCurrentApp = "3";
                currentMenuMap = menu3Map;
                break;
            case R.id.app4_layout:
                mAppName = mAppTextView4.getText().toString();
                mCurrentTextView = mAppTextView4;
                mCurrentIcon = mAppIcon4;
                mCurrentApp = "4";
                currentMenuMap = menu4Map;
                break;
            case R.id.app5_layout:
                mAppName = mAppTextView5.getText().toString();
                mCurrentTextView = mAppTextView5;
                mCurrentIcon = mAppIcon5;
                mCurrentApp = "5";
                currentMenuMap = menu5Map;
                break;
            default:
                break;
        }

        int type = -1;

        mChoosedList.clear();

        for(String action:currentMenuMap.keySet()){

            mChoosedList.add(action);
        }

        if(currentMenuMap.size()==1){

            MenuDataSugar menuDataSugar = (MenuDataSugar) currentMenuMap.get((currentMenuMap.keySet().iterator().next()));
            type = menuDataSugar.getType();
        }


        popupFunctionDialog(type,mChoosedList);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }


    public void popupFunctionDialog(final int type, final ArrayList<String> choosedMenuList) {


        if (mFuncDialog == null) {

            initDialog(choosedMenuList);
        }

        mFuncDialog.setCheckedFuncName(type,choosedMenuList);

        if (mFuncDialog.getDialog() != null) {

            mFuncDialog.getDialog().show();

        } else {
            
            mFuncDialog.show(getActivity().getFragmentManager(), "dialogFragment");
        }

    }

    public  void sendMsg(int what,String name,String msg) {
        Intent intent = new Intent();
        intent.putExtra("what", what);
        intent.putExtra(name, msg);
        intent.setClass(MyApplication.getApplication(), FloatingBallService.class);
        MyApplication.getApplication().startService(intent);

    }

    @Override
    public void onPause() {
        DialogFragment dialogFragment = (DialogFragment) getActivity().getFragmentManager().findFragmentByTag("dialogFragment");

        if(dialogFragment!=null){

            getActivity().getFragmentManager().beginTransaction().remove(dialogFragment).commit();
        }
        super.onPause();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Config.CHOOSE_APP_CODE){

            if(data != null){

                if(!data.getStringExtra("name").equals("")){

                    mCurrentTextView.setText(data.getStringExtra("name"));
                    mCurrentIcon.setImageDrawable(ImageUtils.Bytes2Drawable(data.getByteArrayExtra("icon")));
                    mPreferences.put("app" + mCurrentApp, data.getStringExtra("package"));



                }else {

                    mCurrentTextView.setText(data.getStringExtra("name"));
                    mCurrentIcon.setImageDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    mPreferences.put("app" + mCurrentApp,"");
                }

                sendMsg(Config.UPDATE_APP,"which",mCurrentApp);

            }

        }

    }




    public static class FunctionDialog extends DialogFragment{

        @Bind(R.id.viewPager)
        ViewPager mViewPager;
        @Bind(R.id.tabs)
        TabLayout mTabs;

        static   View mAppView = null;
        static   View mButtonView = null;
        static   View mShortcutView = null;

        private  ArrayList<String> mMenuChoosedList;
        private MyPagerAdapter mPagerAdapter;
        private  AppAdapter adapter;
        private  ToolAdapter mToolAdapter;
        private ShortcutAdapter mShortcutAdapter;
        private String mCheckdedFuncName;
        private OnDialogClickListener mClickListener;
        private int mType=0;
        private Handler mHandler;
        private View mDialogView;

        private static FunctionDialog mInstance=null;

        static FunctionDialog newInstance(ArrayList<String> choosedMenuList) {

            if(mInstance==null){

                FunctionDialog f = new FunctionDialog();

                Bundle args = new Bundle();
                args.putStringArrayList("checkedName", choosedMenuList);
                f.setArguments(args);

                return f;

            }else {

                return mInstance;
            }


        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mMenuChoosedList = getArguments().getStringArrayList("checkedName");
           // mCheckdedFuncName = getArguments().getString("checkedName");
            mHandler = new Handler();

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.function_dialog_layout, null);

            ButterKnife.bind(this, view);

            initAppView();

            initButtonView();
            initShotcutView();

            setupViewPager();

            mDialogView= view;

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.function_dialog_layout, null);

            ButterKnife.bind(this, view);

            initAppView();

            initButtonView();
            initShotcutView();

            setupViewPager();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BottomDialog);

            builder.setView(view);

           Dialog  dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);

            dialog.setTitle("选择快捷功能");

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if(keyCode==KeyEvent.KEYCODE_BACK){

                        getDialog().hide();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                mClickListener.onDialogClick(null);
                            }
                        });

                        return true;
                    }

                    return false;
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


        private void setDialogClickListener(OnDialogClickListener listener){

            mClickListener = listener;
        }

        private void setCheckedFuncName(int type, ArrayList<String> choosedMenuList) {


            mType = type;
            mMenuChoosedList = choosedMenuList;


            if (mType == -1) {

                if (adapter != null) {

                    adapter.setAppChecked(mMenuChoosedList);
                }

                if (mToolAdapter != null) {

                    mToolAdapter.setToolChecked(mMenuChoosedList);
                }

                if (mShortcutAdapter != null) {

                    mShortcutAdapter.setShortcutChecked(mMenuChoosedList);
                }

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(0);
                }

            } else if (mType == 0) {

                if (adapter != null) {

                    adapter.setAppChecked(mMenuChoosedList);
                }


                if (mViewPager != null) {
                    mViewPager.setCurrentItem(0);
                }


            } else if (mType == 1) {


                if (mToolAdapter != null) {

                    mToolAdapter.setToolChecked(mMenuChoosedList);
                }

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(1);
                }

            } else if (mType == 2) {

                if (mShortcutAdapter != null) {

                    mShortcutAdapter.setShortcutChecked(mMenuChoosedList);
                }

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(2);
                }
            }

            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mType==-1){

                        adapter.notifyDataSetChanged();

                        mToolAdapter.notifyDataSetChanged();

                        mShortcutAdapter.notifyDataSetChanged();

                    }else if(mType == 0){

                        adapter.notifyDataSetChanged();

                    }else if (mType == 1){

                        mToolAdapter.notifyDataSetChanged();
                    }else if(mType == 2){

                        mShortcutAdapter.notifyDataSetChanged();
                    }

                }
            },40);

        }



        private void initShotcutView() {

            if(mShortcutView==null){

                mShortcutView = View.inflate(getActivity(),R.layout.activity_choose_app,null);
            }
            final ListView listView = (ListView) mShortcutView.findViewById(R.id.lv_app);

            final ProgressBar loading = (ProgressBar) mShortcutView.findViewById(R.id.loading);

            mShortcutAdapter= new ShortcutAdapter(getActivity());
            mShortcutAdapter.setShortcutChecked(mMenuChoosedList);

            if(shortcutList==null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            shortcutList = AppUtils.getShortcuts();

                        }catch (SecurityException e){

                            e.printStackTrace();

                            shortcutList = new ArrayList<ShortcutInfo>();
                        }


                        mShortcutAdapter.addList(shortcutList);


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                listView.setAdapter(mShortcutAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                                        checkBox.setChecked(!checkBox.isChecked());

                                        TextView textView = (TextView) view.findViewById(R.id.text);

                                        String name = textView.getText().toString();

                                        String action = shortcutList.get(i).getShortcutIntent();

                                        if(checkBox.isChecked()){

                                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                                    name,2,action);

                                            currentMenuMap.put(action,data);

                                            mMenuChoosedList.add(action);
                                            //menuList.add(appList.get(i));
                                        }else {

                                            currentMenuMap.remove(action);
                                            mMenuChoosedList.remove(action);
                                        }

                                        mShortcutAdapter.setShortcutChecked(mMenuChoosedList);


                                        /*Intent intent = new Intent();

                                        String name = shortcutList.get(i).getShortcutTitle();
                                        if (!name.equals(mCheckdedFuncName)) {

                                            mShortcutAdapter.setShortcutChecked(name);
                                            intent.putExtra("name", name);
                                            intent.putExtra("package", shortcutList.get(i).getShortcutIntent());
                                            intent.putExtra("type",2);
                                            intent.putExtra("icon", ImageUtils.Drawable2Bytes(shortcutList.get(i).getShortcutIcon()));
                                        } else {
                                            mShortcutAdapter.setShortcutChecked("");
                                            intent.putExtra("name", "");
                                        }


                                        mClickListener.onDialogClick(intent);

                                        getDialog().hide();*/


                                    }
                                });

                                loading.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        });



                    }
                }).start();


            }else {

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

                        if(checkBox.isChecked()){

                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                    name,2,action);

                            currentMenuMap.put(action,data);
                            mMenuChoosedList.add(action);
                            //menuList.add(appList.get(i));
                        }else {

                            currentMenuMap.remove(action);
                            mMenuChoosedList.remove(action);
                        }

                        mShortcutAdapter.setShortcutChecked(mMenuChoosedList);
                       /* Intent intent = new Intent();

                        String name = shortcutList.get(i).getShortcutTitle();
                        if (!name.equals(mCheckdedFuncName)) {

                            mShortcutAdapter.setShortcutChecked(name);
                            intent.putExtra("name", name);
                            intent.putExtra("package", shortcutList.get(i).getShortcutIntent());
                            intent.putExtra("type",2);
                            intent.putExtra("icon", ImageUtils.Drawable2Bytes(shortcutList.get(i).getShortcutIcon()));
                        } else {
                            mShortcutAdapter.setShortcutChecked("");
                            intent.putExtra("name", "");
                        }


                        mClickListener.onDialogClick(intent);

                        getDialog().hide();*/


                    }
                });

                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }



        }

        private void initButtonView() {

            if(mButtonView==null){

                mButtonView = View.inflate(getActivity(),R.layout.activity_choose_app,null);
            }

            final ListView listView = (ListView) mButtonView.findViewById(R.id.lv_app);

            final ProgressBar loading = (ProgressBar) mButtonView.findViewById(R.id.loading);

            mToolAdapter= new ToolAdapter(getActivity());
            mToolAdapter.setToolChecked(mMenuChoosedList);

            if(toolList==null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        toolList = FloatingBallUtils.getToolInfos();

                        mToolAdapter.addList(toolList);


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                listView.setAdapter(mToolAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                                        checkBox.setChecked(!checkBox.isChecked());

                                        TextView textView = (TextView) view.findViewById(R.id.text);

                                        String name = textView.getText().toString();

                                        if(checkBox.isChecked()){

                                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                                    name,1,name);

                                            currentMenuMap.put(name,data);
                                            mMenuChoosedList.add(name);
                                            //menuList.add(appList.get(i));
                                        }else {

                                            currentMenuMap.remove(name);
                                            mMenuChoosedList.remove(name);
                                        }

                                        mToolAdapter.setToolChecked(mMenuChoosedList);
                                    /*    Intent intent = new Intent();

                                        String name = toolList.get(i).getToolName();

                                        if (!name.equals(mCheckdedFuncName)) {

                                            if("手电筒".equals(name)&&Build.VERSION.SDK_INT>22){

                                                checkPermissionGranted(Manifest.permission.CAMERA);
                                            }


                                            mToolAdapter.setToolChecked(name);
                                            intent.putExtra("name", name);
                                            intent.putExtra("package", name);
                                            intent.putExtra("type",1);
                                            intent.putExtra("icon", ImageUtils.Drawable2Bytes(toolList.get(i).getToolIcon()));
                                        } else {
                                            mToolAdapter.setToolChecked("");
                                            intent.putExtra("name", "");
                                        }


                                        mClickListener.onDialogClick(intent);

                                        getDialog().hide();*/


                                    }
                                });

                                loading.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        });



                    }
                }).start();

            }else {

                mToolAdapter.addList(toolList);

                listView.setAdapter(mToolAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                        checkBox.setChecked(!checkBox.isChecked());

                        TextView textView = (TextView) view.findViewById(R.id.text);

                        String name = textView.getText().toString();

                        if(checkBox.isChecked()){

                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                    name,1,name);

                            currentMenuMap.put(name,data);
                            mMenuChoosedList.add(name);
                            //menuList.add(appList.get(i));
                        }else {

                            currentMenuMap.remove(name);
                            mMenuChoosedList.remove(name);
                        }
                        mToolAdapter.setToolChecked(mMenuChoosedList);
                        /*Intent intent = new Intent();

                        String name = toolList.get(i).getToolName();

                        if (!name.equals(mCheckdedFuncName)) {

                            if("手电筒".equals(name)&&Build.VERSION.SDK_INT>22){

                                checkPermissionGranted(Manifest.permission.CAMERA);
                            }


                            mToolAdapter.setToolChecked(name);
                            intent.putExtra("name", name);
                            intent.putExtra("package", name);
                            intent.putExtra("type",1);
                            intent.putExtra("icon", ImageUtils.Drawable2Bytes(toolList.get(i).getToolIcon()));
                        } else {
                            mToolAdapter.setToolChecked("");
                            intent.putExtra("name", "");
                        }


                        mClickListener.onDialogClick(intent);

                        getDialog().hide();*/


                    }
                });


                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }




        }

        private void checkPermissionGranted(String permission) {


            if(Build.VERSION.SDK_INT>22){

                int grant = getActivity().checkSelfPermission(permission);

                if (grant != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    requestPermissions(new String[]{permission}, 123);
                }
            }

        }


        private void initAppView() {

            if(mAppView==null){

                mAppView = View.inflate(getActivity(),R.layout.activity_choose_app,null);
            }

            final ListView listView = (ListView) mAppView.findViewById(R.id.lv_app);

            final ProgressBar loading = (ProgressBar) mAppView.findViewById(R.id.loading);



            adapter= new AppAdapter(getActivity());
            adapter.setAppChecked(mMenuChoosedList);

            if(appList==null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        appList = AppUtils.getLauncherAppInfos();

                        adapter.addList(appList);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                                        checkBox.setChecked(!checkBox.isChecked());

                                        TextView textView = (TextView) view.findViewById(R.id.text);

                                        String name = textView.getText().toString();

                                        String action = appList.get(i).getAppPackage();

                                        if(checkBox.isChecked()){

                                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                                    name,0,action);

                                            currentMenuMap.put(action,data);

                                            mMenuChoosedList.add(action);


                                            //menuList.add(appList.get(i));
                                        }else {

                                            currentMenuMap.remove(action);

                                            mMenuChoosedList.remove(action);
                                        }

                                        adapter.setAppChecked(mMenuChoosedList);


                                        /*Intent intent = new Intent();

                                        String name = appList.get(i).getAppName();
                                        if (!name.equals(mCheckdedFuncName)) {

                                            adapter.setAppChecked(name);
                                            intent.putExtra("name", name);
                                            intent.putExtra("package", appList.get(i).getAppPackage());
                                            intent.putExtra("type",0);
                                            intent.putExtra("icon", ImageUtils.Drawable2Bytes(appList.get(i).getAppIcon()));
                                        } else {
                                            adapter.setAppChecked("");
                                            intent.putExtra("name", "");
                                        }*/


                                       // mClickListener.onDialogClick(intent);

                                        //getDialog().hide();


                                    }
                                });

                                loading.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        });


                    }
                }).start();



            }else {

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

                        if(checkBox.isChecked()){

                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                    name,0,action);

                            currentMenuMap.put(action,data);
                            mMenuChoosedList.add(action);

                            //menuList.add(appList.get(i));
                        }else {

                            currentMenuMap.remove(action);
                            mMenuChoosedList.remove(action);
                        }

                        adapter.setAppChecked(mMenuChoosedList);

                      /*  Intent intent = new Intent();

                        String name = appList.get(i).getAppName();
                        if (!name.equals(mCheckdedFuncName)) {

                            adapter.setAppChecked(name);
                            intent.putExtra("name", name);
                            intent.putExtra("package", appList.get(i).getAppPackage());
                            intent.putExtra("type",0);
                            intent.putExtra("icon", ImageUtils.Drawable2Bytes(appList.get(i).getAppIcon()));
                        } else {
                            adapter.setAppChecked("");
                            intent.putExtra("name", "");
                        }


                        mClickListener.onDialogClick(intent);*/

                       // getDialog().hide();


                    }
                });

                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

        }


        public void setupViewPager(){

            mPagerAdapter = new MyPagerAdapter();

            if(mAppView!=null){

                mPagerAdapter.addView(mAppView,"应用程序");
                mPagerAdapter.addView(mButtonView,"快捷开关");
                mPagerAdapter.addView(mShortcutView,"快捷方式");
            }


            mViewPager.setAdapter(mPagerAdapter);

            mTabs.setupWithViewPager(mViewPager);

            mViewPager.setOffscreenPageLimit(3);

            switch (mType){

                case 0:
                    mViewPager.setCurrentItem(0);
                    break;
                case 1:
                    mViewPager.setCurrentItem(1);
                    break;
                case 2:
                    mViewPager.setCurrentItem(2);
                    break;
                default:
                    break;
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    switch (position) {
                        case 0:
                            // mFloatingActionButton.hide();
                            break;
                        case 1:
                            // mFloatingActionButton.hide();
                            //mHistoryFragment.refreshHistory();
                            break;
                        case 2:
                            //mFloatingActionButton.show();
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }


    }

    public interface OnDialogClickListener{

        public void onDialogClick(Intent intent);

    }


}
