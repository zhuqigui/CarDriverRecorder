package com.softwinner.bionrecorder.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.softwinner.bionrecorder.R;
public class MainActivity extends Activity implements OnClickListener{
	Button btnOpenCvbs1,btnOpenCvbs2,btnOpenNithtVideo,btnOpenRGB;
	Intent mIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnOpenCvbs1=(Button)this.findViewById(R.id.open_cvbs1);
		btnOpenCvbs2=(Button)this.findViewById(R.id.open_cvbs2);
		btnOpenNithtVideo=(Button)this.findViewById(R.id.open_night_video);
		btnOpenRGB=(Button)this.findViewById(R.id.open_rgb_video);
		btnOpenCvbs1.setOnClickListener(this);
		btnOpenCvbs2.setOnClickListener(this);
		btnOpenNithtVideo.setOnClickListener(this);
		btnOpenRGB.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.open_cvbs1:
				gotoActivity(RecordActivity.class);
			break;
			case R.id.open_cvbs2:
				gotoActivity(RecordActivity.class);
				break;
			case R.id.open_night_video:
				
				break;
			case R.id.open_rgb_video:
				
				break;
			default:
				break;
		}
	}
	public void gotoActivity(Class cls){
		mIntent=new Intent();
		mIntent.setClass(MainActivity.this, cls);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(mIntent);
	}
	
}
