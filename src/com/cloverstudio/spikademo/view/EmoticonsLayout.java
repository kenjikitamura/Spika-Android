/*
 * The MIT License (MIT)
 * 
 * Copyright © 2013 Clover Studio Ltd. All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cloverstudio.spikademo.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.model.Emoticon;
import com.cloverstudio.spikademo.lazy.Emoticons;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * EmoticonsLayout
 * 
 * Creates a two-row layout in horizontal scroll view filled with Emoticon objects.
 */

public class EmoticonsLayout extends LinearLayout {

	private Context mContext;

	public EmoticonsLayout(Context context) {
		super(context);
		mContext = context;
	}

	public EmoticonsLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void add(Emoticon emoticonA, Emoticon emoticonB) {
		addView(getView(emoticonA, emoticonB));
	}

	private View getView(Emoticon emoticonA, Emoticon emoticonB) {

		String emoticonIdA = emoticonA.getIdentifier();
		String emoticonIdB = null;
		if (emoticonB != null) {
			emoticonIdB = emoticonB.getIdentifier();
		}

		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.emoticon_item, this, false);

		ImageButton btnEmoticonA = (ImageButton) v
				.findViewById(R.id.btnEmoticonA);
		ImageButton btnEmoticonB = (ImageButton) v
				.findViewById(R.id.btnEmoticonB);
		
		Utils.displayImage(Emoticons.getInstance()
				.getItem(emoticonIdA).getImageUrl(), btnEmoticonA, ImageLoader.LARGE, R.drawable.image_stub, true);

//		btnEmoticonA.setImageBitmap(Emoticons.getInstance()
//				.getItem(emoticonIdA).getBitmap());
		
		btnEmoticonA.setOnClickListener(getEmoticonListener(Emoticons
				.getInstance().getItem(emoticonIdA)));
		if (emoticonIdB != null) {
			
			Utils.displayImage(Emoticons.getInstance()
					.getItem(emoticonIdB).getImageUrl(), btnEmoticonB, ImageLoader.LARGE, R.drawable.image_stub, true);
			
//			btnEmoticonB.setImageBitmap(Emoticons.getInstance()
//					.getItem(emoticonIdB).getBitmap());
			
			btnEmoticonB.setOnClickListener(getEmoticonListener(Emoticons
					.getInstance().getItem(emoticonIdB)));
		} else {
			btnEmoticonB.setVisibility(View.INVISIBLE);
		}

		return v;
	}

	private OnClickListener getEmoticonListener(final Emoticon emoticon) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				((WallActivity) mContext).sendEmoticon(emoticon);
			}
		};
	}

}
