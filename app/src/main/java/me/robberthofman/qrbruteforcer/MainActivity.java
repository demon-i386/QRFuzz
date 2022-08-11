package me.robberthofman.qrbruteforcer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.os.Handler;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private QRCodeWriter writer = new QRCodeWriter();
    private BitMatrix qrBitMatrix;
    private Bitmap qrBitmap;
    private int qrSize = 200;
    private String toEncode = "0000-0000";
    private BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
    // seek bars with their text views
    private SeekBar speedSeekBar;
    private TextView speedTextView;
    private SeekBar stringLengthSeekBar;
    private TextView stringLengthTextView;
    private SeekBar qrSizeSeekBar;
    private TextView qrSizeTextView;
    private int stringLength = 1;
    private int speed = 1;
    private int imageSize;
    private boolean running = false;
    private Switch switchBtn;
    private TextView qrCodeData;
    private Switch fuzzingType;
    private TextView currentModeShow, alertMessage;
    private Switch definedModeTypeSwitch;
    private EditText maximumDecimalOffset, minimumDecimalOffset;
    private int maximumDecimalOffsetValue, minimumDecimalOffsetValue;
    private boolean checked;

    private Handler handler = new Handler();
    private Runnable timedTask = new Runnable(){
        @Override
        public void run() {
            if (running) {
                newRandomQR();
                updateQrCode();
                handler.postDelayed(timedTask, (1000 / speed));
            }
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // set entire app to portrait
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        setup();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running) {
                    running = true;
                    handler.post(timedTask);
                }
                else {
                    running = false;
                    handler.post(timedTask);
                }
            }
        });
    }


    void setup() {
        speedSeekBar = findViewById(R.id.speedSeekBar);
        speedTextView = findViewById(R.id.speedTextView);
        stringLengthSeekBar = findViewById(R.id.stringLengthSeekBar);
        stringLengthTextView = findViewById(R.id.stringLengthTextView);
        qrSizeSeekBar = findViewById(R.id.qrSizeSeekBar);
        qrSizeTextView = findViewById(R.id.qrSizeTextView);
        switchBtn = findViewById(R.id.switch2);
        qrCodeData = findViewById(R.id.qrdata);

        speedTextView.setText("speed: " + speed);
        stringLengthTextView.setText("length: " + stringLength);
        qrSizeTextView.setText("size: " + qrSize);
        fuzzingType = findViewById(R.id.switchFuzzingType);
        currentModeShow = findViewById(R.id.currentModeTextView);

        definedModeTypeSwitch = findViewById(R.id.definedModeIncDecSwitch);

        maximumDecimalOffset = findViewById(R.id.offsetMaximumNumber);
        minimumDecimalOffset = findViewById(R.id.offsetMinimumNumber);
        alertMessage = findViewById(R.id.textViewWarnings);




        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSpeed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        stringLengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateLength(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        qrSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void updateSpeed(int newSpeed){
        speed = Math.max(1,newSpeed);
        speedTextView.setText("speed: "+speed);
    }

    void updateLength(int newLength){
        stringLength = Math.max(1, Math.round(newLength/5)); // brute force above 20 characters would take too long anyways
        stringLengthTextView.setText("length: "+stringLength);
    }

    void updateSize(int newSize){
        qrSize = Math.max(100, 50*Math.round(newSize)/10); // brute force above 20 characters would take too long anyways
        qrSizeTextView.setText("size: "+ qrSize);
        updateQrCode();
    }

    void updateQrCode(){
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try {
             qrBitMatrix = writer.encode(toEncode, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        qrCodeData.setText(toEncode);
        qrBitmap = barcodeEncoder.createBitmap(qrBitMatrix);

        ImageView imageView = findViewById(R.id.imageView);

        imageView.setImageBitmap(qrBitmap); // display new qr code
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if(switchBtn.isChecked()){
            imageSize = 1150;
        }
        else{
            imageSize = 1150;
        }
        imageView.getLayoutParams().height = imageSize;
        imageView.getLayoutParams().width = imageSize;
    }

    void newRandomQR() {
        if(fuzzingType.isChecked() && switchBtn.isChecked()){
            currentModeShow.setText("Defined Mode");
            if(definedModeTypeSwitch.isChecked()){
                if(!checked){
                    if(maximumDecimalOffset.getText().toString().equals("")) {
                        alertMessage.setText("Enter a maximum offset value");
                        return;
                    }
                    if(minimumDecimalOffset.getText().toString().equals("")){
                        minimumDecimalOffsetValue = 0;
                    }
                    else{
                        minimumDecimalOffsetValue = Integer.parseInt(minimumDecimalOffset.getText().toString());
                    }
                    alertMessage.setText("");
                    maximumDecimalOffsetValue = Integer.parseInt(maximumDecimalOffset.getText().toString());
                    checked = true;
                }
                maximumDecimalOffsetValue = --maximumDecimalOffsetValue;
                if(maximumDecimalOffsetValue < minimumDecimalOffsetValue){
                    minimumDecimalOffsetValue = 0;
                    maximumDecimalOffsetValue = 0;
                    checked = false;
                    return;
                }
                toEncode = String.valueOf(maximumDecimalOffsetValue);
            }
            else{
                if(!checked){
                    if(minimumDecimalOffset.getText().toString().equals("")){
                        alertMessage.setText("Enter a minimum offset value");
                        return;
                    }

                    if(!maximumDecimalOffset.getText().toString().equals("")){
                        maximumDecimalOffsetValue = Integer.parseInt(maximumDecimalOffset.getText().toString());
                    }
                    alertMessage.setText("");
                    minimumDecimalOffsetValue = Integer.parseInt(minimumDecimalOffset.getText().toString());
                    checked = true;
                }
                minimumDecimalOffsetValue = ++minimumDecimalOffsetValue;
                if(minimumDecimalOffsetValue > maximumDecimalOffsetValue && maximumDecimalOffsetValue != 0){
                    minimumDecimalOffsetValue = 0;
                    maximumDecimalOffsetValue = 0;
                    checked = false;
                    return;
                }
                toEncode = String.valueOf(minimumDecimalOffsetValue);
            }
        }

        if(switchBtn.isChecked() && !fuzzingType.isChecked()){
            checked = false;
            fuzzingType.setVisibility(View.VISIBLE);
            findViewById(R.id.textViewRandom).setVisibility(View.VISIBLE);
            findViewById(R.id.textViewDefined).setVisibility(View.VISIBLE);
            currentModeShow.setText("Random Mode");
            toEncode = RandomString.randomAlphaNumeric(stringLength);
        }
        if(!switchBtn.isChecked() && !fuzzingType.isChecked()){
            checked = false;
            fuzzingType.setVisibility(View.INVISIBLE);
            findViewById(R.id.textViewRandom).setVisibility(View.INVISIBLE);
            findViewById(R.id.textViewDefined).setVisibility(View.INVISIBLE);
            currentModeShow.setText("UUID Mode");
            toEncode = "teste";
        }
    }
}
