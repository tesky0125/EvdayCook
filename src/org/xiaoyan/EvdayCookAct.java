package org.xiaoyan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xiaoyan.db.SDCardDBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EvdayCookAct extends Activity {
    
	private Button btnRandCook = null;
	private Button btnLookCook = null;
	private Button btnRandcookpro = null;
	
	private ListView lvOrder = null;
	private List<Map<String,Object>> list_data_order = new ArrayList<Map<String,Object>>();
	private SimpleAdapter orderAdapter = null;
	int firstCnt = 1,secondCnt = 1,thirdCnt = 1;
	
	private Map<String,ArrayList<HashMap<String,Object>>> listmap_data_cook = new HashMap<String,ArrayList<HashMap<String,Object>>>();
	private SDCardDBHelper sdDBHelper = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.randcook);
        
        sdDBHelper = new SDCardDBHelper();
        sdDBHelper.init();      
        
        
        initViewListeners();
        //bindListData(list_data_cook);
        
        orderAdapter = new SimpleAdapter(this, list_data_order, R.layout.lv_item_order,
        		new String[]{"title"}, new int[]{R.id.tv_title});
        lvOrder.setAdapter(orderAdapter);
    }

    
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bindListData(listmap_data_cook);
	}



	private void initViewListeners() {
		// TODO Auto-generated method stub
    	lvOrder = (ListView) this.findViewById(R.id.lv_order);
    	btnRandCook = (Button) this.findViewById(R.id.btn_randcook);
    	btnLookCook = (Button) this.findViewById(R.id.btn_lookcook);
    	btnRandcookpro = (Button) this.findViewById(R.id.btn_randcookpro);
    	btnRandCook.setOnClickListener(listener);
    	btnLookCook.setOnClickListener(listener);
    	btnRandcookpro.setOnClickListener(listener);
	}
    
    private void bindListData(Map<String,ArrayList<HashMap<String,Object>>> listmap)
	{
		listmap.clear();
		
		for(int i=0;i<3;i++){
			
			ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
			Cursor cur = sdDBHelper.query(i);
			startManagingCursor(cur);
			
			while(cur!=null && cur.moveToNext())
			{
				HashMap<String,Object> map= new HashMap<String,Object>();
				
				String _id = cur.getString(cur.getColumnIndex("_id"));
				String title = cur.getString(cur.getColumnIndex("title"));	
				int counts = cur.getInt(cur.getColumnIndex("counts"));			
				
				map.put("_id", _id);
				map.put("title", title);
				map.put("counts", String.valueOf(counts));
				
				list.add(map);
			}		
			
			listmap.put(i+"", list);
		}
	}
    private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_randcook:
				processRandCook();
				break;
			case R.id.btn_lookcook:
				goToLookView();
				break;
			case R.id.btn_randcookpro:
				updateCounts();
				break;
			default:
				break;
			}
		}


	};
	
	private void processRandCook() {
		// TODO Auto-generated method stub
		
		int fisrtsize = listmap_data_cook.get("0").size();
		int secondsize = listmap_data_cook.get("1").size();
		int thirdsize = listmap_data_cook.get("2").size();
		int size = fisrtsize + secondsize + thirdsize;
			
		if(size <= 5 || fisrtsize <=0 ||secondsize <=0 ||thirdsize <=0 ){
			if(fisrtsize ==0){
				Toast.makeText(this, "亲！没有素菜哦。", Toast.LENGTH_SHORT).show();
				return;
			}
			if(secondsize ==0){
				Toast.makeText(this, "亲！没有荤菜哦。", Toast.LENGTH_SHORT).show();
				return;
			}
			if(thirdsize ==0){
				Toast.makeText(this, "亲！没有汤哦。", Toast.LENGTH_SHORT).show();
				return;
			}
        	Toast.makeText(this, "亲！到菜谱多添加几个菜吧。", Toast.LENGTH_SHORT).show();
        	return;
        }

		
		btnRandCook.setText(R.string.btn_randcookagain);
		btnRandcookpro.setVisibility(View.VISIBLE);
		lvOrder.setVisibility(View.VISIBLE);
		
		int [] first = new int[firstCnt];
		int [] second = new int[secondCnt];
		int [] third = new int[thirdCnt];
		
		for(int i=0;i<firstCnt;i++){
			while(true){
				boolean issingle = true;
				first[i] = (int)(Math.random()*fisrtsize);
				if(firstCnt==1) break;
				if(i>0){
					for(int j=i-1;j>=0;j--){
						if(first[i] == first[j]) {
							issingle = false;
							break;
						}
					}
					if(!issingle) continue;
				}
				break;
			}
		}
		//System.out.println(firstCnt+":"+secondCnt+":"+thirdCnt);
		for(int i=0;i<secondCnt;i++){
			while(true){
				boolean issingle = true;
				second[i] = (int)(Math.random()*secondsize);
				//System.out.println("[]"+second[i]);
				if(secondCnt==1) break;
				if(i>0){
					for(int j=i-1;j>=0;j--){
						if(second[i] == second[j]) {
							System.out.println("------");
							issingle = false;
							break;
						}
					}
					System.out.println(second[0]+":"+second[1]);
					if(!issingle) continue;
					
				}
				//System.out.println(second[0]+":"+second[1]);
				break;
			}
		}
		for(int i=0;i<thirdCnt;i++){
			while(true){
				boolean issingle = true;
				third[i] = (int)(Math.random()*thirdsize);
				if(thirdCnt==1) break;
				if(i>0){
					for(int j=i-1;j>=0;j--){
						if(third[i] == third[j]) {
							issingle = false;
							break;
						}
					}
					if(!issingle) continue;
				}
				break;
			}
		}
		
		list_data_order.clear();
		for(int i=0;i<firstCnt;i++){
			Map<String,Object>  firstCookie = listmap_data_cook.get("0").get(first[i]);
			list_data_order.add(firstCookie);
		}
		for(int j=0;j<secondCnt;j++){
			Map<String,Object>  secondCookie = listmap_data_cook.get("1").get(second[j]);
			list_data_order.add(secondCookie);
		}
		for(int k=0;k<thirdCnt;k++){
			Map<String,Object>  thirdCookie = listmap_data_cook.get("2").get(third[k]);
			list_data_order.add(thirdCookie);
		}
		
		orderAdapter.notifyDataSetChanged();
		
/*		Toast.makeText(this, first+":"+firstCookie+",\n"+
				second+":"+secondCookie+",\n"+third+":"+
				thirdCookie, Toast.LENGTH_SHORT).show();*/
	}  
	

	private void updateCounts() {
		// TODO Auto-generated method stub
		for(Map<String,Object> map : list_data_order){
			int _id = Integer.parseInt(map.get("_id").toString());
			int counts = Integer.parseInt(map.get("counts").toString());

			ContentValues values = new ContentValues();
			values.put("counts", ++counts);
			sdDBHelper.update(_id, values);
		}
		//Toast.makeText(this, "统计", Toast.LENGTH_SHORT).show();
		btnRandcookpro.setVisibility(View.INVISIBLE);
	}

	
	private static final int MenuItem_Look = Menu.FIRST;
    private static final int MenuItem_Exit = Menu.FIRST + 1;
    private static final int MenuItem_Set = Menu.FIRST + 2;
    //升级数据库
    private static final int MenuItem_UpDB = Menu.FIRST + 3;
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		menu.add(0, MenuItem_Look, 0,this.getResources().getString(R.string.menuitem_lookcook));
		menu.add(0, MenuItem_Set, 1,this.getResources().getString(R.string.menuitem_setcook));
		menu.add(0, MenuItem_Exit, 2,this.getResources().getString(R.string.menuitem_exit));
		//menu.add(0, MenuItem_UpDB, 0,"升级数据库表");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case MenuItem_Look:
			goToLookView();
			break;
		case MenuItem_Set:
			goToSetCook();
			break;
		case MenuItem_Exit:
			this.finish();
			break;
		case MenuItem_UpDB:
			//sdDBHelper.execSQL();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void goToSetCook() {
		// TODO Auto-generated method stub
		this.showDialog(Create_Dialog_Set);
	}



	private void goToLookView(){
		Intent it  = new Intent(this,LookCookAct.class);
		this.startActivity(it);
	}
	
	private static final int Create_Dialog_Set = R.id.btn_type;

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
			case Create_Dialog_Set:
				List<String> list = new ArrayList<String>();
				list.add("一素一荤一汤");
				list.add("一素两荤一汤");
				list.add("二素两荤一汤");
				return popupDialog(Create_Dialog_Set,list);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 生成弹出对话框
	 * @param index：指向生成哪个对话框
	 * @param list：对话框中list数据
	 * @param data：为点击list的item传送的数据
	 * @return
	 */
	private Dialog popupDialog(final int index, List<String> list){
		
		ListView lv = new ListView(this);
		lv.setCacheColorHint(Color.argb(0, 0, 0, 0));//使用父背景
		//lv.setSelector(this.getResources().getDrawable(R.drawable.list_item_bg));//去掉系统默认Selector设置
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , list));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> lv, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				//debugToast("item click .position:"+position);
				processDialogItem(index,position);
				removeDialog(index);
			}
		});
		
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Please:");		
		builder.setView(lv);
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				removeDialog(index);
			}
		});
		return  builder.create();
	}
	
	private void processDialogItem(int index, int position) {
		// TODO Auto-generated method stub	
		if(index == Create_Dialog_Set){
			switch(position){
				case 0:
					processFirst();
					break;
				case 1:
					processSecond();
					break;
				case 2:
					processThird();
					break;
			}
		}
		
	}



	private void processThird() {
		// TODO Auto-generated method stub
		firstCnt = 2;
		secondCnt = 2;
		thirdCnt = 1;
	}



	private void processSecond() {
		// TODO Auto-generated method stub
		firstCnt = 1;
		secondCnt = 2;
		thirdCnt = 1;
	}



	private void processFirst() {
		// TODO Auto-generated method stub
		firstCnt = 1;
		secondCnt = 1;
		thirdCnt = 1;
	}

}