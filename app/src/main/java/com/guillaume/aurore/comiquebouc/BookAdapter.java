package com.guillaume.aurore.comiquebouc;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 *
 *
 * <h1>BookAdapter</h1>
 * Adapter allowing to display a list of {@link Book Book} inside a {@link RecyclerView
 * RecyclerView}.
 * Inside the class is define the ViewHolder in which to display the data.
 * The Class also define the necessary interface to link a click listener to the RecyclerView.
 *
 * @see RecyclerView
 * @see android.support.v7.widget.RecyclerView.Adapter
 * @see android.support.v7.widget.RecyclerView.ViewHolder
 */
import static com.guillaume.aurore.comiquebouc.R.color.colorAccent;


class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<Book> mBooks;
    private Context mContext;
    private static ClickListener clickListener;


    BookAdapter(Context context, List<Book> books) {
        mBooks = books;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    /**
     * <h2>BookAdapter.ViewHolder</h2>
     * Define the view to fill with the data contain in the Book
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView bookName;
        private ImageView bookImage;
        private TextView bookPages;
        private LinearLayout card;

        private ViewHolder(View itemView) {
            super(itemView);
            card = (LinearLayout) itemView.findViewById(R.id.card);
            card.setOnClickListener(this);
            card.setOnLongClickListener(this);
            bookName = (TextView) itemView.findViewById(R.id.titre);
            bookImage = (ImageView) itemView.findViewById(R.id.couverture);
            bookPages = (TextView) itemView.findViewById(R.id.page);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }


        @Override
        public boolean onLongClick(View view) {
            clickListener.onLongItemClick(getAdapterPosition(), view);
            return false;
        }
    }

    void setOnItemClickListener(ClickListener clickListener) {
        BookAdapter.clickListener = clickListener;
    }

    interface ClickListener {
        void onItemClick(int position, View v);
        void onLongItemClick(int position, View v);
    }

    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.last_read_list_item_layout, parent, false);

        return new ViewHolder(contactView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(BookAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Book book = mBooks.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.bookName;
        textView.setText(book.titreLivre);
        ImageView imageView = viewHolder.bookImage;
        imageView.setImageBitmap(book.couverture);
        viewHolder.itemView.setElevation(4);
        TextView nbpageView = viewHolder.bookPages;
        nbpageView.setText(String.format(getContext().getString(R.string.nb_of_read_pages),
                book.currentPage+1, book.tailleLivre));


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}