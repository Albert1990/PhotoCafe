package com.brain_socket.photocafe;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Albert on 12/25/16.
 */
public class DiagTableNo extends Dialog implements View.OnClickListener{
    private Context context;
    private TableNoDiagCallBack callback;
    private EditText etTableNo;

    public DiagTableNo(Context context,TableNoDiagCallBack callback) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.diag_table_no);
        this.context = context;
        this.callback = callback;
        init();
    }

    private void init(){
        etTableNo = (EditText)findViewById(R.id.etTableNo);
        View tvProceed = findViewById(R.id.tvProceed);

        tvProceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tvProceed){
            boolean cancel = false;
            String tableNo = etTableNo.getText().toString().trim();
            if(tableNo.isEmpty()){
                cancel = true;
                etTableNo.setError(context.getString(R.string.diag_table_no_field_required));
                etTableNo.requestFocus();
            }
            if(!cancel){
                callback.onClose(tableNo);

            }
        }
    }

    public interface TableNoDiagCallBack{
        void onClose(String tableNo);
    }
}
