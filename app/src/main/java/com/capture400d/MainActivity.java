package com.capture400d;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

import com.capture400d.eos.EOSConstant;
import com.capture400d.eos.EOSData;
import com.capture400d.eos.EOSDataExtended;
import com.capture400d.eos.SetDevicePropValueData;
import com.capture400d.eos.command.EOSCommand;
import com.capture400d.eos.command.GetDeviceInfo;
import com.capture400d.eos.command.GetEvent;
import com.capture400d.eos.command.GetObject;
import com.capture400d.eos.command.OpenSession;
import com.capture400d.eos.command.PCHDDCapacity;
import com.capture400d.eos.command.SetDevicePropValue;
import com.capture400d.eos.command.SetExtendedEventInfo;
import com.capture400d.eos.command.SetPCConnectMode;
import com.capture400d.eos.command.TransferComplete;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import usb.jphoto.Command;
import usb.jphoto.Container;
import usb.jphoto.Response;
import usb.jphoto.Session;

public class MainActivity extends Activity implements Runnable {

    private static final String TAG = "Capture400DActivity";

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointOut;
    private UsbEndpoint mEndpointIn;
    private boolean isReceiverRegistered;
    private BroadcastReceiver detachReceiver;


    private Session session;
    private byte[] rawBuffer = new byte[1500000];
    private List<ObjectTransfer> objects = new ArrayList<ObjectTransfer>();
    private Thread captureThread;

