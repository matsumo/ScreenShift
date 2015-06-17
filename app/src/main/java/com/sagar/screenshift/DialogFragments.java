package com.sagar.screenshift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by aravind on 17/6/15.
 * Class for managing alertDialogs by main activity
 */
public class DialogFragments {
    public static final String KEY_LIST_ITEM_STRINGS = "list_item_strings";
    public static final String KEY_WARNING_STRING = "warning_string";

    public static class ScreenShiftMainActivityDialog extends DialogFragment {
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if(!(activity instanceof DialogListener)){
                throw new ClassCastException(activity.toString() + " must implement YesNoListener");
            }
        }
    }

    public static class LoadProfileDialog extends ScreenShiftMainActivityDialog {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            String[] itemStrings = getArguments().getStringArray(KEY_LIST_ITEM_STRINGS);
            return new AlertDialog.Builder(getActivity()).setItems(itemStrings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((DialogListener) getActivity()).onItemClick(LoadProfileDialog.this, i);
                }
            }).create();
        }
    }

    public static class SaveProfileDialog extends ScreenShiftMainActivityDialog {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            final EditText profileNameText = new EditText(getActivity());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = params.rightMargin = params.topMargin = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            FrameLayout layout = new FrameLayout(getActivity());
            layout.addView(profileNameText, params);
            return new AlertDialog.Builder(getActivity()).setTitle("Enter profile name")
                    .setView(layout)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((DialogListener) getActivity()).onPositiveButton(SaveProfileDialog.this, profileNameText.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
        }
    }

    public static class KeepSettingsDialog extends ScreenShiftMainActivityDialog {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage("Do you want to keep the current display settings?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((DialogListener) getActivity()).onNegativeButton(KeepSettingsDialog.this);
                        }
                    })
                    .setPositiveButton("Yes", null)
                    .setCancelable(false)
                    .create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            final Handler handler = new Handler();
            final Runnable revertRunnable = new Runnable(){
                @Override
                public void run() {
                    if(dialog.isShowing()) {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ((DialogListener) getActivity()).onNegativeButton(KeepSettingsDialog.this);
                    }
                }
            };
            handler.postDelayed(revertRunnable, 1000*13);
            return dialog;
        }
    }

    public static class DisplaySettingsWarningDialog extends ScreenShiftMainActivityDialog {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getArguments().getString(KEY_WARNING_STRING))
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((DialogListener) getActivity()).onPositiveButton(DisplaySettingsWarningDialog.this, null);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
        }
    }


    public interface DialogListener {

        void onPositiveButton(DialogFragment fragment, String result);

        void onNegativeButton(DialogFragment fragment);

        void onItemClick(DialogFragment fragment, int i);
    }
}
