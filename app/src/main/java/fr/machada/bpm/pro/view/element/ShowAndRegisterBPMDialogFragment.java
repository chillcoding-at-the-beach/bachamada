package fr.machada.bpm.pro.view.element;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import de.greenrobot.event.EventBus;
import fr.machada.bpm.pro.R;
import fr.machada.bpm.pro.event.OnFBShareFCEvent;
import fr.machada.bpm.pro.model.Effort;
import fr.machada.bpm.pro.utils.FCIndicator;


public class ShowAndRegisterBPMDialogFragment extends DialogFragment {

    private FCIndicator mFCInd;

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


        mFCInd = new FCIndicator(getContext(), getResources());
        Effort ef = mFCInd.getStep(mV);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater li = LayoutInflater.from(getActivity());
        final View promptsView = li.inflate(R.layout.bpm_dialog, null);

        final RadioGroup radioEffortGroup = (RadioGroup) promptsView.findViewById(R.id.efforts);

        CustomShowBPM viewBPM = (CustomShowBPM) promptsView.findViewById(R.id.show_bpm);
        viewBPM.setValue(mV);
        viewBPM.setStep(ef);

        switch (ef) {
            case GURU:
                radioEffortGroup.check(R.id.guru);
                break;
            case WALKING:
                radioEffortGroup.check(R.id.walking);
                break;
            case INTERVAL:
                radioEffortGroup.check(R.id.interval);
                break;
            case EXERCISE:
                radioEffortGroup.check(R.id.exercise);

        }

        builder.setView(promptsView);


        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity

                RadioGroup radioHowGroup = (RadioGroup) promptsView.findViewById(R.id.hows);
                mListener.onDialogSaveClick(mV, radioEffortGroup.getCheckedRadioButtonId(), radioHowGroup.getCheckedRadioButtonId());

            }
        })
                .setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity

                    }
                });

        View fbButton = promptsView.findViewById(R.id.fb_icon);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OnFBShareFCEvent((mV)));
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }


}
