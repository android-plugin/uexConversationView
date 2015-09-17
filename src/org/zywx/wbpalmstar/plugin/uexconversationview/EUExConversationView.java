package org.zywx.wbpalmstar.plugin.uexconversationview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BDebug;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.AddMessagesInputVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.OpenInputVO;

public class EUExConversationView extends EUExBase {

    private String TAG="uexConversationView";

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_OPEN = 1;
    private static final int MSG_ADD_MESSAGES = 2;
    private Gson mGson;
    private ChatListView mChatListView;

    public EUExConversationView(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
    }

    @Override
    protected boolean clean() {
        mGson=null;
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
        mChatListView.init(inputVO.getYou(), inputVO.getMe());
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
        mChatListView.addMessages(inputVO);
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
