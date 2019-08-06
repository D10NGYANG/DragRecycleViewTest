package com.dlong.dragrecycleviewtest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dlong.rep.dltittlebar.DLTittleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.sr_refresh) RefreshLayout srRefresh;
    @BindView(R.id.tittle_bar) DLTittleBar tittleBar;
    @BindView(R.id.rcv_select) SwipeRecyclerView rcvSelect;
    @BindView(R.id.rcv_add) SwipeRecyclerView rcvAdd;

    private SelectAdapter selectAdapter;
    private List<DataBin> selectList = new ArrayList<>();

    private AddAdapter addAdapter;
    private List<DataBin> addList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 初始化越界滚动
        srRefresh.setEnableRefresh(false);//是否启用下拉刷新功能
        srRefresh.setEnableLoadMore(false);//是否启用上拉加载功能
        srRefresh.setEnablePureScrollMode(true);//是否启用纯滚动模式
        srRefresh.setEnableOverScrollBounce(true);//是否启用越界回弹
        srRefresh.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        srRefresh.setEnableNestedScroll(true);//是否启用嵌套滚动

        // 初始化数据
        selectList.clear();
        for (int i = 0; i < 5; i++) {
            DataBin bin = new DataBin();
            bin.name = "计算器-" + i;
            selectList.add(bin);
        }
        selectAdapter = new SelectAdapter(selectList);
        selectAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.img_delete:
                        rcvSelect.smoothOpenRightMenu(position);
                        break;
                }
            }
        });

        addList.clear();
        for (int i = 5; i < 20; i++) {
            DataBin bin = new DataBin();
            bin.name = "计算器-" + i;
            addList.add(bin);
        }
        addAdapter = new AddAdapter(addList);
        addAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.img_add:
                        DataBin bin = addList.get(position);
                        selectList.add(bin);
                        selectAdapter.notifyDataSetChanged();
                        addList.remove(bin);
                        adapter.notifyItemRemoved(position);
                        break;
                }
            }
        });

        // 已选择项开启长按拖拽
        rcvSelect.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        // 已选择项关闭侧滑删除
        rcvSelect.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。
        // 已选择项设置侧滑菜单
        rcvSelect.setSwipeMenuCreator(mSwipeMenuCreator);
        // 已选择项设置侧滑菜单点击监听
        rcvSelect.setOnItemMenuClickListener(mItemMenuClickListener);
        // 已选择项设置拖拽监听
        rcvSelect.setOnItemMoveListener(mItemMoveListener);// 监听拖拽，更新UI。
        // 已选择项设置触摸动作监听
        rcvSelect.setOnItemStateChangedListener(mStateChangedListener);

        // 设置适配器
        rcvSelect.setLayoutManager(new LinearLayoutManager(this));
        rcvSelect.setAdapter(selectAdapter);
        rcvAdd.setLayoutManager(new LinearLayoutManager(this));
        rcvAdd.setAdapter(addAdapter);
    }

    /**
     * 菜单创建器。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_60);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this).setBackground(
                        R.drawable.selector_red)
                        .setText("移除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                // 移除
                DataBin bin = selectList.get(position);
                addList.add(bin);
                addAdapter.notifyDataSetChanged();
                selectList.remove(bin);
                selectAdapter.notifyItemRemoved(position);
            }
        }
    };

    /**
     * 拖拽监听
     */
    OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = srcHolder.getAdapterPosition() - rcvSelect.getHeaderCount();
            int toPosition = targetHolder.getAdapterPosition() - rcvSelect.getHeaderCount();

            Collections.swap(selectList, fromPosition, toPosition);
            selectAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            // 此方法在Item在侧滑删除时被调用。
        }
    };

    /**
     * 触摸动作监听
     */
    private OnItemStateChangedListener mStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            LinearLayout ll = viewHolder.itemView.findViewById(R.id.ll_item);
            ImageView line = viewHolder.itemView.findViewById(R.id.line_bottom);
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                // 状态：正在拖拽。
                srRefresh.setEnableOverScrollBounce(false);//是否启用越界回弹
                srRefresh.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
                rcvSelect.setNestedScrollingEnabled(false);
                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                ll.setSelected(true);
                line.setVisibility(View.INVISIBLE);
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                // 状态：滑动删除。
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 状态：手指松开。
                srRefresh.setEnableOverScrollBounce(true);//是否启用越界回弹
                srRefresh.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
                rcvSelect.setNestedScrollingEnabled(true);
                // 在手松开的时候还原背景。
                ll.setSelected(false);
                line.setVisibility(View.VISIBLE);
            }
        }
    };
}
