package com.example.enigma;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Contract;
import org.json.JSONObject;

import android.util.Base64;
import android.widget.Toast;

import com.example.enigma.communications.MessagingService;
import com.example.enigma.databinding.FragmentAddContactBinding;
import com.example.enigma.qr.QrCodeHelper;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class AddContactFragment extends Fragment {

    private static final int sessionKeySize = 32;
    private static final int sessionIdSize = 16;

    private FragmentAddContactBinding binding;

    public AddContactFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddContactBinding.inflate(inflater, container, false);

        binding.scanButton.setOnClickListener(v -> {
            Intent scanQrCodeActivityIntent = new Intent(getContext(), ScanQrCodeActivity.class);
            startActivity(scanQrCodeActivityIntent);
        });

        MessagingService.setNoSessionOnFocus();

        return binding.getRoot();
    }

    @NonNull
    @Contract("_ -> new")
    private String generateSequence(int size)
    {
//        Random generator = new Random();
//        byte[] data = new byte[size];
//        generator.nextBytes(data);

        String sequence = "11111111111111111111111111111111111111111111111111";
        byte[] data = sequence.subSequence(0, size).toString().getBytes(StandardCharsets.UTF_8);

        return new String(Base64.encode(data, Base64.NO_CLOSE)).trim();
    }

    @Nullable
    private String exportData()
    {
        String publicKey = FileUtils.getInstance(requireActivity()).readFile("public.pem");
        String sessionKey = generateSequence(sessionKeySize);
        String sessionId = generateSequence(sessionIdSize);
        String guardAddress = requireActivity()
                .getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
                .getString("guardAddress", null);

        JSONObject data = new JSONObject();

        try {
            data.put("address", getLocalAddressFromPublicKey(publicKey));
            data.put("guardAddress", guardAddress);
            data.put("sessionId", sessionId);
            data.put("sessionKey", sessionKey);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return data.toString();
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle bundle)
    {
        generateQrCodeFromExportedData();
    }

    private native String getLocalAddressFromPublicKey(String publicKey);
}