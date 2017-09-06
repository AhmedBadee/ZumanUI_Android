package com.spells.zumanui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class Mode extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, Button.OnClickListener {

    private EditText sshIp;
    private EditText sshUsername;
    private EditText sshPassword;
    private EditText streamIp;

    private Button go;

    private RadioGroup modeRadio;

    private SelectedMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        sshIp       = (EditText) findViewById(R.id.ssh_ip);
        sshUsername = (EditText) findViewById(R.id.ssh_username);
        sshPassword = (EditText) findViewById(R.id.ssh_password);
        streamIp    = (EditText) findViewById(R.id.stream_ip);

        go          = (Button) findViewById(R.id.go);
        go.setOnClickListener(this);

        modeRadio   = (RadioGroup) findViewById(R.id.mode_radio);
        modeRadio.setOnCheckedChangeListener(this);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int selectedMode) {

        switch (selectedMode) {
            case R.id.mode_manned:
                mode = SelectedMode.MANNED_MODE;
                break;
            case R.id.mode_auto:
                mode = SelectedMode.AUTONOMOUS_MODE;
                break;
            case R.id.mode_dev:
                mode = SelectedMode.DEVELOPER_MODE;
                break;
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent;

        String ssh_ip       = sshIp.getText().toString();
        String ssh_username = sshUsername.getText().toString();
        String ssh_password = sshPassword.getText().toString();
        String stream_ip    = streamIp.getText().toString();

        if (mode == SelectedMode.MANNED_MODE) {
            intent = new Intent(Mode.this, Manned.class);
            intent.putExtra("SSH_IP", ssh_ip);
            intent.putExtra("SSH_USERNAME", ssh_username);
            intent.putExtra("SSH_PASSWORD", ssh_password);
            intent.putExtra("STREAM_IP", stream_ip);
            startActivity(intent);
        } else if (mode == SelectedMode.AUTONOMOUS_MODE) {
            intent = new Intent(Mode.this, Autonomous.class);
            intent.putExtra("SSH_IP", ssh_ip);
            intent.putExtra("SSH_USERNAME", ssh_username);
            intent.putExtra("SSH_PASSWORD", ssh_password);
            intent.putExtra("STREAM_IP", stream_ip);
            startActivity(intent);
        } else if (mode == SelectedMode.DEVELOPER_MODE) {
            intent = new Intent(Mode.this, Developer.class);
            intent.putExtra("SSH_IP", ssh_ip);
            intent.putExtra("SSH_USERNAME", ssh_username);
            intent.putExtra("SSH_PASSWORD", ssh_password);
            intent.putExtra("STREAM_IP", stream_ip);
            startActivity(intent);
        }
    }
}
