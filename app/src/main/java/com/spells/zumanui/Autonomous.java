package com.spells.zumanui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Autonomous extends AppCompatActivity {

    private String ssh_ip;
    private String ssh_username;
    private String ssh_password;

    private Spinner positions;

    private Button autoGo;

    private SSHApplication sshApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomous);

        Intent intent = getIntent();
        ssh_ip = intent.getStringExtra("SSH_IP");
        ssh_username = intent.getStringExtra("SSH_USERNAME");
        ssh_password = intent.getStringExtra("SSH_PASSWORD");

        positions = (Spinner) findViewById(R.id.positions);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.places_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positions.setAdapter(adapter);

        autoGo = (Button) findViewById(R.id.auto_go_btn);

        new SSHConnectionTask(Autonomous.this).execute();
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
            sshApplication = new SSHApplication(Autonomous.this, ssh_ip, ssh_username, ssh_password);

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
}
