/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 2015 -- changed by Elizaveta Tretyakova, elizabet.tretyakova@gmail.com
 */


package ru.mit.au.spb.olga.catendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import com.google.gson.Gson;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class CompareFragment extends Fragment {

    private static Gson gson = new Gson();
    private ServerSocket serverSocket;
    private int port;
    private final String TAG = "COMPARE_FRAGMENT";

    public CompareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        mUpdateHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                String gotMessageToCompare = msg.getData().getString("msg");
//                Week weekToDisplay = gson.fromJson(gotMessageToCompare, Week.class);
//
//                CalendarFragment calendar = new CalendarFragment();
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, calendar).commit();
//            }
//        };
//
//        mConnection = new ChatConnection(mUpdateHandler);
//
//        mNsdHelper = new NsdHelper(getActivity());
//        mNsdHelper.initializeNsd();

        View compareView = inflater.inflate(R.layout.fragment_compare, container, false);

        compareView.findViewById(R.id.advertise_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAdvertise(v);
            }
        });

        compareView.findViewById(R.id.discover_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDiscover(v);
            }
        });

        compareView.findViewById(R.id.connect_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickConnect(v);
            }
        });

        compareView.findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSend(v);
            }
        });

//        mStatusView = (TextView) compareView.findViewById(R.id.status);

        return compareView;
    }

    public void clickAdvertise(View v) {
        // Register service
//        if(mConnection.getLocalPort() > -1) {
//            mNsdHelper.registerService(mConnection.getLocalPort());
//        } else {
//            Log.d(TAG, "ServerSocket isn't bound.");
//        }
    }

    public void clickDiscover(View v) {
//        mNsdHelper.discoverServices();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void clickConnect(View v) {
//        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
//        if (service != null) {
//            Log.d(TAG, "Connecting.");
//            mConnection.connectToServer(service.getHost(),
//                    service.getPort());
//        } else {
//            Log.d(TAG, "No service to connect to!");
//        }
    }

    public void clickSend(View v) {
//        EditText messageView = (EditText) getView().findViewById(R.id.chatInput);
//        if (messageView != null) {
//            String message = gson.toJson(testWeek());
//            String[] parts = String.valueOf(messageView.getText()).split(" ");
//            String host = parts[0];
//            Integer portNumber = Integer.parseInt(parts[1]);
//            if (!message.isEmpty()) {
//                mConnection.sendMessage(message);
//            }
//            messageView.setText("");
//        }
    }

//    public void compare(String line) {
//    //TODO: compare with actual value
//        mStatusView.append("\n" + line);
//    }

    @Override
    public void onPause() {
//        if (mNsdHelper != null) {
//            mNsdHelper.stopDiscovery();
//        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mNsdHelper != null) {
//            mNsdHelper.discoverServices();
//        }
    }

    @Override
    public void onDestroy() {
//        mNsdHelper.tearDown();
//        mConnection.tearDown();
        super.onDestroy();
    }

    private Week testWeek() {
        //FIXME: just for the demo
        //GregorianCalendar start = Week.formDate();//the day is the first day of a week
        Week sampleWeek = new Week();
        ArrayList<Event> events = new ArrayList<>();
        for(int i = 1; i <= 7; i++) {
            GregorianCalendar start = new GregorianCalendar();
            start.set(java.util.Calendar.DAY_OF_WEEK, 0);
            GregorianCalendar end = start;
            end.add(java.util.Calendar.HOUR_OF_DAY, 1);
            start.set(java.util.Calendar.DAY_OF_WEEK, i);
            end.set(java.util.Calendar.DAY_OF_WEEK, i);

            Event event = new Event(0, start, end);
            event.setText("event #00" + Integer.toString(i));

            events.add(event);
        }
        Template myHometasks = new Template("myHometasks", events);
        sampleWeek.addTemplate(myHometasks);

        Event singleEvent = new Event();
        singleEvent.setText("Still alive");

        sampleWeek.addEvent(singleEvent);
        return sampleWeek;
    }
}
