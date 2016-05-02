package osg.susan.android.emojicents;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by susanosgood on 4/5/16.
 */
public class EmojicentsFragment extends Fragment {
    private static final String TAG = EmojicentsFragment.class.getSimpleName();
    public static final String SAVED_SPENDING_LIMIT = "mSpendingLimit";
    public static final String SAVED_REMAINDER = "remainder";
    public static final float LIMIT_RESET = 0;

    private static final String DIALOG_SUBTRACT = "DialogSubtract";
    private static final String DIALOG_ADD = "DialogAdd";
    private static final String DIALOG_LIMIT = "DialogLimit";
    private static final int REQUEST_SUBTRACT_AMOUNT = 0;
    private static final int REQUEST_ADD_AMOUNT = 1;
    private static final int REQUEST_SET_LIMIT = 2;

    private float mSpendingLimit;
    private float mTotalRemainder;
    private EditText mLimitView;
    private TextView mRemainderView;
    private TextView mPercentView;
    private ImageView mEmojiView;
    private Button addButton;
    private Button subtractButton;
    private Drawable mEmojiHappy;
    private Drawable mEmojiSmile;
    private Drawable mEmojiNeutral;
    private Drawable mEmojiHushed;
    private Drawable mEmojiSweat;
    private Drawable mEmojiWorried;
    private Drawable mEmojiFrown;
    private Drawable mEmojiDisappointed;

    private DecimalFormat mformat = new DecimalFormat("0.00");

    public static EmojicentsFragment newInstance() {
        return new EmojicentsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emojicents, container, false);

