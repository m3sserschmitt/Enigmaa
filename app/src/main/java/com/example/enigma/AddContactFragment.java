package com.example.enigma;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Contract;
import org.json.JSONObject;

import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import com.example.enigma.communications.MessagingService;
import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Contact;
import com.example.enigma.database.ContactDao;
import com.example.enigma.database.Node;
import com.example.enigma.databinding.FragmentAddContactBinding;
import com.example.enigma.qr.QrCodeHelper;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class AddContactFragment extends Fragment {

    private FragmentAddContactBinding binding;

    private ExportedContactData exportedContactData;

    public AddContactFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddContactBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle bundle)
    {
        generateQrCodeFromExportedData();

        binding.scanButton.setOnClickListener(v -> onScanButtonPressed());

        binding.saveExportedButton.setOnClickListener(v -> onSaveExportedButtonPressed());

        MessagingService.setNoSessionOnFocus();
    }

    private class ExportedContactData {
        private static final int sessionKeySize = 32;
        private static final int sessionIdSize = 16;

        private String guardAddress;

        private String sessionKey;

        private String sessionId;

        private String address;

        public ExportedContactData(String publicKeyFile)
        {
            createExportedContactData(publicKeyFile);
        }

        @NonNull
        @Contract("_ -> new")
        private String generateSequence(int size)
        {
//            Random generator = new Random();
//            byte[] data = new byte[size];
//            generator.nextBytes(data);

            // for tests only
            String sequence = "11111111111111111111111111111111111111111111111111";
            byte[] data = sequence.subSequence(0, size).toString().getBytes(StandardCharsets.UTF_8);

            return new String(Base64.encode(data, Base64.DEFAULT)).trim();
        }

        public void createExportedContactData(String publicKeyFile)
        {
            OnionServices onionServices = OnionServices.getInstance();
            String publicKey = FileUtils.getInstance(requireActivity()).readFile(publicKeyFile);
            address = onionServices.getAddressFromPublicKey(publicKey);
            guardAddress = requireActivity()
                    .getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
                    .getString("guardAddress", null);
            sessionKey = generateSequence(sessionKeySize);
            sessionId = generateSequence(sessionIdSize);
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        @NonNull
        @Override
        public String toString()
        {
            JSONObject data = new JSONObject();

            try {
                data.put("address", address);
                data.put("guardAddress", guardAddress);
                data.put("sessionId", sessionId);
                data.put("sessionKey", sessionKey);
            } catch (Exception e)
            {
                e.printStackTrace();
                return "";
            }

            return data.toString();
        }
    }

    @NonNull
    private String exportData()
    {
        exportedContactData = new ExportedContactData("public.pem");

        return exportedContactData.toString();
    }

    private void saveExported(String contactName)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase databaseInstance = AppDatabase.getInstance(requireActivity());
            ContactDao contactDao = databaseInstance.contactDao();

            Contact contact = new Contact(OnionServices.getDefaultAddress(),
                    exportedContactData.getSessionId(),
                    exportedContactData.getSessionKey(),
                    OnionServices.getDefaultAddress(),
                    contactName);

            contactDao.insertAll(contact);

            OnionServices.getInstance().loadContact(contact.getAddress(), contact.getDecodedSessionId(), contact.getDecodedSessionKey());

            handler.post(() -> {
               Toast.makeText(requireActivity(), "Session saved", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void onScanButtonPressed()
    {
        Intent scanQrCodeActivityIntent = new Intent(getContext(), ScanQrCodeActivity.class);
        startActivity(scanQrCodeActivityIntent);
    }

    private void onSaveExportedButtonPressed()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setTitle("Enter name for new contact");

        EditText input = new EditText(requireActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            saveExported(input.getText().toString());
        });

        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        alertDialogBuilder.show();
    }

    private void generateQrCodeFromExportedData(){

        Handler handler = new Handler(Looper.getMainLooper());

        Executors.newSingleThreadExecutor().execute(() -> {
            final String exportedData = exportData();
            final Bitmap qrCodeBitmap = QrCodeHelper
                        .newInstance(requireActivity())
                        .setContent(exportedData)
                        .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                        .getQRCOde();

            handler.post(() -> {
                if(qrCodeBitmap != null)
                {
                     binding.qrCodeImageView.setImageBitmap(qrCodeBitmap);
                } else {
                    Toast.makeText(requireActivity(), "Cannot generate QR code",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}