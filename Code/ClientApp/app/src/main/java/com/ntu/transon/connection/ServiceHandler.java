package com.ntu.transon.connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServiceHandler extends EventHandler{
//    private final static String TAG = "ServiceHandler";
    protected SocketChannel socketChannel=null;
    protected ByteBuffer input=null;
    protected ByteBuffer output=null;
    protected Packet writePck=null;
    protected Packet initPck;
	protected Reactor reactor=null;
    protected HashMap<String,Listener> commendHandleMap;
    Gson gson = new GsonBuilder().create();


    public ServiceHandler(Reactor reactor,String TAG){
        handlerName=TAG;
        this.reactor=reactor;
        commendHandleMap = new HashMap<String,Listener>();
    }


    public void open() {
        TCPSocketHandle socketHandle= (TCPSocketHandle)this.handle;
        socketChannel = socketHandle.getSocketChannel();
        try {
            reactor.registerHandler(this,  SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d( handlerName,"I-isOpened");

        if(initPck!=null) {
            write(initPck);
            initPck=null;
        }


    }


    protected Packet read() throws IOException {
        return readPacket();
    }



    public void write (Packet pck) {
//        while(getHandle()==null){
//            Log.d( handlerName,"connection is not established");
//        }
        if(socketChannel!=null) {
            Log.d( handlerName,"connection is established");
            writePck = pck;
            send();
        } else {
            initPck = pck;
        }
    }

    public void send() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                String request = gson.toJson(writePck);
                int length = request.getBytes().length;
                Log.d("Send Message Length", "I-send-Length " + length);
                output = ByteBuffer.allocate(4 + length);
                output.clear();
                output.putInt(length);
                output.put(request.getBytes());
                output.flip();
                try {
                    socketChannel.write(output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d( handlerName, "I-send " + new String(output.array()).trim());
                return null;
            }
        };
        task.execute();
    }

	private Packet readPacket() throws IOException {
        int length = readFullPacket(4).getInt();
        Log.d( handlerName, "I-readPacket " + " [size=" + length + "]");

        input = readFullPacket(length);

        String readMessage = new String(input.array()).trim();
        Log.d( handlerName, "I-readPacket " + readMessage + " [size=" + readMessage.length() + "]");
        Gson gson = new GsonBuilder().create();
        Packet packet = gson.fromJson(readMessage, Packet.class);
        return packet;
    }

    private ByteBuffer readFullPacket(int length) {
        int currentLength = 0;
        ByteBuffer buf = ByteBuffer.allocate(length);
        buf.clear();
        while(currentLength != length) {
            try {
                currentLength += socketChannel.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        buf.rewind();
        return buf;
    }

    public void addListener(String commend, Listener handleListener){
        commendHandleMap.put(commend,handleListener);
    }

    public void removeListener(String command) {
        commendHandleMap.put(command, null);
    }

    public void handleEvent() throws IOException {
        handlePacket(read());
    }

    public void handlePacket(Packet pck) {
        Listener listener = commendHandleMap.get(pck.getCommand());
        if (listener != null) {
            listener.handle(pck);
            Log.d(handlerName, "Handle " + pck.getCommand() + " finished.");
        }else{
            Log.e( handlerName,"No-such-commend");
        }
    }

    public Reactor getReactor(){
        return reactor;
    }


    public void disconnect() throws IOException {
        if( socketChannel!=null){
            socketChannel.close();
        }
    }
}
