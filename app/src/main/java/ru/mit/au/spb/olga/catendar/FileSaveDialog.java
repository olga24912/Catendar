// SimpleFileDialog.java
package ru.mit.au.spb.olga.catendar;

/*
* 
* This file is licensed under The Code Project Open License (CPOL) 1.02 
* http://www.codeproject.com/info/cpol10.aspx
* http://www.codeproject.com/info/CPOL.zip
* 
* License Preamble:
* This License governs Your use of the Work. This License is intended to allow developers to use the Source
* Code and Executable Files provided as part of the Work in any application in any form.
* 
* The main points subject to the terms of the License are:
*    Source Code and Executable Files can be used in commercial applications;
*    Source Code and Executable Files can be redistributed; and
*    Source Code can be modified to create derivative works.
*    No claim of suitability, guarantee, or any warranty whatsoever is provided. The software is provided "as-is".
*    The Article(s) accompanying the Work may not be distributed or republished without the Author's consent
* 
* This License is entered between You, the individual or other entity reading or otherwise making use of
* the Work licensed pursuant to this License and the individual or other entity which offers the Work
* under the terms of this License ("Author").
*  (See Links above for full license text)
*/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FileSaveDialog {

    private final String DEFAULT_FILE_NAME = CalendarToICSWriter.getDefaultFileName(null);

    private String mSdcardDirectory = "";
    private Context mContext;
    private TextView mTitleView;
    private String SelectedFileName = DEFAULT_FILE_NAME;
    private EditText inputText;

    private String mCurrentDir = "";
    private List<String> mSubdirs = null;
    private FileSaveDialogListener mFileSaveDialogListener = null;
    private ArrayAdapter<String> mListAdapter = null;

    //////////////////////////////////////////////////////
    // Callback interface for selected directory
    //////////////////////////////////////////////////////
    public interface FileSaveDialogListener {
        void onChosenDir(String chosenDir);
    }

    public FileSaveDialog(Context context, FileSaveDialogListener fileSaveDialogListener) {

        mContext = context;
        mSdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileSaveDialogListener = fileSaveDialogListener;

        try {
            mSdcardDirectory = new File(mSdcardDirectory).getCanonicalPath();
        } catch (IOException ioe) {
            throw new RuntimeException("Couldn't get the file path", ioe);
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // chooseFile_or_Dir() - load directory chooser dialog for initial
    // default sdcard directory
    ///////////////////////////////////////////////////////////////////////
    public void chooseFile() {
        // Initial directory is sdcard directory
        if (mCurrentDir.equals("")) {
            chooseFile(mSdcardDirectory);
        } else {
            chooseFile(mCurrentDir);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // chooseFile_or_Dir(String dir) - load directory chooser dialog for initial
    // input 'dir' directory
    ////////////////////////////////////////////////////////////////////////////////
    public void chooseFile(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dir = mSdcardDirectory;
        }

        try {
            dir = new File(dir).getCanonicalPath();
        } catch (IOException ioe) {
            return;
        }

        mCurrentDir = dir;
        mSubdirs = getDirectories(dir);

        class FileSaveDialogOnClickListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int item) {
                String mOldDir = mCurrentDir;
                String sel = "" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
                if (sel.charAt(sel.length() - 1) == '/') sel = sel.substring(0, sel.length() - 1);

                // Navigate into the sub-directory
                if (sel.equals("..")) {
                    mCurrentDir = mCurrentDir.substring(0, mCurrentDir.lastIndexOf("/"));
                } else {
                    mCurrentDir += "/" + sel;
                }
                SelectedFileName = DEFAULT_FILE_NAME;

                if ((new File(mCurrentDir).isFile())) // If the selection is a regular file
                {
                    mCurrentDir = mOldDir;
                    SelectedFileName = sel;
                }

                updateDirectory();
            }
        }

        AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog(dir, mSubdirs,
                new FileSaveDialogOnClickListener());

        dialogBuilder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Current directory chosen
                // Call registered listener supplied with the chosen directory
                if (mFileSaveDialogListener != null) {
                    {
                        SelectedFileName = inputText.getText() + "";
                        mFileSaveDialogListener.onChosenDir(mCurrentDir + "/" + SelectedFileName);
                    }
                }
            }
        }).setNegativeButton("Cancel", null);

        final AlertDialog dirsDialog = dialogBuilder.create();

        // Show directory chooser dialog
        dirsDialog.show();
    }

    private boolean createSubDir(String newDir) {
        File newDirFile = new File(newDir);
        return !newDirFile.exists() && newDirFile.mkdir();
    }

    private List<String> getDirectories(String dir) {
        List<String> dirs = new ArrayList<>();
        try {
            File dirFile = new File(dir);

            // if directory is not the base sd card directory add ".." for going up one directory
            if (!mCurrentDir.equals(mSdcardDirectory)) {
                dirs.add("..");
            }

            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return dirs;
            }

            for (File file : dirFile.listFiles()) {
                if (file.isDirectory()) {
                    // Add "/" to directory names to identify them in the list
                    dirs.add(file.getName() + "/");
                } else {
                    // Add file names to the list if we are doing a file save or file open operation
                    dirs.add(file.getName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to access the directory or file", e);
        }

        Collections.sort(dirs, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return dirs;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////                                   START DIALOG DEFINITION                                    //////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        ////////////////////////////////////////////////
        // Create title text showing file select type //
        ////////////////////////////////////////////////
        TextView mTypeTitleView = new TextView(mContext);
        mTypeTitleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        //mTypeTitleView.setTextAppearance(mContext, android.R.style.TextAppearance_Large);
        //mTypeTitleView.setTextColor( mContext.getResources().getColor(android.R.color.black) );

        mTypeTitleView.setText("Save As:");

        //need to make this a variable Save as, Open, Select Directory
        mTypeTitleView.setGravity(Gravity.CENTER_VERTICAL);

        // Create custom view for AlertDialog title
        LinearLayout titleLayout1 = new LinearLayout(mContext);
        titleLayout1.setOrientation(LinearLayout.VERTICAL);
        titleLayout1.addView(mTypeTitleView);


        ///////////////////////////////
        // Create New Folder Button  //
        ///////////////////////////////
        Button newDirButton = new Button(mContext);
        newDirButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        newDirButton.setText("New Folder");
        newDirButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final EditText input = new EditText(mContext);

                                                // Show new folder name input dialog
                                                new AlertDialog.Builder(mContext).
                                                        setTitle("New Folder Name").
                                                        setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        Editable newDir = input.getText();
                                                        String newDirName = newDir.toString();
                                                        // Create new directory
                                                        if (createSubDir(mCurrentDir + "/" + newDirName)) {
                                                            // Navigate into the new directory
                                                            mCurrentDir += "/" + newDirName;
                                                            updateDirectory();
                                                        } else {
                                                            Toast.makeText(mContext, "Failed to create '"
                                                                    + newDirName + "' folder", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }).setNegativeButton("Cancel", null).show();
                                            }
                                        }
        );
        titleLayout1.addView(newDirButton);

        /////////////////////////////////////////////////////
        // Create View with folder path and entry text box //
        /////////////////////////////////////////////////////
        LinearLayout titleLayout = new LinearLayout(mContext);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        mTitleView = new TextView(mContext);
        mTitleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mTitleView.setGravity(Gravity.CENTER_VERTICAL);
        mTitleView.setText(title);

        titleLayout.addView(mTitleView);

        inputText = new EditText(mContext);
        inputText.setText(DEFAULT_FILE_NAME);
        titleLayout.addView(inputText);

        //////////////////////////////////////////
        // Set Views and Finish Dialog builder  //
        //////////////////////////////////////////
        dialogBuilder.setView(titleLayout);
        dialogBuilder.setCustomTitle(titleLayout1);
        mListAdapter = createListAdapter(listItems);
        dialogBuilder.setSingleChoiceItems(mListAdapter, -1, onClickListener);
        dialogBuilder.setCancelable(false);
        return dialogBuilder;
    }

    private void updateDirectory() {
        mSubdirs.clear();
        mSubdirs.addAll(getDirectories(mCurrentDir));
        mTitleView.setText(mCurrentDir);
        mListAdapter.notifyDataSetChanged();

        inputText.setText(SelectedFileName);
    }

    private ArrayAdapter<String> createListAdapter(List<String> items) {
        return new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, android.R.id.text1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (v instanceof TextView) {
                    // Enable list item (directory) text wrapping
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }
                return v;
            }
        };
    }
} 
