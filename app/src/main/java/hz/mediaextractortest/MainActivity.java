package hz.mediaextractortest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private MediaExtractorManager mMediaExtractorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaExtractorManager = new MediaExtractorManager();
    }

    public void extractorVideo(View v) {
        mMediaExtractorManager.exactorMedia();
    }
}
