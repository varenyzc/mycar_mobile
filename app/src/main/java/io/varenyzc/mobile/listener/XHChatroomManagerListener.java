package io.varenyzc.mobile.listener;

import com.starrtc.starrtcsdk.apiInterface.IXHChatroomManagerListener;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import io.varenyzc.mobile.utils.AEvent;


public class XHChatroomManagerListener implements IXHChatroomManagerListener {
    @Override
    public void onMembersUpdated(int number) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,true,number);
    }

    @Override
    public void onSelfKicked() {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,true,"");
    }

    @Override
    public void onSelfMuted(int seconds) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,true,seconds);
    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onReceivedMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_REV_MSG,true,message);
    }

    @Override
    public void onReceivePrivateMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,true,message);
    }

    @Override
    public void onError(String error) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_ERROR,true,error);
    }
}
