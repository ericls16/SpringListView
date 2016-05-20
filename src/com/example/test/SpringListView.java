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
 * ���е��Ե�ListView(���������е���)
 * 
 * @author eric liu
 * @creatTime 2016.05.20 11:14
 * @updateTime 2016.05.20 11:14
 *
 */
public class SpringListView extends ListView implements OnScrollListener {

	private static final float PULL_FACTOR = 0.5F; // �������ӣ�������ʱ������Ч��
	private static final int PULL_BACK_REDUCE_STEP = 1; // �����ص�ʱ�Ĳ���
	private static final int PULL_BACK_TASK_PERIOD = 700; // �ص�ʱ�ƶ��ĸ��¼��
	
	private boolean isRecored; // ��¼���ڻ���״̬
	private int startY; // ��ʼ������
	private View headView; // ���ӵ�ͷ������
	private int firstItemIndex; // ListView��ǰ��ʾ�ĵ�һ����Ŀ
	private boolean isUp; // ������־
	private int endItemIndex; // ��ǰҳ����ʾListView�����һ����Ŀ������
	private int maxItemIndex; // ListView�б�����������ֵ
	private int instance; // �����ľ���

	private ScheduledExecutorService schedulor; // �̳߳أ����ڿ��ƻص����첽�����߳�
	private View rootView; // ����ListView
	
	/**
	 * �߳� ���ڶԽ�����и���
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