package com.progrema.superbaby.ui.fragment.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.progrema.superbaby.R;
import com.progrema.superbaby.models.Nursing;
import com.progrema.superbaby.util.FormatUtils;

/**
 * Created by aria on 20/2/14.
 */
public class NursingDialogFragment extends DialogFragment {

    private static NursingDialogFragment singletonNursingDialogFragment = null;

    public NursingDialogFragment(){

    }

    public static synchronized NursingDialogFragment getInstance()
    {
        if (singletonNursingDialogFragment == null)
        {
            singletonNursingDialogFragment = new NursingDialogFragment();
        }
        return singletonNursingDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.dialog_fragment_nursing , parent, false);
        Button buttonDry = (Button) view.findViewById(R.id.dialog_choice_left);
        buttonDry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(Nursing.NURSING_TYPE_KEY, Nursing.NursingType.LEFT.getTitle());
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, result );
                getDialog().dismiss();
            }
        });

        Button buttonWet = (Button) view.findViewById(R.id.dialog_choice_right);
        buttonWet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(Nursing.NURSING_TYPE_KEY, Nursing.NursingType.RIGHT.getTitle());
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, result );
                getDialog().dismiss();
            }
        });

        Button buttonMixed = (Button) view.findViewById(R.id.dialog_choice_formula);
        Button formulaOK = (Button) view.findViewById(R.id.button_formula_ok);


        buttonMixed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout extraInfoFormula = (LinearLayout) getDialog().findViewById(R.id.container_formula_entry);
                extraInfoFormula.setVisibility(View.VISIBLE);
            }
        });

        formulaOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText inputVolume = (EditText) getDialog().findViewById(R.id.entry_text_volume);
                String volume = inputVolume.getText().toString();

                if (!FormatUtils.isValidNumber(volume)){
                    Toast invalidNumber = Toast.makeText(getActivity(),"invalid number", Toast.LENGTH_LONG);
                    invalidNumber.show();
                    return; //invalid volume
                }

                Intent result = new Intent();
                result.putExtra(Nursing.NURSING_TYPE_KEY, Nursing.NursingType.FORMULA.getTitle());
                result.putExtra(Nursing.FORMULA_VOLUME_KEY, volume);
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, result );
                getDialog().dismiss();
            }
        });

        return view;
    }


}