package ru.mit.au.spb.olga.catendar;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by liza on 17.12.15.
 */
public class CompareServer {
    public int port;
    public ServerSocket serverSocket;
    public Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    private final String TAG = "CAOMPARE_SERVER";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void CompareServer() {
        try {
            serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            clientSocket = serverSocket.accept();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            Log.d(TAG, "Server initialization failed");
        }
    }

//    public void sendMessage()
}
