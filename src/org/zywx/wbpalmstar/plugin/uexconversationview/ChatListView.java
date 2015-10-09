/*
 * Copyright (c) 2015.  The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.zywx.wbpalmstar.plugin.uexconversationview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ListView;

import org.zywx.wbpalmstar.plugin.uexconversationview.vo.AddMessagesInputVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.MessageVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.UserVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylt on 15/9/14.
 */
public class ChatListView extends ListView {

    private List<MessageVO> mMessageVOs;

    private ChatAdapter mChatAdapter;

    private OnLoadingListener mLoadingListener;

    private boolean isFirst=true;//是否第一次添加消息
    public ChatListView(Context context) {
        super(context);
    }

    public void init(UserVO otherUserVO,UserVO myUserVO){
        mMessageVOs=new ArrayList<MessageVO>();
        if (mChatAdapter==null) {
            mChatAdapter = new ChatAdapter(this.getContext(), mMessageVOs, otherUserVO, myUserVO);
        }
//        SimpleHeader header=new SimpleHeader(getContext());
//        header.setTextColor(0xff0066aa);
//        header.setCircleColor(0xff33bbee);
//        setHeadable(header);
        setAdapter(mChatAdapter);
//        setOnRefreshStartListener(new OnStartListener() {
//            @Override
//            public void onStart() {
//                if (mLoadingListener != null) {
//                    mLoadingListener.onLoading();
//                }
//            }
//        });
        setBackgroundColor(Color.parseColor("#e7eff0"));
        setFadingEdgeLength(0);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setVerticalFadingEdgeEnabled(false);
        setSelector(new ColorDrawable(Color.TRANSPARENT));
        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setCacheColorHint(Color.TRANSPARENT);
        setTranscriptMode(TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    public void addMessages(AddMessagesInputVO inputVO){
        int lastVisiblePosition=getLastVisiblePosition();
        boolean needScrollToEnd=false;
        if (lastVisiblePosition!=-1&&getAdapter().getCount()-lastVisiblePosition<5){
            needScrollToEnd=true;
        }
        if (isFirst){
            needScrollToEnd=true;
            isFirst=false;
        }
//        setRefreshSuccess("");
        if (inputVO.getType()==1){
            //新消息
            mMessageVOs.addAll(inputVO.getMessages());
        }else{
            //历史记录
            List<MessageVO> messageVOs=inputVO.getMessages();
            if (messageVOs!=null&&(!messageVOs.isEmpty())){
                for (int i=messageVOs.size()-1;i>=0;i--){
                    mMessageVOs.add(0,messageVOs.get(i));
                }
            }
        }
        mChatAdapter.notifyDataSetChanged();
        if (needScrollToEnd&&getAdapter().getCount()>0){
            setSelection(getAdapter().getCount() - 1);
        }
    }

    public void changeStatusByTimestamp(long time,int status){
        for (MessageVO messageVO:mMessageVOs){
            if (messageVO.getTimestamp()==time){
                messageVO.setStatus(status);
                break;
            }
        }
        mChatAdapter.notifyDataSetChanged();
    }

    public void deleteMessageByTimestamp(long time){
        MessageVO deleteMsg=null;
        for (MessageVO messageVO:mMessageVOs){
            if (messageVO.getTimestamp()==time){
                deleteMsg=messageVO;
                break;
            }
        }
        if (deleteMsg!=null){
            mMessageVOs.remove(deleteMsg);
        }
        mChatAdapter.notifyDataSetChanged();
    }


    public void setLoadingListener(OnLoadingListener mLoadingListener) {
        this.mLoadingListener = mLoadingListener;
    }

    public void setCallBack(ChatItemView.CallBack callBack) {
        mChatAdapter.setCallBack(callBack);
    }


    public interface OnLoadingListener{
        void onLoading();
    }


}
