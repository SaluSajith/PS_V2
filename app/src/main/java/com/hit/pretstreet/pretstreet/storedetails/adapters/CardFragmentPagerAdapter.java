package com.hit.pretstreet.pretstreet.storedetails.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.storedetails.interfaces.CardAdapter;
import com.hit.pretstreet.pretstreet.storedetails.model.Testimonials;
import com.hit.pretstreet.pretstreet.storedetails.view.CardFragment;

import java.util.ArrayList;

/**
 * Created by User on 6/30/2017.
 * To setup Testimonial Card
 * @author SVS
 */
public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private ArrayList<CardFragment> fragments;
    private ArrayList<Testimonials> mTestimonialsArrayList;
    private float baseElevation;

    public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation, ArrayList<Testimonials> testimonialsArrayList) {
        super(fm);
        fragments = new ArrayList<>();
        this.mTestimonialsArrayList = testimonialsArrayList;
        this.baseElevation = baseElevation;

        for(int i = 0; i< mTestimonialsArrayList.size(); i++){
            Bundle bundle = new Bundle();
            String myMessage = mTestimonialsArrayList.get(i).getTestimonial();
            bundle.putString("message", myMessage );
            CardFragment fragInfo = new CardFragment();
            fragInfo.setArguments(bundle);
            addCardFragment(fragInfo);
        }
    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return CardFragment.getInstance(position, mTestimonialsArrayList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (CardFragment) fragment);
        return fragment;
    }

    public void addCardFragment(CardFragment fragment) {
        fragments.add(fragment);
    }

}