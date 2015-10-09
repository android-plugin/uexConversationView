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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.zywx.wbpalmstar.plugin.uexconversationview.vo.MessageVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.UserVO;

import java.util.List;

/**
 * Created by ylt on 15/9/14.
 */
public class ChatAdapter extends BaseAdapter {

    private List<MessageVO> mMessageVOs;
    private Context mContext;
    private UserVO mOtherUserVO;
    private UserVO mMyUserVO;
    private ChatItemView.CallBack mCallBack;

    public ChatAdapter(Context context,List<MessageVO> messageVOs,UserVO otherUserVO,UserVO myUserVO){
        mMessageVOs=messageVOs;
        mContext=context;
        mOtherUserVO=otherUserVO;
        mMyUserVO=myUserVO;
    }

    @Override
    public int getCount() {
        return mMessageVOs.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageVOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMessageVOs.get(position).getTimestamp();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageVO messageVO=mMessageVOs.get(position);
        UserVO userVO;
        ChatItemView chatItemView;
        if (convertView==null||!(convertView instanceof ChatItemView)){
            chatItemView=new ChatItemView(mContext);
        }else{
            chatItemView= (ChatItemView) convertView;
        }
        if (messageVO.getFrom()==1){
            userVO=mMyUserVO;
        }else{
            userVO=mOtherUserVO;
        }
        chatItemView.setCallBack(mCallBack);
        chatItemView.setData(userVO,messageVO);
        return chatItemView;
    }


    public void setCallBack(ChatItemView.CallBack callBack) {
        this.mCallBack=callBack;
    }
}
