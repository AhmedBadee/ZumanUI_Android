package com.spells.zumanui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class Manned extends AppCompatActivity implements MediaPlayer.OnPreparedListener, SurfaceHolder.Callback{

    // private String RTSP_URL = "rtsp://192.168.1.103:8080/h264_pcm.sdp";

    private MediaPlayer mediaPlayer;

    private String ssh_ip;
    private String ssh_username;
    private String ssh_password;
    private String stream_ip;

    private EditText distanceText;
    private EditText degreeText;
    private EditText commandText;

    private TextView yawTextView;
    private TextView pitchTextView;

    private CheckBox mannedCheck;

    private SSHApplication sshApplication;

    private Boolean lightState = false;

    private int yawValue = 80;
    private int pitchValue = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manned);

        Intent intent = getIntent();
        ssh_ip = intent.getStringExtra("SSH_IP");
        ssh_username = intent.getStringExtra("SSH_USERNAME");
        ssh_password = intent.getStringExtra("SSH_PASSWORD");
        stream_ip    = intent.getStringExtra("STREAM_IP");

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFixedSize(320, 240);

        distanceText  = (EditText) findViewById(R.id.distance_txt);
        degreeText    = (EditText) findViewById(R.id.degree_txt);
        commandText   = (EditText) findViewById(R.id.command_txt);

        mannedCheck   = (CheckBox) findViewById(R.id.manned_check);
        mannedCheck.setOnCheckedChangeListener(checkedChangeListener);

        SeekBar yawSeekBar = (SeekBar) findViewById(R.id.yaw_seekbar);
        yawSeekBar.setOnSeekBarChangeListener(yawSeekBarChangeListener);
        SeekBar pitchSeekBar = (SeekBar) findViewById(R.id.pitch_seekbar);
        pitchSeekBar.setOnSeekBarChangeListener(pitchSeekBarChangeListener);

        yawTextView   = (TextView) findViewById(R.id.yaw_txt_view);
        pitchTextView = (TextView) findViewById(R.id.pitch_txt_view);

        new SSHConnectionTask(Manned.this).execute();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);

        Context context = getApplicationContext();
        Uri source = Uri.parse("rtsp://" + stream_ip + "/h264_pcm.sdp");

        try {
            // Specify the IP camera's URL and auth headers.
            mediaPlayer.setDataSource(context, source);

            // Begin the process of setting up a video stream.
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        }
        catch (Exception ignored) {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mediaPlayer.release();
    }

    CheckBox.OnCheckedChangeListener checkedChangeListener = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            String manned;

            if (isChecked) {
                manned = "rostopic pub hw_low zuman_msgs/Instruction -1 \"start_manned\" 0 0";
                new ExecuteCommandTask(Manned.this, manned).execute();
            } else {
                manned = "rostopic pub hw_low zuman_msgs/Instruction -1 \"end_manned\" 0 0";
                new ExecuteCommandTask(Manned.this, manned).execute();
            }
        }
    };

    SeekBar.OnSeekBarChangeListener yawSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            yawTextView.setText("Yaw (" + progress + ")");
            yawValue = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            String command = "rostopic pub hw_low zuman_msgs/Instruction -1 -- \"set_cam\" " + yawValue + " " + pitchValue;
            new ExecuteCommandTask(Manned.this, command).execute();
        }
    };

    SeekBar.OnSeekBarChangeListener pitchSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            pitchTextView.setText("Pitch (" + progress + ")");
            pitchValue = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            String command = "rostopic pub hw_low zuman_msgs/Instruction -1 -- \"set_cam\" " + yawValue + " " + pitchValue;
            new ExecuteCommandTask(Manned.this, command).execute();
        }
    };

    public void onDistanceBtnClick(View view) {
        String distance = distanceText.getText().toString();

        String command = "rostopic pub hw_low zuman_msgs/Instruction -1 -- \"map_move\" " + distance + " 0";
        new ExecuteCommandTask(Manned.this, command).execute();
    }

    public void onDegreeBtnClick(View view) {
        String degree = degreeText.getText().toString();

        String command = "rostopic pub hw_low zuman_msgs/Instruction -1 -- \"map_rotate\" " + degree + " 0";
        new ExecuteCommandTask(Manned.this, command).execute();
    }

    public void onLightBtnClick(View view) {
        if (lightState) { // Switch On
            String command = "rostopic pub hw_low zuman_msgs/Instruction -1 -- \"light_on\" 0 0";
            new ExecuteCommandTask(Manned.this, command).execute();
            lightState = !lightState;
        } else {          // Switch off
            String command = "rostopic pub hw_low zuman_msgs/Instruction -1 -- \"light_off\" 0 0";
            new ExecuteCommandTask(Manned.this, command).execute();
            lightState = !lightState;
        }
    }

    public void onCommandBtnClick(View view) {
        String command = commandText.getText().toString();

        new ExecuteCommandTask(Manned.this, command).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        sshApplication.executeCommand("rostopic pub hw_low zuman_msgs/Instruction -1 \"end_manned\" 0 0");
        sshApplication.exit();
    }

    private class SSHConnectionTask extends AsyncTask<Void, Void, Boolean> {

        private AlertDialog.Builder alertDialogBuilder;
        private AlertDialog alertDialog;

        private Context context;

        SSHConnectionTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Connection Status");
            alertDialogBuilder.setMessage("Connecting...");
            alertDialogBuilder.setCancelable(false);

            alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            sshApplication = new SSHApplication(Manned.this, ssh_ip, ssh_username, ssh_password);

            if (sshApplication.isConnected()) {
                sshApplication.executeCommand("sudo chown grad /dev/ttyUSB0 /dev/ttyUSB1");
                sshApplication.executeCommand("123456789");
                sshApplication.executeCommand("roscore &");
                sshApplication.executeCommand("rosrun rosserial_python serial_node.py /dev/ttyUSB0 __name:=\"manipulator\" &");
                sshApplication.executeCommand("rosrun rosserial_python serial_node.py /dev/ttyUSB1 __name:=\"camera\" &");
                sshApplication.executeCommand("rosrun rosserial_python serial_node.py tcp __name:=\"stick\" &");
            }

            return sshApplication.isConnected();
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (status) {
                alertDialog.dismiss();

                alertDialogBuilder.setMessage("Connected :D");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            } else {
                alertDialog.dismiss();

                alertDialogBuilder.setMessage("Can't Connect :)");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        }
    }

    private class ExecuteCommandTask extends AsyncTask<Void, Void, Void> {

        private AlertDialog.Builder alertDialogBuilder;
        private AlertDialog alertDialog;

        private Context context;

        private String command;

        ExecuteCommandTask(Context context, String command) {
            this.context = context;
            this.command = command;
        }

        @Override
        protected void onPreExecute() {
            alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Sending Status");
            alertDialogBuilder.setMessage("Sending...");
            alertDialogBuilder.setCancelable(false);

            alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sshApplication.executeCommand(command);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            alertDialog.dismiss();
        }
    }
}
