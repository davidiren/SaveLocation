package se.umu.dair0002.uppgift3;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainHost extends AppCompatActivity {

    /**
     * The activity hosting all fragments
     * @param savedInstanceState - bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }
}
