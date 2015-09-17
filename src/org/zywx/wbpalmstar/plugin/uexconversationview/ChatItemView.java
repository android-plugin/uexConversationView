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
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.zywx.wbpalmstar.base.ACEImageLoader;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.MessageVO;
import org.zywx.wbpalmstar.plugin.uexconversationview.vo.UserVO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ylt on 15/9/14.
 */
public class ChatItemView extends LinearLayout {

    private TextView mMsgContentTV;
    private TextView mMsgVoiceTimeLeftTV;
    private TextView mMsgVoiceTimeRightTV;
    private TextView mTimeTV;
    private LinearLayout mLeftInfoLayout;
    private LinearLayout mRightInfoLayout;
    private LinearLayout mMsgContentLayout;//消息内容外面一层layout


    public ChatItemView(Context context) {
        super(context);
        initViews(context);
    }

    public void initViews(Context context){
        LayoutInflater.from(context).inflate(EUExUtil.getResLayoutID
                ("plugin_uex_conversation_chat_item_layout"), this, true);
        mMsgContentTV= (TextView) findViewById(EUExUtil.getResIdID("msg_txt"));
        mMsgVoiceTimeLeftTV = (TextView) findViewById(EUExUtil.getResIdID("msg_voice_time_left_tv"));
        mMsgVoiceTimeRightTV= (TextView) findViewById(EUExUtil.getResIdID("msg_voice_time_right_tv"));
        mTimeTV= (TextView) findViewById(EUExUtil.getResIdID("time_tv"));
        mLeftInfoLayout= (LinearLayout) findViewById(EUExUtil.getResIdID("chat_info_left_layout"));
        mRightInfoLayout= (LinearLayout) findViewById(EUExUtil.getResIdID("chat_info_right_layout"));
        mMsgContentLayout= (LinearLayout) findViewById(EUExUtil.getResIdID("msg_content_layout"));
    }

    public void setData(UserVO userVO, final MessageVO messageVO){
        mTimeTV.setText(getTimeString(messageVO.getTimestamp()));
        if (messageVO.getFrom()==1){
            //自己
            mLeftInfoLayout.setVisibility(View.INVISIBLE);
            mRightInfoLayout.setVisibility(View.VISIBLE);
            setInfoLayout(mRightInfoLayout, userVO);
            mMsgContentTV.setGravity(Gravity.RIGHT);
            mMsgContentLayout.setGravity(Gravity.RIGHT);
            mMsgContentTV.setBackgroundResource(EUExUtil.getResDrawableID("plugin_uexconversation_text_right_bg"));
        }else{
            //对方
            mLeftInfoLayout.setVisibility(View.VISIBLE);
            mRightInfoLayout.setVisibility(View.INVISIBLE);
            setInfoLayout(mLeftInfoLayout, userVO);
            mMsgContentTV.setGravity(Gravity.LEFT);
            mMsgContentLayout.setGravity(Gravity.LEFT);
            mMsgContentTV.setBackgroundResource(EUExUtil.getResDrawableID("plugin_uexconversation_text_left_bg"));
        }
        mMsgVoiceTimeLeftTV.setVisibility(View.GONE);
        mMsgVoiceTimeRightTV.setVisibility(View.GONE);
        if (messageVO.getType()==1){
            //文本
            mMsgContentTV.setText(messageVO.getData());
            mMsgContentTV.setGravity(Gravity.LEFT);
            mMsgContentTV.setOnClickListener(null);
            mMsgContentTV.setCompoundDrawables(null, null, null, null);
        }else{
            //语音
            mMsgContentTV.setText("");
            if (messageVO.getFrom()==1){
                //自己
                mMsgVoiceTimeLeftTV.setVisibility(View.VISIBLE);
                setVoiceTime(mMsgVoiceTimeLeftTV,messageVO);
                Drawable drawable=getResources().getDrawable(EUExUtil.getResDrawableID
                        ("plugin_uexconversation_voice_icon_right"));
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                mMsgContentTV.setCompoundDrawables(null, null, drawable, null);
            }else{
                //对方
                mMsgVoiceTimeRightTV.setVisibility(View.VISIBLE);
                setVoiceTime(mMsgVoiceTimeRightTV,messageVO);
                Drawable drawable=getResources().getDrawable(EUExUtil.getResDrawableID
                        ("plugin_uexconversation_voice_icon_left"));
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                mMsgContentTV.setCompoundDrawables(drawable, null, null, null);
            }

            mMsgContentTV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //播放语音
                    playVoice(messageVO.getData());
                }
            });
        }
    }

    private String getTimeString(long time){
        SimpleDateFormat format=new SimpleDateFormat("MM-dd  HH:mm");
        Date date=new Date(time);
        return format.format(date);
    }

    private void playVoice(String filePath){
        MediaPlayer player=new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.prepare();
            player.start();
        } catch (IOException e) {
        }
     }

    /**
     * 设置语音时长信息
     * @param timeView
     * @param messageVO
     */
    private void setVoiceTime(TextView timeView,MessageVO messageVO){
        if (messageVO.getDuration()==-1l) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
                    MediaMetadataRetriever retriever = null;
                    retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(messageVO.getData()); //在获取前，设置文件路径（应该只能是本地路径）
                    String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    retriever.release(); //释放
                    long dur = 0l;
                    if (!TextUtils.isEmpty(duration)) {
                        dur = Long.parseLong(duration);
                    }
                    if (dur%1000>=500){
                        dur+=1000;
                    }
                    messageVO.setDuration(dur / 1000);
                  } else {
                    messageVO.setDuration(1);
                }
            } catch (Exception e) {
                messageVO.setDuration(1);
            }
        }
        timeView.setText(messageVO.getDuration() + "''");

    }

    /**
     * 设置头像，昵称信息
     * @param layout
     * @param userVO
     */
    private void setInfoLayout(LinearLayout layout,UserVO userVO){
        ImageView photoView= (ImageView)layout.findViewById(EUExUtil.getResIdID("phone_img"));
        TextView nickTV= (TextView) layout.findViewById(EUExUtil.getResIdID("nick_tv"));
        ACEImageLoader.getInstance().displayImage(photoView,userVO.getPhoto());
        nickTV.setText(userVO.getNickname());
    }

}
