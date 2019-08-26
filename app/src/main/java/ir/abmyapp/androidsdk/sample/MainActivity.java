package ir.abmyapp.androidsdk.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ir.abmyapp.androidsdk.ABResources;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "ABMyAppSample";

    private TextView mHelloText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelloText = findViewById(R.id.hello_text);

        String alphaText = ABResources.get(this).getString(R.string.hello);
        String betaText = ABResources.get(this).getString("hello");

        Log.i(TAG, "Text from resources is: " + alphaText);
        Log.i(TAG, "Text from constants is: " + betaText);

        mHelloText.setText(alphaText);
    }

}
