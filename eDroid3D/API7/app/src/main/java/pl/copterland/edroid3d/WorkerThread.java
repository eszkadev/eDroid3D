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
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WorkerThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private Activity parentActivity;

    public WorkerThread(Activity parent, BluetoothSocket clientSocket) {
        socket = clientSocket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        parentActivity = parent;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        inStream = tmpIn;
        outStream = tmpOut;
    }

    public void run() {
        final byte[] buffer = new byte[8];
        int bytes;

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                bytes = inStream.read(buffer);

                parentActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        ((MainActivity)parentActivity).executeCommand(new String(buffer));
                    }
                });
            } catch (IOException e) {
                cancel();
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            outStream.write(bytes);
        } catch (IOException e) {
            cancel();
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }

        parentActivity.runOnUiThread(new Runnable() {
            public void run() {
                ((MainActivity)parentActivity).onWorkerFinished();
            }
        });
    }

}