        subtractButton = (Button) view.findViewById(R.id.button_subtract);
        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                SubtractAmountDialog dialog = SubtractAmountDialog.newInstance();
                dialog.setTargetFragment(EmojicentsFragment.this, REQUEST_SUBTRACT_AMOUNT);
                dialog.show(fragmentManager, DIALOG_SUBTRACT);
            }
        });
        subtractButton.setEnabled(false);

        addButton = (Button) view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                AddAmountDialog dialog = AddAmountDialog.newInstance();
                dialog.setTargetFragment(EmojicentsFragment.this, REQUEST_ADD_AMOUNT);
                dialog.show(fragmentManager, DIALOG_ADD);
            }
        });
        addButton.setEnabled(false);

        Resources resources = getResources();
        mEmojiHappy = resources.getDrawable(R.drawable.happy);
        mEmojiSmile = resources.getDrawable(R.drawable.smile);
        mEmojiNeutral = resources.getDrawable(R.drawable.neutral);
        mEmojiHushed = resources.getDrawable(R.drawable.hushed);
        mEmojiSweat = resources.getDrawable(R.drawable.coldsweat);
        mEmojiWorried = resources.getDrawable(R.drawable.worried);
        mEmojiFrown = resources.getDrawable(R.drawable.frown);
        mEmojiDisappointed = resources.getDrawable(R.drawable.disappointed);

        mRemainderView = (TextView) view.findViewById(R.id.text_view_remainder);
        mPercentView = (TextView) view.findViewById(R.id.text_view_percent);
        mEmojiView = (ImageView) view.findViewById(R.id.image_view_emoji);

        mLimitView = (EditText) view.findViewById(R.id.edit_text_limit);
        mLimitView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    FragmentManager fragmentManager = getFragmentManager();
                    SetLimitDialog dialog = SetLimitDialog.newInstance();
                    dialog.setTargetFragment(EmojicentsFragment.this, REQUEST_SET_LIMIT);
                    dialog.show(fragmentManager, DIALOG_LIMIT);
                    mLimitView.clearFocus();
                }
            }
        });

        if (savedInstanceState != null) {
            mSpendingLimit = savedInstanceState.getFloat("mSpendingLimit");
            mTotalRemainder = savedInstanceState.getFloat("remainder");
            LimitPreferences.setStoredLimit(getActivity(), mSpendingLimit);
            LimitPreferences.setStoredRemainder(getActivity(), mTotalRemainder);

        } else {
            mSpendingLimit = LimitPreferences.getStoredLimit(getActivity());
            mTotalRemainder = LimitPreferences.getStoredRemainder(getActivity());
        }
        updateUI();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_SUBTRACT_AMOUNT) {
            float subtractAmount = (float) data.getSerializableExtra(SubtractAmountDialog.EXTRA_AMOUNT);
            if (subtractAmount != 0) {
                mTotalRemainder = mTotalRemainder - subtractAmount;
                LimitPreferences.setStoredRemainder(getActivity(), mTotalRemainder);
                updateRemainder();
            }

        } else if (requestCode == REQUEST_ADD_AMOUNT) {
            float addAmount = (float) data.getSerializableExtra(AddAmountDialog.EXTRA_AMOUNT);
            if (addAmount != 0) {
                mTotalRemainder = mTotalRemainder + addAmount;
                LimitPreferences.setStoredRemainder(getActivity(), mTotalRemainder);
                updateRemainder();
            }
        } else if (requestCode == REQUEST_SET_LIMIT) {
            float limit = (float) data.getSerializableExtra(AddAmountDialog.EXTRA_AMOUNT);
            setNewLimit(limit);
        }
    }

    private void setNewLimit(float newLimit) {
        mSpendingLimit = newLimit;
        mTotalRemainder = mSpendingLimit;
        LimitPreferences.setStoredLimit(getActivity(), mSpendingLimit);
        LimitPreferences.setStoredRemainder(getActivity(), mTotalRemainder);
        updateUI();
    }

    private void updateUI() {
        if (mSpendingLimit > 0) {
            mLimitView.setText(mformat.format(mSpendingLimit));
            mLimitView.clearFocus();
            addButton.setEnabled(true);
            subtractButton.setEnabled(true);
            updateRemainder();
        } else if (mSpendingLimit == 0) {
            // cannot add and subtract from a zero spending limit, reset fields
            addButton.setEnabled(false);
            subtractButton.setEnabled(false);
            mLimitView.setText(null);
            mRemainderView.setText(null);
            mPercentView.setText(null);
            mEmojiView.setImageDrawable(mEmojiHappy);
        }
    }

    private void updateRemainder() {
        Resources res = getResources();

        String remainderText = String.format(res.getString(R.string.remainder_currency), mformat.format(mTotalRemainder));
        mRemainderView.setText(remainderText);

        String percentString;
        float percentage = (mTotalRemainder / mSpendingLimit) * 100;
        if (Math.abs(percentage) < 1) {
            percentString = String.valueOf(mformat.format(percentage));
        } else {
            percentString = String.valueOf((int)percentage);
        }

        String percentText = String.format(res.getString(R.string.remainder_percent), percentString);
        mPercentView.setText(percentText);

        updateEmoji(percentage);
    }

    private void updateEmoji(float percent) {
        if (percent > 70) {
            mEmojiView.setImageDrawable(mEmojiHappy);
        } else if (percent > 30) {
            mEmojiView.setImageDrawable(mEmojiSmile);
        } else if (percent > 20) {
            mEmojiView.setImageDrawable(mEmojiNeutral);
        } else if (percent > 15) {
            mEmojiView.setImageDrawable(mEmojiHushed);
        } else if (percent > 10) {
            mEmojiView.setImageDrawable(mEmojiSweat);
        } else if (percent > 5) {
            mEmojiView.setImageDrawable(mEmojiWorried);
        } else if (percent > 0) {
            mEmojiView.setImageDrawable(mEmojiFrown);
        } else {
            mEmojiView.setImageDrawable(mEmojiDisappointed);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(SAVED_SPENDING_LIMIT, mSpendingLimit);
        outState.putFloat(SAVED_REMAINDER, mTotalRemainder);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_reset_app:
                setNewLimit(LIMIT_RESET);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
