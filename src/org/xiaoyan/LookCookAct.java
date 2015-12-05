package org.xiaoyan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xiaoyan.db.SDCardDBHelper;
import org.xiaoyan.db.TimeUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LookCookAct extends Activity {
	
	private ListView lvCook = null;
	private List<Map<String,Object>> list_data_cook = new ArrayList<Map<String,Object>>();
	private SimpleAdapter cookAdapter = null;
	
	private RelativeLayout layoutAdd = null;
	private EditText etAdd = null;
	private Button btnType = null;
	private Button btnAdd = null;
	private TextView tvType = null;
	private String cookname = null;
	private int cooktype = -1;
	
	
	private SDCardDBHelper sdDBHelper = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookcook);
         
        sdDBHelper = new SDCardDBHelper();
        sdDBHelper.init();    
        
        initViewListeners();

        bindListData(list_data_cook);
        if(list_data_cook.size()==0){
        	Toast.makeText(this, "�װ��Σ���û����Ӳ���Ŷ��", Toast.LENGTH_LONG).show();
        }
        
        cookAdapter = new SimpleAdapter(this, list_data_cook, R.layout.lv_item_cook, 
        		new String[]{"title","counts"}, new int[]{R.id.tv_title,R.id.tv_counts});
        lvCook.setAdapter(cookAdapter);
        
    }

    private void initViewListeners() {
		// TODO Auto-generated method stub
		lvCook = (ListView) this.findViewById(R.id.lv_cook);
		layoutAdd = (RelativeLayout) this.findViewById(R.id.layout_addcook);
		etAdd = (EditText) this.findViewById(R.id.text_add);
		btnAdd = (Button) this.findViewById(R.id.btn_add);
		btnType = (Button) this.findViewById(R.id.btn_type);
		tvType = (TextView) this.findViewById(R.id.tv_type);
		
		btnAdd.setOnClickListener(listener);
		btnType.setOnClickListener(listener);
		
		lvCook.setOnItemClickListener(lvListener);
	}

    private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_add:
				processAdd();
				break;
			case R.id.btn_type:
				processType();
				break;
			default:
				break;
			}
		}

	};
	
	private void processType() {
		// TODO Auto-generated method stub
		LookCookAct.this.showDialog(Create_Dialog_Type);
	}	
	
	private Map<String,Object> itemmap = null;
	private OnItemClickListener lvListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			/*Map<String,Object>*/ itemmap = list_data_cook.get(position);
			LookCookAct.this.showDialog(Create_Dialog_ListItem);
		}
	};
	
	private void bindListData(List<Map<String,Object>> list)
	{
		list.clear();
		
		Cursor cur = sdDBHelper.query();
		startManagingCursor(cur);
		
		while(cur!=null && cur.moveToNext())
		{
			Map<String,Object> map= new HashMap<String,Object>();
			
			String _id = cur.getString(cur.getColumnIndex("_id"));
			String title = cur.getString(cur.getColumnIndex("title"));	
			int counts = cur.getInt(cur.getColumnIndex("counts"));			
			
			map.put("_id", _id);
			map.put("title", title);
			map.put("counts", String.valueOf(counts));
			
			list.add(map);
		}		
		
	}

	private void processAdd() {
		// TODO Auto-generated method stub
		cookname = etAdd.getEditableText().toString();
		if(!cookname.equals("") && cooktype != -1){
			
			for(Map<String,Object> map : list_data_cook){
				String title = map.get("title").toString();
				if(title.equals(cookname)) {
					Toast.makeText(this, "�˲����Ѵ��ڣ�", Toast.LENGTH_SHORT).show();
					etAdd.getEditableText().clear();
					return ;
				}
			}
			
			ContentValues values=new ContentValues();	
			values.put("title", cookname);
			values.put("counts", 0);
			values.put("type", cooktype);
			values.put("favor", 0);
			String time = TimeUtils.createDateTime();
			values.put("time", time);
			
			if(sdDBHelper.insert(values) == -1){
				Toast.makeText(this, "���ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "��ӳɹ���", Toast.LENGTH_SHORT).show();
				bindListData(list_data_cook);
				cookAdapter.notifyDataSetChanged();
				this.etAdd.getEditableText().clear();
				this.tvType.setText(R.string.btn_sel);
			}
			
		}else if(cookname.equals("")){
			Toast.makeText(this, "�����������", Toast.LENGTH_SHORT).show();
		}else if(cooktype == -1){
			Toast.makeText(this, "��ѡ��˱�", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	private static final int MenuItem_Add = Menu.FIRST;
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		menu.add(0, MenuItem_Add, 0,this.getResources().getString(R.string.btn_addcook));
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case MenuItem_Add:
			showAddView();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void showAddView(){
		layoutAdd.setVisibility(View.VISIBLE);
	}
	
	
	
	private static final int Create_Dialog_ListItem = R.id.lv_cook;
	private static final int Create_Dialog_Type = R.id.btn_type;

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
			case Create_Dialog_ListItem:
				List<String> list = new ArrayList<String>();
				list.add("ɾ���ò���");
				list.add("���Ϊϲ��");
				return popupDialog(Create_Dialog_ListItem,list);
			case Create_Dialog_Type:
				List<String> list2 = new ArrayList<String>();
				list2.add("�ز�");
				list2.add("���");
				list2.add("��");
				return popupDialog(Create_Dialog_Type,list2);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * ���ɵ����Ի���
	 * @param index��ָ�������ĸ��Ի���
	 * @param list���Ի�����list����
	 * @param data��Ϊ���list��item���͵�����
	 * @return
	 */
	private Dialog popupDialog(final int index, List<String> list){
		
		ListView lv = new ListView(this);
		lv.setCacheColorHint(Color.argb(0, 0, 0, 0));//ʹ�ø�����
		//lv.setSelector(this.getResources().getDrawable(R.drawable.list_item_bg));//ȥ��ϵͳĬ��Selector����
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
				/*R.layout.lv_item_func*/, list));
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
		if(index == Create_Dialog_ListItem){
			switch(position){
				case 0:
					processDelCook();
					break;
				case 1:
					processFavor();
					break;
			}
		}else if(index == Create_Dialog_Type)
		{
			cooktype=position;
			switch(position){
				case 0:
					tvType.setText("�ز�");
					break;
				case 1:
					tvType.setText("���");
					break;
				case 2:
					tvType.setText("��");
					break;
			}
		}
		
	}

	private void processFavor() {
		// TODO Auto-generated method stub
		String _id = itemmap.get("_id").toString();
		ContentValues values = new ContentValues();
		values.put("favor", 1);
		sdDBHelper.update(Integer.valueOf(_id), values);
	}

	private void processDelCook() {
		// TODO Auto-generated method stub
		String _id = itemmap.get("_id").toString();
		if(sdDBHelper.delete(Integer.valueOf(_id)) != -1){
			bindListData(list_data_cook);
			cookAdapter.notifyDataSetChanged();
			Toast.makeText(this, "ɾ���ɹ���", Toast.LENGTH_SHORT).show();
		}
	}
}