package pl.copterland.edroid3d;

/**
 * Created by szymo_000 on 27.06.2016.
 */
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

    public void setAccelerometerX(byte x)
    {
        data[Offset.PALM_X] = x;
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
