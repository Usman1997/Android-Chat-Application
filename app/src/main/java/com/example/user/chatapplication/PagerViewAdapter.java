package com.example.user.chatapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;


            case 1:
                FriendsFragment friendsFragent = new FriendsFragment();
                return friendsFragent;


            case 2:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:

                return "CHATS";
            case 1:
                return "FRIENDS";
            case 2:

                return "REQUEST";

             default:
                 return null;

        }
    }
}
