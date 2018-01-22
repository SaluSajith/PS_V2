package com.hit.pretstreet.pretstreet.storedetails.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.CardAdapter;
import com.hit.pretstreet.pretstreet.storedetails.model.Testimonials;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 6/30/2017.
 */

public class CardFragment extends Fragment {

    @BindView(R.id.cardView) CardView cardView;
    @BindView(R.id.tv_testimonial) TextViewPret tv_testimonial;
    @BindView(R.id.tv_testimonial_name) TextViewPret tv_testimonial_name;

    public static Fragment getInstance(int position, Testimonials testimonials) {
        CardFragment f = new CardFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("testimonials", testimonials);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.row_testimonials, container, false);
        ButterKnife.bind(this, view);
        initUi();
        return view;
    }

    private void initUi(){
        Testimonials testi_msg = (Testimonials) getArguments().getSerializable("testimonials");

        cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        tv_testimonial.setText("'"+ testi_msg.getTestimonial()+"'");
        tv_testimonial_name.setText(" -"+testi_msg.getName());
    }

    public CardView getCardView() {
        return cardView;
    }
}
