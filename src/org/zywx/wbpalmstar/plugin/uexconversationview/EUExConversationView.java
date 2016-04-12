package org.zywx.wbpalmstar.plugin.uexconversationview;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.AbsoluteLayout;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BDebug;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.AddMessagesInputVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.MessageVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.OnRefreshStatusVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.OpenInputVO;

import zrc.widget.ZrcListView;

public class EUExConversationView extends EUExBase {

    private static final int MSG_OPEN = 1;
    private static final int MSG_ADD_MESSAGES = 2;
    private static final int MSG_CLOSE = 3;
    private static final int MSG_CHANGE_STATUS_BY_TIMESTAMP = 4;
    private static final int MSG_DELETE_MESSAGE_BY_TIMESTAMP = 5;
    private static final int MSG_STOP_PLAYING = 6;
    private static final int MSG_END_REFRESHING = 7;
    private String TAG="uexConversationView";

    private static final String BUNDLE_DATA = "data";
    private Gson mGson;
    private ChatListView mChatListView;

    public EUExConversationView(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
    }

    @Override
    protected boolean clean() {
        return false;
    }


    public void open(String[] params) {
        mGson=new Gson();
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_OPEN;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void openMsg(String[] params) {
        String json = params[0];
        OpenInputVO inputVO=mGson.fromJson(json,OpenInputVO.class);
        if (mChatListView!=null){
            return;
        }
        mChatListView=new ChatListView(mContext);
        mChatListView.init(inputVO.getYou(), inputVO.getMe(), new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                OnRefreshStatusVO statusVO=new OnRefreshStatusVO();
                statusVO.status=2;
                statusVO.type=1;
                callBackPluginJs(JsConst.ON_REFRESH_STATUS_CHANGE,mGson.toJson(statusVO));
            }
        });
        AbsoluteLayout.LayoutParams layoutParams=new AbsoluteLayout.LayoutParams
                (inputVO.getW(),inputVO.getH(),inputVO.getX(),inputVO.getY());
//        layoutParams.setMargins(inputVO.getX(),inputVO.getY(),0,0);
        mChatListView.setLoadingListener(new ChatListView.OnLoadingListener() {
            @Override
            public void onLoading() {
                callBackPluginJs(JsConst.ON_PULL_LOADING,"");
            }
        });
        addViewToWebView(mChatListView, layoutParams, TAG);
        JSONObject jsonResult = new JSONObject();
        try {
            jsonResult.put("", "");
        } catch (JSONException e) {
        }
        callBackPluginJs(JsConst.CALLBACK_OPEN, jsonResult.toString());
        mChatListView.setCallBack(new ChatItemView.CallBack() {
            @Override
            public void onFailedMsgClick(MessageVO messageVO) {
                JSONObject jsonResult = new JSONObject();
                try {
                    jsonResult.put("timestamp", messageVO.getTimestamp());
                } catch (JSONException e) {
                }
                callBackPluginJs(JsConst.ON_ERROR_LABEL_CLICKED, jsonResult.toString());
            }
        });
    }

    public void addMessages(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_ADD_MESSAGES;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void addMessagesMsg(String[] params) {
        String json = params[0];
        AddMessagesInputVO inputVO=mGson.fromJson(json,AddMessagesInputVO.class);
        if (inputVO==null){
            BDebug.e("appcan","input param is null...");
            return;
        }
        parseData(inputVO);
        mChatListView.addMessages(inputVO);
    }

    private void parseData(AddMessagesInputVO inputVO){
        if (inputVO.getMessages()==null){
            return;
        }
        for (MessageVO messageVO:inputVO.getMessages()){
            if (messageVO.getType()==2){
                //录音
                String path=messageVO.getData();
                String realPath= BUtility.makeRealPath(
                        BUtility.makeUrl(mBrwView.getCurrentUrl(), path),
                        mBrwView.getCurrentWidget().m_widgetPath,
                        mBrwView.getCurrentWidget().m_wgtType);
                messageVO.setData(realPath);
            }
        }
    }

    public void close(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLOSE;
        mHandler.sendMessage(msg);
    }

    private void closeMsg(String[] params) {
        removeViewFromWebView(TAG);
        mChatListView=null;
    }

    public void changeStatusByTimestamp(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CHANGE_STATUS_BY_TIMESTAMP;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void changeStatusByTimestampMsg(String[] params) {
        String json = params[0];
        try {
            JSONObject jsonObject=new JSONObject(json);
            int status=jsonObject.getInt("status");
            long time=jsonObject.getLong("timestamp");
            mChatListView.changeStatusByTimestamp(time,status);
        } catch (JSONException e) {

        }

    }

    public void deleteMessageByTimestamp(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_DELETE_MESSAGE_BY_TIMESTAMP;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void deleteMessageByTimestampMsg(String[] params) {
        String json = params[0];
        try {
            JSONObject jsonObject=new JSONObject(json);
            long time=jsonObject.getLong("timestamp");
            mChatListView.deleteMessageByTimestamp(time);
        } catch (JSONException e) {

        }
    }

    public void stopPlaying(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_STOP_PLAYING;
        mHandler.sendMessage(msg);
    }

    private void stopPlayingMsg(String[] params) {
        mChatListView.stopPlaying();
    }

    public void endRefreshing(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_END_REFRESHING;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void endRefreshingMsg(String[] params) {
        mChatListView.setRefreshSuccess();
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_OPEN:
                openMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_ADD_MESSAGES:
                addMessagesMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLOSE:
                closeMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CHANGE_STATUS_BY_TIMESTAMP:
                changeStatusByTimestampMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_DELETE_MESSAGE_BY_TIMESTAMP:
                deleteMessageByTimestampMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_STOP_PLAYING:
                stopPlayingMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_END_REFRESHING:
                endRefreshingMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    private void callBackPluginJs(String methodName, String jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }

}
