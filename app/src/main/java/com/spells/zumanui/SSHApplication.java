package com.spells.zumanui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

class SSHApplication {

    private boolean connectionState = false;

    private SSHConnection sshConnection;

    SSHApplication(Context context, String host, String username, String password) {

        Log.e("Connection", "Connecting");

        int port        = 22;
        int timeout     = 30000;

        sshConnection = new SSHConnection();

        connectionState = sshConnection.openConnection(
                host,
                port,
                username,
                password,
                timeout
        );

        if (connectionState) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e("Connection", "Connected");

        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setTitle("Connection Status");
            alertDialog.setMessage("Can't Connect");

            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alertDialog.show();
        }
    }

    boolean isConnected() {
        return connectionState;
    }

    void executeCommand(String command) {

        sshConnection.sendCommand(command + "\n");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    String readOutput(String command) {

        String output;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        output = sshConnection.replaceCommand(sshConnection.receiveData(), command);

        return output;
    }

    void exit() {
        sshConnection.close();
    }
}
