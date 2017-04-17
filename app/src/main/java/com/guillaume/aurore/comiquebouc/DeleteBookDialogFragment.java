package com.guillaume.aurore.comiquebouc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Boite de dialogue demandant la suppression d'un livre de la bibliothèque.
 *
 * @see DialogFragment
 */


public  class DeleteBookDialogFragment extends DialogFragment {
    int position;
    String title;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.position = getArguments().getInt("position");
        this.title = getArguments().getString("title");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.dialog_message,title))
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(DeleteBookDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onDialogNegativeClick(DeleteBookDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * défini l'interface permettant d'implémenter les actions des boutons positif et négatif dans
     * la class utilisant la boite de dialogue
     */
    public interface DeleteBookDialogListener {
        void onDialogPositiveClick(DeleteBookDialogFragment dialog);
        void onDialogNegativeClick(DeleteBookDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    DeleteBookDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DeleteBookDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}