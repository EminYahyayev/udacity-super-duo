package it.jaschke.alexandria.ui.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.provider.AlexandriaContract;
import it.jaschke.alexandria.service.DownloadImageTask;

/**
 * Corrections:
 * <ul>
 * <li> {@code Glide} is used instead of {@link DownloadImageTask}
 * </ul>
 */
public final class BookListAdapter extends CursorAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;

    public BookListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_book, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(mContext)
                .load(imgUrl)
                .crossFade()
                .placeholder(R.color.book_cover_placeholder)
                .into(holder.bookCover);

        String bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        holder.bookTitle.setText(bookTitle);

        String bookSubTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        holder.bookSubTitle.setText(bookSubTitle);
    }

    static class ViewHolder {
        @Bind(R.id.book_item_cover) ImageView bookCover;
        @Bind(R.id.book_item_title) TextView bookTitle;
        @Bind(R.id.book_item_subtitle) TextView bookSubTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
