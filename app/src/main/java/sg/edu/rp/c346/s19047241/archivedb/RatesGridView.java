package sg.edu.rp.c346.s19047241.archivedb;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class RatesGridView extends GridView {

    public RatesGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatesGridView(Context context) {
        super(context);
    }

    public RatesGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
