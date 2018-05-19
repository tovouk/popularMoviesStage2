package com.josehinojo.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class DetailActivity extends AppCompatActivity {

    ImageView backdrop;
    ImageView poster;
    ParcelableMovie movie;
    TextView title;
    RatingBar average;
    TextView averageVote;
    TextView overview;
    TextView releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
        backdrop = findViewById(R.id.backdrop);
        backdrop.setLayoutParams(layoutParams);

        poster = (ImageView)findViewById(R.id.detail_poster);

        title = (TextView)findViewById(R.id.movie_title);
        title.setText(movie.getTitle());

        average = (RatingBar)findViewById(R.id.ratingBar);
        average.setRating((float)movie.getVoteAverage()/2);

        averageVote = (TextView)findViewById(R.id.averageVote);
        averageVote.setText(Double.toString(movie.getVoteAverage()));

        overview = (TextView)findViewById(R.id.overview);
        overview.setText(movie.getPlot());

        releaseDate = (TextView)findViewById(R.id.releaseDate);
        releaseDate.setText("Release Date:\n" +
                movie.getReleaseDate());

        Picasso.get().load(movie.getBackdropIMG()).into(backdrop);
        Picasso.get().load(movie.getPosterIMG()).into(poster);

    }
}
