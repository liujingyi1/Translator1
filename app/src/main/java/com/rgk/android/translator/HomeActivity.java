package com.rgk.android.translator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rgk.android.translator.database.TranslatorStorage;
import com.rgk.android.translator.database.beans.UserBean;
import com.rgk.android.translator.ui.PairActivity;
import com.rgk.android.translator.utils.Logger;
import com.rgk.android.translator.utils.NetUtil;
import com.rgk.android.translator.view.CircleIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "RTranslator/HomeActivity";
    private static final int PAGE_ROW_COUNT = 3;
    private static final int PAGE_COLUMNS_COUNT = 3;
    private static final int PAGE_ITEMS_COUNT = PAGE_ROW_COUNT * PAGE_COLUMNS_COUNT;

    private List<RecyclerView> mPages = new ArrayList<>();
    private ViewPager mViewPager;
    private CircleIndicatorView mIndicatorView;
    private ImageButton mSettingsBtn;

    private List<HomeLanguageItem> mLanguageItems = new ArrayList<>();
    private int pageNum;
    private int lastPageItemNum;
    private TranslatorStorage mTranslatorStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (RequestPermissionsActivity.startPermissionActivity(this)) {
            Logger.i(TAG,"[onCreate]startPermissionActivity,return.");
            return;
        }

        setContentView(R.layout.activity_home);
        //初始化数据
        mTranslatorStorage = TranslatorStorage.getInstance();
        initLanguageItems();
        pageNum = mLanguageItems.size() / PAGE_ITEMS_COUNT + 1;
        lastPageItemNum = mLanguageItems.size() - (pageNum - 1) * PAGE_ITEMS_COUNT;
        Logger.v(TAG, "pageNum=" + pageNum + ", lastPageItemNum=" + lastPageItemNum);

        //初始化View
        LayoutInflater inflater = LayoutInflater.from(this);
        mViewPager = findViewById(R.id.id_language_items_container);

        for (int i = 0; i < pageNum; i++) {
            RecyclerView view = (RecyclerView) inflater.inflate(R.layout.layout_home_language_page, null);
            GridLayoutManager layoutManager = new GridLayoutManager(this, PAGE_COLUMNS_COUNT);
            view.setLayoutManager(layoutManager);
            LanguageRecyclerAdapter adapter = new LanguageRecyclerAdapter(this, i, mLanguageItems);
            adapter.setOnItemClickListener(mPagedItemOnClickListener);
            view.setAdapter(adapter);
            mPages.add(view);
        }
        mViewPager.setAdapter(mPagerAdapter);
        mIndicatorView = findViewById(R.id.id_home_page_indicator);
        mIndicatorView.setUpWithViewPager(mViewPager);
        mSettingsBtn = findViewById(R.id.id_floating_action_button);
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.v(TAG, "Settings click");
                //TODO Start settings activity
                Intent intent = new Intent(HomeActivity.this, PairActivity.class);
                startActivity(intent);
            }
        });
    }

    private OnItemClickListener mPagedItemOnClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int index) {
            Logger.v(TAG, "onItemClick:" + index + " - " + mLanguageItems.get(index).getCode());
            if (NetUtil.isNetworkConnected(HomeActivity.this)) {
                UserBean user = mTranslatorStorage.getUser();
                user.setLanguage(mLanguageItems.get(index).getCode());
                Intent composeActivityIntent = new Intent(HomeActivity.this, ComposeMessageActivity.class);
                composeActivityIntent.putExtra("LanguageName", mLanguageItems.get(index).getLanguageName());
                startActivity(composeActivityIntent);
            } else {
                //TODO start network settings
                Logger.i(TAG, "No network !");
            }
        }
    };

    public interface OnItemClickListener {
        void onItemClick(View view, int index);
    }

    class LanguageRecyclerAdapter extends RecyclerView.Adapter<LanguageRecyclerHolder> {
        private int pageIndex;
        private List<HomeLanguageItem> datas;
        private LayoutInflater inflater;

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        public LanguageRecyclerAdapter(Context context, int pageIndex, List<HomeLanguageItem> datas) {
            this.pageIndex = pageIndex;
            this.datas = datas;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public LanguageRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = inflater.inflate(R.layout.layout_home_language_item, parent, false);
            LanguageRecyclerHolder holder = new LanguageRecyclerHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull LanguageRecyclerHolder holder, final int position) {
            //Logger.v(TAG, "onBindViewHolder-pageIndex="+pageIndex+", position="+position);
            final int realIndex = pageIndex * PAGE_ITEMS_COUNT + position;
            holder.icon.setImageResource(datas.get(realIndex).getIconRes());
            holder.name.setText(datas.get(realIndex).getLanguageName());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, realIndex);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if ((pageIndex + 1) < pageNum) {
                return PAGE_ITEMS_COUNT;
            } else {
                return lastPageItemNum;
            }
        }
    }

    class LanguageRecyclerHolder extends RecyclerView.ViewHolder {
        private View view;
        private ImageView icon;
        private TextView name;

        public LanguageRecyclerHolder(View itemView) {
            super(itemView);
            view = itemView;
            icon = itemView.findViewById(R.id.id_home_language_item_icon);
            name = itemView.findViewById(R.id.id_home_language_item_name);
        }
    }

    PagerAdapter mPagerAdapter = new PagerAdapter() {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mPages.get(position));
            return mPages.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mPages.get(position));
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    };

    private void initLanguageItems() {
        mLanguageItems.clear();

        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "zh-CN", getString(R.string.language_name_zh_cn)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "zh-HK", getString(R.string.language_name_zh_hk)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "en-US", getString(R.string.language_name_en_us)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "en-AU", getString(R.string.language_name_en_au)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "en-CA", getString(R.string.language_name_en_ca)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "en-GB", getString(R.string.language_name_en_gb)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "en-IN", getString(R.string.language_name_en_in)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "en-NZ", getString(R.string.language_name_en_nz)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "ar-EG", getString(R.string.language_name_ar_eg)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "da-DK", getString(R.string.language_name_da_dk)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "de-DE", getString(R.string.language_name_de_de)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "es-ES", getString(R.string.language_name_es_es)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "es-MX", getString(R.string.language_name_es_mx)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "fi-FI", getString(R.string.language_name_fi_fi)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "fr-CA", getString(R.string.language_name_fr_ca)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "fr-FR", getString(R.string.language_name_fr_fr)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "it-IT", getString(R.string.language_name_it_it)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "ja-JP", getString(R.string.language_name_ja_jp)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "ko-KR", getString(R.string.language_name_ko_kr)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "pl-PL", getString(R.string.language_name_pl_pl)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "pt-BR", getString(R.string.language_name_pt_br)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "pt-PT", getString(R.string.language_name_pt_pt)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "ru-RU", getString(R.string.language_name_ru_ru)));
        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "sv-SE", getString(R.string.language_name_sv_se)));


        mLanguageItems.add(new HomeLanguageItem(R.mipmap.ic_launcher, "hi-IN", getString(R.string.language_name_hi_in)));
    }
}