    private TextView tView;
    private TextView tView2;
    private StringBuilder text;
    private StringBuilder text2;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tView = (TextView)findViewById(R.id.textView);
        this.text = new StringBuilder("Coucou");
        this.tView.setText(this.text.toString());
        this.tView2 = (TextView)findViewById(R.id.lastDownloaded);
        this.text2 = new StringBuilder("Last downloaded file: none");
        this.tView2.setText(this.text2.toString());
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (text) {
                                    tView.setText(text.toString());
                                }
                                synchronized (text2) {
                                    tView2.setText(text2.toString());
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Debug.print(e);
                }
            }
        };
        t.start();

        Debug.open();
        Debug.println("onCreate");

        this.mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        beep();
    }

    @Override
    protected void onDestroy() {
        Debug.println("onDestroy");
        Debug.close();
        super.onDestroy();
    }

    @Override
    public void run() {
        Debug.println("run");
        try {
            int wait = 6;
            for (int i = 0; i < wait; i++) {
                Thread.sleep(1000);
                print("please wait..." + (wait - i) + " seconds");
            }
            capture();
        } catch (InterruptedException e) {
            Debug.println("run interrupted.");
            Debug.print(e);
        } catch (IOException e) {
            error_beep();
            Debug.println("run IOException "+e.getClass().getName()+" message"+e.getMessage());
            Debug.print(e);
        } catch (Throwable e) {
            error_beep();
            Debug.println("run Throwable "+e.getClass().getName()+" message"+e.getMessage());
            Debug.print(e);
        }
    }

    @Override
    public void onResume() {
        Debug.println("onResume");
        super.onResume();
        Intent intent = getIntent();
        Debug.println("onResume intent: " + intent);
        String action = intent.getAction();
        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Debug.println("onResume action:" + action);
        Debug.println("onResume device:" + device);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            setDevice(device);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (mDevice != null && mDevice.equals(device)) {
                setDevice(null);
            }
        } else {
            error_beep();
            print("No Canon EOS found.");
        }
    }

    private void setDevice(UsbDevice device) {
        Debug.println("setDevice device:" + device);
        try {
            if (device != null && this.mDevice == null) {
                Debug.println("setDevice device.getInterfaceCount:" + device.getInterfaceCount());
                UsbInterface intf = device.getInterface(0);
                Debug.println("setDevice intf:" + intf);
                Debug.println("setDevice intf.getEndpointCount:"+intf.getEndpointCount());
                for (int i = 0; i<intf.getEndpointCount(); i++) {
                    UsbEndpoint endPoint = intf.getEndpoint(i);
                    Debug.println("setDevice endpoint["+i+"]:"+endPoint);
                    Debug.println("setDevice endpoint["+i+"].address:"+endPoint.getAddress());
                    if (endPoint.getAddress() == 0x02) {
                        this.mEndpointOut = endPoint;
                    } else if (endPoint.getAddress() == 0x81) {
                        this.mEndpointIn = endPoint;
                    }
                }
                Debug.println("setDevice mEndpointOut:" + this.mEndpointOut);
                Debug.println("setDevice mEndpointIn:" + this.mEndpointIn);

                Debug.println("setDevice hasPermission:" + mUsbManager.hasPermission(device));
                UsbDeviceConnection connection = mUsbManager.openDevice(device);
                Debug.println("setDevice connection:" + connection);
                if (connection != null && connection.claimInterface(intf, true)) {
                    Debug.println("setDevice open SUCCESS");
                    print("Canon EOS connected.");
                    this.mConnection = connection;
                    this.captureThread = new Thread(this);
                    this.captureThread.start();
                    registerUsbDetachedReceiver();
                } else {
                    error_beep();
                    Debug.println("setDevice open FAIL");
                    print("Connection failed.");
                    this.mConnection = null;
                    this.session = null;
                }
            } else if (device == null && this.mDevice == null) {
                error_beep();
                print("No Canon EOS found.");
                this.mConnection = null;
                this.session = null;
            } else if (device == null && this.mDevice != null) {
                print("Connection lost.");
                this.captureThread.interrupt();
                this.mConnection.close();
                this.mConnection = null;
                this.session = null;
                unregisterUsbDetachedReceiver();
                finish();
            } else if (device != null && this.mDevice != null) {
                Debug.println("Device already connected.");
            }
        } catch (Throwable e) {
            error_beep();
            Debug.print(e);
        }
        this.mDevice = device;
    }

    private void registerUsbDetachedReceiver() {
        Debug.println("registerUsbDetachedReceiver");
        if (!isReceiverRegistered) {
            this.detachReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Debug.println("onReceive");
                    Debug.println("onReceive intent: " + intent);
                    String action = intent.getAction();
                    Debug.println("onReceive action:" + action);
                    if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        Debug.println("onReceive device:" + device);
                        if (mDevice != null && mDevice.equals(device)) {
                            setDevice(null);
                        }
                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(this.detachReceiver, filter);

            isReceiverRegistered = true;
        }
    }

    private void unregisterUsbDetachedReceiver() {
        Debug.println("unregisterUsbDetachedReceiver");
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(this.detachReceiver);
            } catch (IllegalArgumentException e) {
                // Do nothing
            }
            isReceiverRegistered = false;
        }
    }

    private void sendCommand(Command command) throws InterruptedException {
        Debug.println("sendCommand command:" + command);
        Thread.sleep(200); // Avoid sending burst of commands.
        int sent = this.mConnection.bulkTransfer(this.mEndpointOut, command.getData(), command.getData().length, 1000);
        if (sent != command.getData().length) {
            Debug.println(sent + " bytes sent of " + command.getData().length + " bytes !");
        }
    }

    private void sendData(EOSData data) throws InterruptedException {
        Debug.println("sendData data:" + data);
        Thread.sleep(200); // Avoid sending burst of commands.
        int sent = this.mConnection.bulkTransfer(this.mEndpointOut, data.getData(), data.getData().length, 1000);
        if (sent != data.getData().length) {
            Debug.println(sent + " bytes sent of " + data.getData().length + " bytes !");
        }
    }

    private byte[] receiveData() {
        Debug.println("receiveData");
        int received = this.mConnection.bulkTransfer(this.mEndpointIn, rawBuffer, rawBuffer.length, 1000);
        Debug.println("receiveData received:" + received);
        byte[] result = null;
        if (received > 0) {
            result = Arrays.copyOf(rawBuffer, received);
            Debug.println("receiveData result:" + ByteUtils.toString(result));
        } else {
            Debug.println("receiveData BulkTransfer problem (return code "+received+"), simulates a KO response with Xid=0.");
            // Simulate a false KO response.
            result = ByteUtils.toByte(new int[]{0x0c,0x00,0x00,0x00,0x03,0x00,0x00,0x20,0x00,0x00,0x00,0x00});
        }
        return result;
    }

    private void capture() throws InterruptedException, IOException {
        Debug.println("capture");
        if (session == null) {
            this.session = new Session();
            InitConnection connection = new InitConnection();
            boolean connectionOk = false;
            connection.execute(this.session);
            try {
                connectionOk = connection.get(10, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                error_beep();
                Debug.print(e);
                return;
            } catch (TimeoutException e) {
                error_beep();
                Debug.print(e);
                return;
            }
            if (!connectionOk) {
                print("Failed to initialize connection.");
                error_beep();
                return;
            }
        }

        print("Waiting for capture...");
        beep();
        this.objects.clear();
        while (true) {
            while (this.objects.isEmpty()) {
                sendCommand(new GetEvent(session));
                processData();
            }

            Debug.println("Device request transfer of "+objects.size()+" object(s):");
            for (ObjectTransfer object : objects) {
                Debug.println("\tname:"+object.getFilename() + " id:"+object.getObjectId()+" size:" + object.getSize() + " bytes");
            }

            while (!this.objects.isEmpty()) {
                downloadFile(session);
            }
            print("Waiting for capture...");
            beep();
        }
    }

    private Container parseUsb(Command raw) {
        switch (raw.getBlockType()) {
            case 1:
                return new EOSCommand(raw.getData());
            case 2:
                return new EOSData(raw.getData());
            case 3:
                return new Response(raw.getData());
        }
        return new EOSDataExtended(raw.getData(), null);
    }

    private void processData() {
        while (true) {
            Container usbContainer = parseUsb(new Command(receiveData()));
            if (usbContainer instanceof EOSData) {
                List<ObjectTransfer> newObjects = ((EOSData)usbContainer).getRequestObjectTransfers();
                if (newObjects != null) {
                    this.objects.addAll(newObjects);
                }
            } else if (usbContainer instanceof Response) {
                if (((Response) usbContainer).getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                    error_beep();
                    Debug.println("Response KO:\n" + ((Response) usbContainer).toString());
                }
                break;
            }
        }
    }

    private void downloadFile(Session session) throws InterruptedException, IOException {
        long startTime = System.currentTimeMillis();

        ObjectTransfer object = this.objects.remove(0);

        print("Downloading [" + object.getFilename() + "] (1 of "+(this.objects.size()+1)+")...");

        byte[] buffer = new byte[object.getSize()];
        int bufferIndex = 0;
        int lastBufferIndex = 0;
        int retry = 0;
        sendCommand(new GetObject(session, object.getObjectId(), bufferIndex, Math.min(0xff000, buffer.length - bufferIndex)));
        int delta = 12;
        while (bufferIndex < object.getSize()) {
            EOSData data = new EOSData(receiveData());
            if (data.getBlockType() == 3) { // Response block
                if (data.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                    // Response KO.
                    retry++;
                    if (retry < 2) {
                        Debug.println("Failed to GetObject from " + lastBufferIndex + " size:" + (buffer.length - lastBufferIndex) + " retry "+retry+" ...");
                        bufferIndex = lastBufferIndex;
                    } else {
                        error_beep();
                        throw new RuntimeException("Failed to GetObject from " + lastBufferIndex + " size:" + (buffer.length - lastBufferIndex)+" after "+(retry-1)+" retry.");
                    }
                } else {
                    retry = 0;
                }
                sendCommand(new GetObject(session, object.getObjectId(), bufferIndex, Math.min(0xff000, buffer.length - bufferIndex)));
                lastBufferIndex = bufferIndex;
                Debug.println("size:" + Math.min(0xff000, buffer.length - bufferIndex));
                delta = 12;
            } else {
                // download data
                bufferIndex += ByteUtils.copy(data.getData(), buffer, delta, bufferIndex);
                print("downloaded:"+(bufferIndex*100/object.getSize())+"%");
                Debug.println("delta:" + delta + " " + bufferIndex + "/" + object.getSize());
                if (delta == 12) {
                    delta = 0;
                }
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = sdf.format(new Date())+object.getFilename().substring(object.getFilename().indexOf("."));

        File sdcard = Environment.getExternalStorageDirectory();
        File objectFile = new File(sdcard, "/Pictures/"+fileName);
        FileOutputStream fos = new FileOutputStream(objectFile);
        fos.write(buffer);
        fos.close();

        sendCommand(new TransferComplete(session, object.getObjectId()));
        Response response = new Response(receiveData());
        if (response.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
            error_beep();
            throw new RuntimeException("Failed to TransferComplete objectId=" + object.getObjectId() + " (0x" + Integer.toHexString(object.getObjectId()) + ")");
        }
        print2("Last downloaded file:\n["+fileName+"] "+object.getSize()+" bytes in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    private void print(String text) {
        Debug.println(text);
        synchronized (this.text) {
            this.text.setLength(0);
            this.text.append(text);
        }
    }

    private void print2(String text) {
        Debug.println(text);
        synchronized (this.text2) {
            this.text2.setLength(0);
            this.text2.append(text);
        }
    }

    private void beep() {
        beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    private void error_beep() {
        beep(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE, 1000);
    }

    private void beep(int type, int duration) {
        AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)*6;
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
        toneG.startTone(type, duration);
    }

    private class InitConnection extends AsyncTask<Session, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Session... params) {
            print("1/7 Open session...");
            try {
                sendCommand(new OpenSession(session));
            } catch (InterruptedException e) {
                Debug.print(e);
                return false;
            }
            Response response = new Response(receiveData());
            if (response.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                throw new RuntimeException("Failed to OpenSession.");
            }
            session.open();
            Debug.println("Session opened.");

            print("2/7 Set Extended Event Info...");
            try {
                sendCommand(new SetExtendedEventInfo(session));
            } catch (InterruptedException e) {
                Debug.print(e);
                return false;
            }
            response = new Response(receiveData());
            if (response.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                throw new RuntimeException("Failed to SetExtendedEventInfo.");
            }
            Debug.println("Event info extended.");

            print("3/7 Set PC connect mode 1...");
            try {
                sendCommand(new SetPCConnectMode(session, 1));
            } catch (InterruptedException e) {
                Debug.print(e);
                return false;
            }
            response = new Response(receiveData());
            if (response.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                throw new RuntimeException("Failed to SetPCConnectMode 1.");
            }
            Debug.println("PC connect set to 1.");

            print("4/7 Give host storage capacity...");
            try {
                sendCommand(new PCHDDCapacity(session, 0x1007fffffffl, 1));
            } catch (InterruptedException e) {
                Debug.print(e);
                return false;
            }
            response = new Response(receiveData());
            if (response.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                throw new RuntimeException("Failed to PCHDDCapacity 0x1007fffffff 1.");
            }
            Debug.println("Host storage capacity given.");

            print("5/7 Set Capture Destination 1...");
            try {
                sendCommand(new SetDevicePropValue(session));
                sendData(new SetDevicePropValueData(session, EOSConstant.DeviceProperty_CaptureDestination, 1));
            } catch (InterruptedException e) {
                Debug.print(e);
                return false;
            }
            response = new Response(receiveData());
            if (response.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                throw new RuntimeException("Failed to SetDevicePropValue CaptureDestination=1");
            }
            Debug.println("Capture destination set to 1.");

            print("6/7 Get Device Info...");
            try {
                sendCommand(new GetDeviceInfo(session));
            } catch (InterruptedException e) {
                Debug.print(e);
                return false;
            }
            new EOSData(receiveData());
            response = new Response(receiveData());
            Debug.println("Device info obtained.");

            print("7/7 Set Capture Destination 4...");
            try {
                sendCommand(new SetDevicePropValue(session));
                sendData(new SetDevicePropValueData(session, EOSConstant.DeviceProperty_CaptureDestination, 4));
            } catch (InterruptedException e) {
                Debug.print(e);
                return false;
            }
            response = new Response(receiveData());
            if (response.getCode() != EOSConstant.ReponseCode_OK.getValue()) {
                throw new RuntimeException("Failed to SetDevicePropValue CaptureDestination=4");
            }
            Debug.println("Capture Destination set to 4.");
            return true;
        }
    }

}
