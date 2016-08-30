package app.com.thetechnocafe.quicktoggles;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class ToggleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.toggle_fragment_container);

        if(fragment == null) {
            fragment = ToggleFragment.getInstance();
            fragmentManager.beginTransaction().add(R.id.toggle_fragment_container, fragment).commit();
        }
    }
}
