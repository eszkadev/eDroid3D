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

public class Frame {
    public static final int FRAME_SIZE = 27;
    private byte[] data;

    public Frame()
    {
        data = new byte[FRAME_SIZE];
    }

    public Frame(byte[] frame)
    {
        data = frame.clone();
    }

    public byte[] getData()
    {
        return data;
    }

    public void setFrameNumber(byte number)
    {
        data[Offset.FRAME_NO] = number;
    }

    public byte getFrameNumber()
    {
        return data[Offset.FRAME_NO];
    }

    public void setAccelerometer(byte x, byte y, byte z)
    {
        data[Offset.PALM_X] = x;
        data[Offset.PALM_Y] = y;
        data[Offset.PALM_Z] = z;
    }

    private class Offset
    {
        public static final int FRAME_NO = 0;
        public static final int PINKY_Z = 1;
        public static final int PINKY_Y = 2;
        public static final int PINKY_X = 3;
        public static final int RING_Z = 4;
        public static final int RING_Y = 5;
        public static final int RING_X = 6;
        public static final int MIDDLE_Z = 7;
        public static final int MIDDLE_Y = 8;
        public static final int MIDDLE_X = 9;
        public static final int INDEX_Z = 10;
        public static final int INDEX_Y = 11;
        public static final int INDEX_X = 12;
        public static final int THUMB_Z = 13;
        public static final int THUMB_Y = 14;
        public static final int THUMB_X = 15;
        // Palm accelerometer has different order compared to the other sensors.
        public static final int PALM_Y = 16;
        public static final int PALM_Z = 17;
        public static final int PALM_X = 18;
        public static final int VOLTAGE = 19;
        public static final int MAGNETOMETER_X = 21;
        public static final int MAGNETOMETER_Y = 23;
        public static final int MAGNETOMETER_Z = 25;
    }
}
