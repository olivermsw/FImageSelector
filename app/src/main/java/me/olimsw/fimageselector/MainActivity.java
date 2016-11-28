package me.olimsw.fimageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import me.olimsw.fimageselectorlibrary.MPhoto;
import me.olimsw.fimageselectorlibrary.bean.PhotoInfo;
import me.olimsw.fimageselectorlibrary.ui.PhotoSelectActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_start;
    private RadioButton rb_s;
    private RadioButton rb_d;
    private RadioGroup rg;
    private EditText et_count;
    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_start = (Button) findViewById(R.id.btn_start);
        rb_s = (RadioButton) findViewById(R.id.rb_s);
        rb_d = (RadioButton) findViewById(R.id.rb_d);
        rg = (RadioGroup) findViewById(R.id.rg);
        et_count = (EditText) findViewById(R.id.et_count);
        gv = (GridView) findViewById(R.id.gv);

        btn_start.setOnClickListener(this);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_d) {
                    et_count.setVisibility(View.VISIBLE);
                } else {
                    et_count.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (rg.getCheckedRadioButtonId() == R.id.rb_s) {
                    MPhoto.getInstance().setMaxSelectCount(1);
                } else {
                    try {
                        int count = Integer.parseInt(et_count.getText().toString().trim());
                        MPhoto.getInstance().setMaxSelectCount(count);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "请输入正确的数字", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                MPhoto.getInstance().clear();
                Intent intent = new Intent(this, PhotoSelectActivity.class);
                startActivityForResult(intent, 123);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final List<PhotoInfo> selectedList = MPhoto.getInstance().getSelectedList();
        Toast.makeText(getApplicationContext(), selectedList.size()+"", Toast.LENGTH_LONG).show();
        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return selectedList.size();
            }

            @Override
            public Object getItem(int position) {
                return selectedList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = View.inflate(getApplicationContext(),R.layout.item,null);
                ImageView iv = (ImageView) v.findViewById(R.id.iv);
                Glide.with(MainActivity.this).load(selectedList.get(position).getPhotoPath()).asBitmap().into(iv);
                return v;
            }
        });


    }
}
