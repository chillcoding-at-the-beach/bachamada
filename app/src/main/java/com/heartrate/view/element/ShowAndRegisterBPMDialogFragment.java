package com.heartrate.view.element;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.heartrate.R;


public class ShowAndRegisterBPMDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogSaveClick(int v, int effort, int how);
    }

    NoticeDialogListener mListener;
    private int mV;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Bundle b = getArguments();
        int t = b.getInt(getString(R.string.value_timer));
        mV = b.getInt(getString(R.string.value_bpa)) * 60000 / t;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater li = LayoutInflater.from(getActivity());
        final View promptsView = li.inflate(R.layout.bpm_dialog, null);

        CustomShowBPM viewBPM = (CustomShowBPM) promptsView.findViewById(R.id.show_bpm);
        viewBPM.setValue(mV);

        builder.setView(promptsView);

        //builder.setMessage(R.string.text_heart_rate)
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity

                RadioGroup radioEffortGroup = (RadioGroup) promptsView.findViewById(R.id.efforts);
                RadioGroup radioHowGroup = (RadioGroup) promptsView.findViewById(R.id.hows);
                mListener.onDialogSaveClick(mV, radioEffortGroup.getCheckedRadioButtonId(), radioHowGroup.getCheckedRadioButtonId());

            }
        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity

                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }

}
