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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class ServerThread extends Thread {
    private final BluetoothServerSocket serverSocket;
    private final static String APP_NAME = "copterland";
    private final static String RFCOMM_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private Activity parentActivity;
    private WorkerThread thread;

    public ServerThread(Activity parent, BluetoothAdapter adapter) {
        // Use a temporary object that is later assigned to serverSocket,
        // because serverSocket is final
        BluetoothServerSocket tmp = null;
        try {
            tmp = adapter.listenUsingRfcommWithServiceRecord(APP_NAME, UUID.fromString(RFCOMM_UUID));
        } catch (IOException e) { }
        serverSocket = tmp;
        parentActivity = parent;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                updateUI("Wait for client...");
                socket = serverSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                updateUI("Connected");
                thread = new WorkerThread(parentActivity, socket);
                thread.start();

                parentActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        ((MainActivity)parentActivity).setWorker(thread);
                    }
                });

                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void updateUI(final String message)
    {
        parentActivity.runOnUiThread(new Runnable() {
            public void run() {
                ((MainActivity)parentActivity).updateStatus(message);
            }
        });
    }

    public void cancel() {
        try {
            thread.cancel();
            serverSocket.close();
        } catch (IOException e) { }
    }
}
