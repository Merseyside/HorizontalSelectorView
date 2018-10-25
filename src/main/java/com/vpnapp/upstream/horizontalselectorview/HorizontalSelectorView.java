package com.vpnapp.upstream.horizontalselectorview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HorizontalSelectorView extends LinearLayout {

    private final String TAG = "CustomPreference";

    public enum Type {
        TEXT(0), IMAGE(1);

        private int id;

        Type(int id) {
            this.id = id;
        }

        static Type fromId(int id) {
            for (Type type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No color with passed id");
        }

        public int getId() {
            return id;
        }
    }

    public enum Buttons {
        ARROWS(0);

        private int id;

        Buttons(int id) {
            this.id = id;
        }

        static Buttons fromId(int id) {
            for (Buttons type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No color with passed id");
        }

        public int getId() {
            return id;
        }
    }

    public interface OnValueChangeListener {
        void valueChanged(String entryValue);
    }

    private OnValueChangeListener listener;

    private Context context;
    private int title_id;
    private int entries_id;
    private String[] entry_values;
    private int entryValueIndex = 0;
    private int textColorId;
    private int buttonsColorId;

    private Type type;
    private Buttons buttons;

    private TextView title_tw;
    private ImageButton prev;
    private ImageButton next;
    private View value_view;

    public HorizontalSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        loadAttrs(attrs);
        initializeView();
    }

    private void loadAttrs(AttributeSet attrs) {

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalSelectorViewAttrs, 0, 0);
        title_id = array.getResourceId(R.styleable.HorizontalSelectorViewAttrs_horizontalSelectorTitle, View.NO_ID);

        entries_id = array.getResourceId(R.styleable.HorizontalSelectorViewAttrs_android_entries, View.NO_ID);

        CharSequence[] charSequencesEntryValues = array.getTextArray(R.styleable.HorizontalSelectorViewAttrs_android_entryValues);
        entry_values = new String[charSequencesEntryValues.length];
        for (int i = 0; i < charSequencesEntryValues.length; i++) {
            entry_values[i] = charSequencesEntryValues[i].toString();
        }

        type = Type.fromId(array.getInt(R.styleable.HorizontalSelectorViewAttrs_horizontalSelectorType, 0));
        buttons = Buttons.fromId(array.getInt(R.styleable.HorizontalSelectorViewAttrs_horizontalSelectorButtons, 0));

        textColorId = array.getColor(R.styleable.HorizontalSelectorViewAttrs_horizontalSelectorTextColor, View.NO_ID);
        buttonsColorId = array.getColor(R.styleable.HorizontalSelectorViewAttrs_horizontalSelectorButtonColor, View.NO_ID);

        array.recycle();
    }

    private void initializeView() {

        if (type == Type.TEXT) {
            LayoutInflater.from(context).inflate(R.layout.horizontal_selector_textview, this);

        } else {
            LayoutInflater.from(context).inflate(R.layout.horizontal_selector_imageview, this);
        }

        title_tw = findViewById(R.id.title);
        prev = findViewById(R.id.prev);
        prev.setOnClickListener(clickListener);
        setDrawableTint(prev, R.drawable.horizontal_selector_chevron_left, buttonsColorId);

        next = findViewById(R.id.next);
        next.setOnClickListener(clickListener);
        setDrawableTint(next, R.drawable.horizontal_selector_chevron_right, buttonsColorId);

        value_view = findViewById(R.id.entry);

        fillView();
    }

    private void setDrawableTint(ImageButton imageButton, int res, int color) {
        if (color != View.NO_ID) {
            Drawable mWrappedDrawable = ContextCompat.getDrawable(context, res).mutate();
            mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);
            DrawableCompat.setTint(mWrappedDrawable, color);
            DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);

            imageButton.setImageDrawable(mWrappedDrawable);
        } else {
            imageButton.setImageDrawable(ContextCompat.getDrawable(context, res));
        }
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (entry_values.length != 1) {
                if(view.getId() == R.id.prev) {
                    if (entryValueIndex == 0)
                        entryValueIndex = entry_values.length - 1;
                    else
                        entryValueIndex--;
                } else if(view.getId() == R.id.next) {
                    if (entryValueIndex == entry_values.length - 1)
                        entryValueIndex = 0;
                    else
                        entryValueIndex++;
                }
                fillView();
                if (listener != null)
                    listener.valueChanged(entry_values[entryValueIndex]);
            }
        }
    };

    private void fillView() {
        if (title_id != View.NO_ID) {
            title_tw.setText(context.getResources().getString(title_id));
            title_tw.setTextColor(textColorId);
        } else
            title_tw.setVisibility(View.GONE);

        String[] entries = context.getResources().getStringArray(entries_id);

        if (value_view instanceof TextView) {
            TextView tw = (TextView) value_view;
            tw.setTextColor(textColorId);
            tw.setText(entries[entryValueIndex]);
        } else {
            ImageView iw = (ImageView) value_view;
            int id = context.getResources().getIdentifier(entries[entryValueIndex], "drawable", context.getPackageName());
            iw.setImageResource(id);
        }

    }

    public void setCurrentEntryValue(String value) {
        for (int i = 0; i < entry_values.length; i++) {
            if (value.equals(entry_values[i])) {
                entryValueIndex = i;
                break;
            }
        }

        fillView();
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public void updateLanguage(Context context) {
        this.context = context;
        fillView();
    }
}
