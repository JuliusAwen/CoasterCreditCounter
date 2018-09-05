package de.juliusawen.coastercreditcounter.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.presentation.activities.Locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class MainActivity extends AppCompatActivity
{
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_app_name));
        setSupportActionBar(toolbar);

//        this.progressBar = findViewById(R.id.progressBar);
//        this.progressBar.setVisibility(View.VISIBLE);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        textView = (TextView) findViewById(R.id.textView);
//        // Start long running operation in a background thread
//        new Thread(new Runnable() {
//            public void run() {
//                while (progressStatus < 100)
//                {
//                    progressStatus += 1;
//                    // Update the progress bar and display the
//                    //current value in the text view
//                    handler.post(new Runnable() {
//                        public void run() {
//                            progressBar.setProgress(progressStatus);
//                            textView.setText(progressStatus+"/"+progressBar.getMax());
//                        }
//                    });
//                    try {
//                        // Sleep for 200 milliseconds.
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            }
//        }).start();


//        this.progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(this, ShowLocationsActivity.class);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, Content.getInstance().getRootElement().getUuid().toString());
        startActivity(intent);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        menu.add(0, 1, Menu.NONE, R.string.selection_locations);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == 1)
        {
            Intent intent = new Intent(this, ShowLocationsActivity.class);
            //intent.putExtra(Constants.EXTRA_ELEMENT_UUID, Content.getInstance().getRootElement().getUuid().toString());
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
