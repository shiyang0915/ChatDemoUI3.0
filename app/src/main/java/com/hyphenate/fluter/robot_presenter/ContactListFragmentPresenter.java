package com.hyphenate.fluter.robot_presenter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.impl.ZszlFriendsContactChangeListener;
import com.hyphenate.fluter.robot_utils.OkHttpRequestUtils;
import com.hyphenate.fluter.robot_utils.RequestCommand;
import com.hyphenate.fluter.robot_utils.Utils;
import com.hyphenate.fluter.ui.ContactListFragment;
import com.hyphenate.fluter.ui.MainActivity;
import com.hyphenate.easeui.domain.MyFriends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hyphenate.easeui.utils.EaseCommonUtils.saveContactMyFriend;


public class ContactListFragmentPresenter implements
        ViewPager.OnPageChangeListener {

    private ContactListFragment contactListFragment;
    private ViewPager viewPager;
    private View view;
    private TabLayout mTabLayout;
    private List<Fragment> mFragmentLists;
    private PagerAdapter mAdater;
    private ViewHolder holder;
    private List<TabLayout.Tab> tabs = new ArrayList<>();

    public void setZszlFriendsContactChangeListener(ZszlFriendsContactChangeListener zszlFriendsContactChangeListener) {
        this.zszlFriendsContactChangeListener = zszlFriendsContactChangeListener;
    }

    private ZszlFriendsContactChangeListener zszlFriendsContactChangeListener;


    /**
     * 请求回调
     */
    private RequestCommand.RequestListener requestListener = new RequestCommand.RequestListener() {
        @Override
        public void onResponse(final Object o) {
            contactListFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final MyFriends myFriends = (MyFriends) o;
                    DemoApplication.getInstance().myFriends = myFriends;
                    saveContactMyFriend(contactListFragment.getContext(), myFriends);
                    ((MainActivity) contactListFragment.getActivity()).cancelDialog();
                    zszlFriendsContactChangeListener.contactChange();

//                    MyFriends.DataBean.RobotBean robotBeannew = new MyFriends.DataBean.RobotBean();
//                    robotBeannew.setFriend_name("hahaha");
//                    robotBeannew.setAvatar("");
//
//                    MyFriends.DataBean.RobotBean robotBeannew1 = new MyFriends.DataBean.RobotBean();
//                    robotBeannew1.setFriend_name("shiyang");
//                    robotBeannew1.setAvatar("");
//
//                    MyFriends.DataBean.RobotBean robotBeannew6 = new MyFriends.DataBean.RobotBean();
//                    robotBeannew6.setFriend_name("施洋测试");
//                    robotBeannew6.setAvatar("");
//
//                    MyFriends.DataBean.RobotBean robotBeannew7 = new MyFriends.DataBean.RobotBean();
//                    robotBeannew7.setFriend_name("斯柯达");
//                    robotBeannew7.setAvatar("");
//
//                    MyFriends.DataBean.RobotBean robotBeannew2 = new MyFriends.DataBean.RobotBean();
//                    robotBeannew2.setFriend_name("test");
//                    robotBeannew2.setAvatar("");
//
//                    MyFriends.DataBean.RobotBean robotBeannew3 = new MyFriends.DataBean.RobotBean();
//                    robotBeannew3.setFriend_name("262673839498");
//                    robotBeannew3.setAvatar("");
//
//                    MyFriends.DataBean.RobotBean robotBeannew4 = new MyFriends.DataBean.RobotBean();
//                    robotBeannew4.setFriend_name("美国人");
//                    robotBeannew4.setAvatar("");
//
//                    MyFriends.DataBean.RobotBean robotBeannew5 = new MyFriends.DataBean.RobotBean();
//                    robotBeannew5.setFriend_name("日本");
//                    robotBeannew5.setAvatar("");
//
//                    robotBeans.add(robotBeannew);
//                    robotBeans.add(robotBeannew1);
//                    robotBeans.add(robotBeannew2);
//                    robotBeans.add(robotBeannew3);
//                    robotBeans.add(robotBeannew4);
//                    robotBeans.add(robotBeannew5);
//                    robotBeans.add(robotBeannew6);
//                    robotBeans.add(robotBeannew7);
//
//                    DemoApplication.getInstance().myFriends.getData().setRobot(robotBeans);


//                    Log.i("SHIYANGTEST", userBeanList.size() + " " + userBeanList.get(0).getFriend_name());
//                    Log.i("SHIYANGTEST", robotBeans.size() + " " + robotBeans.get(0).getFriend_name());
                }
            });

        }

        @Override
        public void failure(String message) {
        }
    };


    public ContactListFragmentPresenter(ContactListFragment contactListFragment) {
        this.contactListFragment = contactListFragment;
        this.view = contactListFragment.view;
        init();
    }

    private void init() {
        mTabLayout = (TabLayout) view.findViewById(com.hyphenate.easeui.R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewpager_contact);
        mFragmentLists = new ArrayList<>();

        mFragmentLists.add(contactListFragment.robotContactFragment);
        mFragmentLists.add(contactListFragment.ownerContactFragment);
        mAdater = new ViewPagerAdapter(contactListFragment.getActivity().getSupportFragmentManager());
        viewPager.setAdapter(mAdater);
        viewPager.addOnPageChangeListener(this);
        initTabView();
    }


    public void getContactList() {
        ((MainActivity) contactListFragment.getActivity()).showDialog();
        Map<String, String> map = new HashMap<>();
        map.put("owner", DemoApplication.getInstance().userInfor.getData().getUsername());
        map.put("zsm", "butler");
        map.put("zsc", "huanxin_user_friend");
        map.put("zsa", "get_friend");
        map.put("zsv", "v1");
        map.put("timestamp", Utils.getTime());
        map.put("login_user_id", DemoApplication.getInstance().userInfor.getData().getId());
        map.put("platform", "android");
        map.put("app_type", "butler");
        map.put("sign", RequestCommand.getSign(map));
        OkHttpRequestUtils.sendPostRequest(RequestCommand.ALL_COMMON, map, requestListener, MyFriends.class);
    }


    private void initTabView() {
        holder = null;
        TabLayout.Tab tab1 = mTabLayout.newTab().setText("机器人");
        TabLayout.Tab tab2 = mTabLayout.newTab().setText("业主");
        tabs.add(tab1);
        tabs.add(tab2);
        mTabLayout.addTab(tab1);
        mTabLayout.addTab(tab2, false);
        for (int i = 0; i < tabs.size(); i++) {
            //获取tab
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            //给tab设置自定义布局
            tab.setCustomView(com.hyphenate.easeui.R.layout.indicator_text);
            holder = new ViewHolder(tab.getCustomView());
            //填充数据
            holder.mTabItemName.setText(tabs.get(i).getText());
            //默认选择第一项
            if (i == 0) {
                holder.mTabItemName.setSelected(true);
                holder.mTabItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        contactListFragment.getResources().getDimensionPixelSize(com.hyphenate.easeui.R.dimen.x32));
                TextPaint tp = holder.mTabItemName.getPaint();
                tp.setFakeBoldText(true);
            } else {
                holder.mTabItemName.setSelected(false);
                holder.mTabItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        contactListFragment.getResources().getDimensionPixelSize(com.hyphenate.easeui.R.dimen.x28));
            }
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                holder = new ViewHolder(tab.getCustomView());
                holder.mTabItemName.setSelected(true);
                //设置选中后的字体大小
                holder.mTabItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        contactListFragment.getResources().getDimensionPixelSize(com.hyphenate.easeui.R.dimen.x32));
                TextPaint tp = holder.mTabItemName.getPaint();
                tp.setFakeBoldText(true);
                //关联Viewpager
//                mViewPager.setCurrentItem(tab.getPosition());
//                tab.getPosition()
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                holder = new ViewHolder(tab.getCustomView());
                holder.mTabItemName.setSelected(false);
                //恢复默认字体大小
                holder.mTabItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        contactListFragment.getResources().getDimensionPixelSize(com.hyphenate.easeui.R.dimen.x28));
                TextPaint tp = holder.mTabItemName.getPaint();
                tp.setFakeBoldText(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewHolder {
        TextView mTabItemName;

        ViewHolder(View tabView) {
            mTabItemName = (TextView) tabView.findViewById(com.hyphenate.easeui.R.id.tv_tab_indicator);
        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //获取Fragment
        @Override
        public Fragment getItem(int position) {
            return mFragmentLists.get(position);
        }

        //总共有mFragmentLists.size()个需要显示
        @Override
        public int getCount() {
            return mFragmentLists.size();
        }
    }
}
