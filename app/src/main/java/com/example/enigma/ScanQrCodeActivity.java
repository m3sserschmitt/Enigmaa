package com.example.enigma;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.enigma.communications.MessagingService;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Contact;
import com.example.enigma.database.ContactDao;
import com.example.enigma.database.Node;
import com.example.enigma.database.NodeDao;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQrCodeActivity extends AppCompatActivity implements
        ZXingScannerView.ResultHandler{

    private ZXingScannerView qrCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        qrCodeScanner = findViewById(R.id.qr_code_scanner);
        setScannerProperties();
    }

    private void setScannerProperties()
    {
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);

        qrCodeScanner.setFormats(formats);
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.viewfinder_laser);
        qrCodeScanner.setMaskColor(R.color.viewfinder_laser);
        qrCodeScanner.setResultHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeScanner.stopCameraPreview();
        qrCodeScanner.stopCamera();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        qrCodeScanner.stopCameraPreview();
        qrCodeScanner.stopCamera();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if(isGranted)
                {
                    qrCodeScanner.startCamera();
                }
            });

    @Override
    public void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
            qrCodeScanner.startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        MessagingService.setNoSessionOnFocus();
    }

    private void insertData(String scannedData, String name)
    {
        try {
            JSONObject jsonObject = new JSONObject(scannedData);

            String address = jsonObject.getString("address");
            String guardAddress = jsonObject.getString("guardAddress");
            String sessionId = jsonObject.getString("sessionId");
            String sessionKey = jsonObject.getString("sessionKey");

            Handler handler = new Handler(Looper.getMainLooper());

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase appDatabaseInstance = AppDatabase.getInstance(this);

                NodeDao nodeDao = appDatabaseInstance.nodeDao();
                ContactDao contactDao = appDatabaseInstance.contactDao();

                Contact contact = new Contact(address, sessionId, sessionKey, guardAddress, name);
                Node node = new Node();

                node.setAddress(address);

                final Contact contactControl = contactDao.findByAddress(address);

                if(contactControl == null)
                {
                    nodeDao.insertAll(node);
                    contactDao.insertAll(contact);
                } else {
                    contact.setId(contactControl.getId());
                    contactDao.update(contact);
                }

                handler.post(() -> {
                    if(contactControl == null)
                    {
                        Toast.makeText(this, "New contact added: " + address,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Existing contact updated",
                                Toast.LENGTH_LONG).show();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Errors occurred",
                    Toast.LENGTH_LONG).show();
        }finally {
            finish();
        }
    }

    @Override
    public void handleResult(Result result) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Enter name for new contact");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            String scannedData = result.getText();
            insertData(scannedData, input.getText().toString());
        });

        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            finish();
        });

        alertDialogBuilder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}