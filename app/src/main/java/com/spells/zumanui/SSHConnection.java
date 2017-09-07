package com.spells.zumanui;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

class SSHConnection {

    // Create JSch variable
    private JSch jSch = null;
    // Create SSH Session
    private Session session = null;
    // Create new SSH Channel
    private Channel channel = null;

    private InputStream sshInputStream   = null;
    private OutputStream sshOutputStream = null;

    SSHConnection() {

    }

    // new SSH Connection
    boolean openConnection(String host, int port, String username, String password, int timeout) {

        boolean connectionResult = false;

        try {
            // Create new value jsch
            jSch = new JSch();

            // get session
            session = jSch.getSession(username, host, port);
            // Set password
            session.setPassword(password);

            // Set sftp server with no check key at login
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "on");
            session.setConfig(config);

            session.connect(timeout);
            // Get channel to connect with ssh server
            channel = session.openChannel("shell");

            //Connect to channel
            channel.connect();

            sshInputStream = channel.getInputStream();
            sshOutputStream = channel.getOutputStream();

            connectionResult = true;
        } catch (Exception e) {
            // Show error
            e.printStackTrace();
        }

        return connectionResult;
    }

    // function to send commands to ssh server
    void sendCommand(String command) {

        // boolean sendResult = false;

        try {
            if (sshOutputStream != null) {
                sshOutputStream.write(command.getBytes());
                sshOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // function to receive data from ssh server
    String receiveData() {

        String data = "";

        try {
            if (this.sshInputStream != null) {
                // Check for data from input stream
                int available = sshInputStream.available();

                while (available > 0) {
                    byte[] buffer = new byte[available];
                    int byteRead = this.sshInputStream.read(buffer);
                    available = available - byteRead;
                    data += new String(buffer);
                }

                data = data.replace("grad@grad:~$", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    String replaceCommand(String result, String command) {
        result = result.replace(command, "");
        return result;
    }

    // close function to close all in/out ssh streams
    void close() {
        if (session != null) {
            session.disconnect();
        }

        if (channel != null) {
            channel.disconnect();
        }

        if (sshInputStream != null) {
            try {
                sshInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sshOutputStream != null) {
            try {
                sshOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        jSch = null;
    }
}
