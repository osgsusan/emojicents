package osg.susan.android.emojicents;

import android.support.v4.app.Fragment;

public class EmojicentsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return EmojicentsFragment.newInstance();
    }

}
