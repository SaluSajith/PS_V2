package com.hit.pretstreet.pretstreet.interfaces;

import com.hit.pretstreet.pretstreet.fragment.StoreDetailFragment;
import java.util.ArrayList;

/**
 * Created by User on 5/19/2017.
 */

public interface ZoomedViewListener {

    void onClicked(int position, ArrayList<StoreDetailFragment.ProductImageItem > mImagearray);
}
