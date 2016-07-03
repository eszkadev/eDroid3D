/*
 * Copyright © 2016
 * Szymon Kłos, Robert Jankowski, Wojciech Tokarski
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Szymon Kłos, Robert Jankowski and Wojciech Tokarski
 *       nor the names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL SZYMON KŁOS, ROBERT JANKOWSKI, WOJCIECH TOKARSKI BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * */

package pl.copterland.edroid3d;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final static int BLUETOOTH_ENABLE_REQUEST = 1;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private BluetoothAdapter bluetoothAdapter;

    private TextView xTextView;
    private TextView yTextView;
    private TextView zTextView;

    private ServerThread thread;
    private WorkerThread worker;

    private Frame frame;

    public enum State {
        WaitForClient,
        Stopped,
        Running
    }

    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        state = State.WaitForClient;

        frame = new Frame();
        worker = null;

        // Get UI elements
        xTextView = (TextView)findViewById(R.id.x_text_view);
        yTextView = (TextView)findViewById(R.id.y_text_view);
        zTextView = (TextView)findViewById(R.id.z_text_view);

        // Sensors initialization
        sensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Bluetooth initialization
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            updateStatus("Bluetooth not supported!");
        } else {
            // Enable bluetooth
            if (!bluetoothAdapter.isEnabled())
                enableBluetooth();
            else
                startBluetoothServer();
        }
    }

    private void enableBluetooth()
    {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST);
        }
    }

    private void startBluetoothServer()
    {
        thread = new ServerThread(this, bluetoothAdapter);
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == BLUETOOTH_ENABLE_REQUEST) {
            if (resultCode == RESULT_OK)
                startBluetoothServer();
            else if (resultCode == RESULT_CANCELED)
                updateStatus("User cancelled the action!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_enable_bluetooth) {
            enableBluetooth();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            
            xTextView.setText(Float.toString(x));
            yTextView.setText(Float.toString(y));
            zTextView.setText(Float.toString(z));

            float range = mySensor.getMaximumRange();
            frame.setFrameNumber((byte)((int)frame.getFrameNumber() + 1));
            frame.setAccelerometer((byte)(x/range*127), (byte)(y/range*127), (byte)(z/range*127));

            if (worker != null && state == State.Running)
                worker.write(frame.getData());
        }
    }

    public void setWorker(WorkerThread thread)
    {
        worker = thread;
        state = State.Stopped;
    }

    public void onWorkerFinished()
    {
        state = State.WaitForClient;
        startBluetoothServer();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updateStatus(String message)
    {
        Snackbar.make(findViewById(R.id.main_view), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void executeCommand(String command)
    {
        for(char c : command.toCharArray()) {
            if (c == 'R') {
                state = State.Running;
                updateStatus("Started");
            } else if (c == 'S') {
                state = State.Stopped;
                updateStatus("Stopped");
            } else if (c != 0){
                updateStatus("Unsupported: \'" + c + "\'");
            }
        }
    }
}
