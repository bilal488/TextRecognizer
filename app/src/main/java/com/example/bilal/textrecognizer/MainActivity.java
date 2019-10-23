package com.example.bilal.textrecognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Bitmap bitmap;

    /////textToSpeech
    TextToSpeech toSpeech;
    int result;
    String text;
    //////////////textToSpeech

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        textView = (TextView)findViewById(R.id.textView);

        /////TEXT To SPEECH/////////
        toSpeech= new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS)
                {
                    result=toSpeech.setLanguage(Locale.UK);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /////TEXT TO  SPEECH/////////
    }

    public void detect(View v)
    {
        if (bitmap == null)
        {
            Toast.makeText(getApplicationContext(), "Bitmap is null", Toast.LENGTH_SHORT).show();
        }
        else
        {
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

            FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();

            firebaseVisionTextDetector.detectInImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            process_text(firebaseVisionText);
                        }
                    });
        }
    }

    private void process_text(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();
        if (blocks.size() == 0)
        {
            Toast.makeText(getApplicationContext(), "No text detected", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (FirebaseVisionText.Block block:firebaseVisionText.getBlocks())
            {
                String text = block.getText();
                textView.setText(text);
            }
        }
    }

    public void pick_image(View v)
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                imageView.setImageBitmap(bitmap);

            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    ///////TEXT TO SPEECH/////

    public void TTS(View view){
        switch (view.getId())
        {
            case R.id.bspeak:
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    text = textView.getText().toString();
                    toSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toSpeech!=null)
        {
            toSpeech.stop();
            toSpeech.shutdown();
        }
    }

    //////TEXT TO SPEECH///////////

}
