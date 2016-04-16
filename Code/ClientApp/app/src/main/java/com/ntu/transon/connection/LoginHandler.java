package com.ntu.transon.connection;

/**
 * Created by User on 2015/1/5.
 */
public class LoginHandler extends ServiceHandler{

    private Packet initPck;
    public LoginHandler(Reactor reactor, String TAG) {
        super(reactor, TAG);
    }

    public void open() {
        super.open();
    }

    public void setInitPck(Packet pck){
                write(pck);
    }
}
