package com.silogood.s_permissions.custom;

import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.LineDataProvider;

/**
 * Created by Philipp Jahoda on 12/09/15.
 */
public class MyFillFormatter implements FillFormatter {

    private float mFillPos = 0f;

    public MyFillFormatter(float fillpos) {
        this.mFillPos = fillpos;
    }

    @Override
    public float getFillLinePosition(LineDataSet dataSet, LineDataProvider dataProvider) {
        // your logic could be here
        return mFillPos;
    }
}
