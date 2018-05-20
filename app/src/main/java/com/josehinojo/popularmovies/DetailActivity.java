package com.josehinojo.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.backdrop) ImageView backdrop;
    @BindView(R.id.detail_poster) ImageView poster;
    ParcelableMovie movie;
    @BindView(R.id.movie_title) TextView title;
    @BindView(R.id.ratingBar) RatingBar average;
    @BindView(R.id.averageVote) TextView averageVote;
    @BindView(R.id.overview) TextView overview;
    @BindView(R.id.releaseDate) TextView releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        movie = (ParcelableMovie) getIntent().getExtras().getParcelable("sentMovie");

        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        /*
        change backdrop height on orientation change
        https://developer.android.com/reference/android/content/res/Configuration#orientation
         */
        if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            size.y /= 3.33;
        }else if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            size.y /= 2;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size.x, size.y);
        backdrop.setLayoutParams(layoutParams);

        title.setText(movie.getTitle());

        average.setRating((float)movie.getVoteAverage()/2);

        averageVote.setText(Double.toString(movie.getVoteAverage()));

        overview.setText(movie.getPlot());

        releaseDate.setText("Release Date:\n" +
                movie.getReleaseDate());

        Picasso.get().load(movie.getBackdropIMG()).into(backdrop);
        Picasso.get().load(movie.getPosterIMG()).into(poster);

    }
}
