/*  Enigma - Onion Routing based messaging app.
    Copyright (C) 2022  Romulus-Emanuel Ruja <romulus-emanuel.ruja@tutanota.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
import com.example.enigma.database.Edge;
import com.example.enigma.database.EdgeDao;
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
                EdgeDao edgeDao = appDatabaseInstance.edgeDao();

                Contact contact = new Contact(address, sessionId, sessionKey, guardAddress, name);
                Node node = new Node(address, null, null);
                Edge edge1 = new Edge(guardAddress, address);
                Edge edge2 = new Edge(address, guardAddress);

                contactDao.delete(contact);
                nodeDao.delete(node);
                edgeDao.deleteEdges(address);

                contactDao.insertAll(contact);
                nodeDao.insertAll(node);
                edgeDao.insertAll(edge1, edge2);

                OnionServices.getInstance().loadContact(contact.getAddress(),
                        contact.getDecodedSessionId(), contact.getDecodedSessionKey());

                handler.post(() -> {
                    Toast.makeText(this, "New contact added", Toast.LENGTH_SHORT).show();
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