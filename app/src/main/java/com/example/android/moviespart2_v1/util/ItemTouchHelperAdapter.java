package com.example.android.moviespart2_v1.util;

/**
 * Created by waiyi on 12/12/2017.
 */

public interface ItemTouchHelperAdapter {

    /**
     * This interface is copied from the work of Paul Burke
     *
     * @ https://gist.github.com/iPaulPro/5d43325ac7ae579760a9
     */


    void onItemMove(int fromPosition, int toPosition);

    String onItemDismiss(int position);
}



