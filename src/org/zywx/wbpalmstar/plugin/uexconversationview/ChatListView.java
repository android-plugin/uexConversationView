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

import org.zywx.wbpalmstar.plugin.uexconversationview.vo.AddMessagesInputVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.MessageVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.UserVO;

import java.util.ArrayList;
import java.util.List;

import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;

/**
 * Created by ylt on 15/9/14.
 */
public class ChatListView extends ZrcListView {

    private List<MessageVO> mMessageVOs;

    private ChatAdapter mChatAdapter;

    private OnLoadingListener mLoadingListener;

    public ChatListView(Context context) {
        super(context);
    }

    public void init(UserVO otherUserVO,UserVO myUserVO){
        mMessageVOs=new ArrayList<MessageVO>();
        if (mChatAdapter==null) {
            mChatAdapter = new ChatAdapter(this.getContext(), mMessageVOs, otherUserVO, myUserVO);
        }
        SimpleHeader header=new SimpleHeader(getContext());
        header.setTextColor(0xff0066aa);
        header.setCircleColor(0xff33bbee);
        setHeadable(header);
        setAdapter(mChatAdapter);
        setOnRefreshStartListener(new OnStartListener() {
            @Override
            public void onStart() {
                if (mLoadingListener != null) {
                    mLoadingListener.onLoading();
                }
            }
        });
        setBackgroundColor(Color.parseColor("#e7eff0"));
        setFadingEdgeLength(0);
        setSelector(new ColorDrawable(Color.TRANSPARENT));
        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setCacheColorHint(Color.TRANSPARENT);
    }

    public void addMessages(AddMessagesInputVO inputVO){
        setRefreshSuccess("");
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
    }

    public void setLoadingListener(OnLoadingListener mLoadingListener) {
        this.mLoadingListener = mLoadingListener;
    }


    public interface OnLoadingListener{
        void onLoading();
    }


}
