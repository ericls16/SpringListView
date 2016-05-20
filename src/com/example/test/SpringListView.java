package com.example.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * 富有弹性的ListView(上下拉都有弹性)
 * 
 * @author eric liu
 * @creatTime 2016.05.20 11:14
 * @updateTime 2016.05.20 11:14
 *
 */
public class SpringListView extends ListView implements OnScrollListener {

	private static final float PULL_FACTOR = 0.5F; // 滑动因子，让下拉时有阻尼效果
	private static final int PULL_BACK_REDUCE_STEP = 1; // 下拉回弹时的步长
	private static final int PULL_BACK_TASK_PERIOD = 700; // 回弹时移动的更新间隔
	
	private boolean isRecored; // 记录正在滑动状态
	private int startY; // 起始纵坐标
	private View headView; // 增加的头部布局
	private int firstItemIndex; // ListView当前显示的第一个条目
	private boolean isUp; // 上拉标志
	private int endItemIndex; // 当前页面显示ListView的最后一个条目的索引
	private int maxItemIndex; // ListView列表项的最大索引值
	private int instance; // 滑动的距离

	private ScheduledExecutorService schedulor; // 线程池，用于控制回弹的异步任务线程
	private View rootView; // 代表本ListView
	
	/**
	 * 线程 用于对界面进行更新
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==0){
				AbsListView.LayoutParams params = (LayoutParams) headView.getLayoutParams();
				params.height -= PULL_BACK_REDUCE_STEP;
				headView.setLayoutParams(params);
				headView.invalidate();

				if (params.height <= 0) {
					schedulor.shutdownNow();
				}
			}else if(msg.what==1){
				rootView.setTranslationY(0);
				invalidate();
				schedulor.shutdownNow();
			}
			
		}
	};
	
	public SpringListView(Context context) {
		super(context);
		init();
	}
	
	public SpringListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SpringListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setOnScrollListener(this);
		
		headView = new View(this.getContext());
		headView.setBackgroundColor(Color.TRANSPARENT);
		headView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, 0));
		this.addHeaderView(headView);
		rootView=this;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			if (firstItemIndex == 0 || endItemIndex==maxItemIndex) {
				isRecored = true;
				startY = (int) event.getY();
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			if (!isRecored) {
				break;
			}
			schedulor = Executors.newScheduledThreadPool(1);
			schedulor.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if(isUp){
						Message msg1=handler.obtainMessage();
						msg1.what=1;
						handler.sendMessage(msg1);
					}else{
						Message msg2=handler.obtainMessage();
						msg2.what=0;
						handler.sendMessage(msg2);
					}
				}
			}, 0, PULL_BACK_TASK_PERIOD, TimeUnit.NANOSECONDS);

			isRecored = false;

			break;

		case MotionEvent.ACTION_MOVE:

			if (!isRecored && (firstItemIndex == 0 || endItemIndex==maxItemIndex)) {
				isRecored = true;
				startY = (int) event.getY();
			}

			if (!isRecored) {
				break;
			}

			int tempY = (int) event.getY();
			int moveY =0;
			
			int compare=tempY-startY;
			
			if(compare<0){
				moveY = -(tempY - startY);
				isUp=true;
			}else{
				moveY = tempY - startY;
				isUp=false;
			}

			if (moveY < 0) {
				isRecored = false;
				break;
			}
			
			instance=(int) (moveY * PULL_FACTOR);
			
			if(compare<0){
				this.setTranslationY(-instance);
				invalidate();
			}else{
				headView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, instance));
				headView.invalidate();
			}

			break;
		}
		return super.onTouchEvent(event);
	}

	public void onScroll(AbsListView view, int firstVisiableItem, int visibleItemCount, int totalItemCount) {
		firstItemIndex = firstVisiableItem;
		endItemIndex = firstVisiableItem+visibleItemCount-1;
		maxItemIndex=totalItemCount-1;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
}