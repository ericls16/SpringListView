package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 首页
 * @author eric liu
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	
	private Button btn_spring;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		btn_spring=(Button) findViewById(R.id.btn_spring);
	}

	private void initListener() {
		btn_spring.setOnClickListener(this);
	}

	private void initData() {
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_spring:
			startActivity(new Intent(this, SpringActivity.class));
			break;
		}
	}

}
