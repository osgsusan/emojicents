package osg.susan.android.emojicents;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by susanosgood on 4/5/16.
 */
public class SubtractAmountDialog extends DialogFragment {
    private static final String TAG = SubtractAmountDialog.class.getSimpleName();
    public static final String EXTRA_AMOUNT = "osg.susan.android.emojicents.amount";
    private static final String ARG_AMOUNT = "amount";

    public static SubtractAmountDialog newInstance() {
        Bundle args = new Bundle();
//        args.putSerializable(ARG_AMOUNT, amount);

        SubtractAmountDialog fragment = new SubtractAmountDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_subtract_amount, null);

        final EditText amountView = (EditText) view.findViewById(R.id.dialog_edit_text_subtract_amount);

        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.subtract_from_total)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float amount = 0;
                        try {
                            if (amountView.getText() != null && !amountView.getText().toString().isEmpty()) {
                                amount = Float.parseFloat(amountView.getText().toString());
                            }
                            sendResult(Activity.RESULT_OK, amount);
                        } catch (NumberFormatException e) {
                            sendResult(Activity.RESULT_CANCELED, 0);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, 0);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, float addAmount) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_AMOUNT, addAmount);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
