package com.dlong.dragrecycleviewtest;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author D10NG
 * @date on 2019-08-06 10:08
 */
public class SelectAdapter extends BaseQuickAdapter<DataBin, BaseViewHolder> {
    public SelectAdapter(@Nullable List<DataBin> data) {
        super(R.layout.item_select, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DataBin item) {
        helper.setText(R.id.txt_name, item.name);
        helper.addOnClickListener(R.id.img_delete);
    }
}
