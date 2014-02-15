package com.progrema.superbaby.adapter.navigationdrawer;

import android.view.View;
import android.widget.TextView;

import com.progrema.superbaby.R;

/**
 * Created by iqbalpakeh on 15/2/14.
 */
public class ActionItem extends Item
{
    public ActionItem(String text)
    {
        this.setText(text);
        this.setLayout(R.layout.navigation_drawer_action_item);
    }

    /**
     * inflate view
     *
     * @param view parent view
     */
    @Override
    public void inflate(View view)
    {
        TextView actionTextView;
        actionTextView = (TextView) view.findViewById(R.id.action_type_view);
        actionTextView.setText(getText());
    }
}
