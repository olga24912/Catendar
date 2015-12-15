package ru.mit.au.spb.olga.catendar;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;


public class CompareFragment extends Fragment {

    NsdHelper mNsdHelper;

    private TextView mStatusView;
    private Handler mUpdateHandler;

    public static final String TAG = "NsdChat";

    ChatConnection mConnection;

    public CompareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatusView = (TextView) getView().findViewById(R.id.status);

        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String gotMessageToCompare = msg.getData().getString("msg");
//                compare(gotMessageToCompare);
            }
        };

        mConnection = new ChatConnection(mUpdateHandler);

        mNsdHelper = new NsdHelper(getActivity());
        mNsdHelper.initializeNsd();

        return inflater.inflate(R.layout.fragment_compare, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }


}
