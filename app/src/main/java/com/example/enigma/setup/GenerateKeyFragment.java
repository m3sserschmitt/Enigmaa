package com.example.enigma.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.example.enigma.OnionServices;
import com.example.enigma.R;
import com.example.enigma.databinding.FragmentGenerateKeyBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenerateKeyFragment extends Fragment {

    private FragmentGenerateKeyBinding binding;
    private FragmentActivity activity;

    private final int keySize = 2048;

    private void closeKeyboardOnGenerateKeys()
    {
        View view = activity.getCurrentFocus();

        if(view != null)
        {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);

            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void disableInputsOnGenerateKeys()
    {
        binding.generateKeyButton.setEnabled(false);
        binding.passwordEditText.setEnabled(false);
    }

    private void enableInputsOnGenerateKeysDone()
    {
        binding.generateKeyButton.setEnabled(true);
        binding.passwordEditText.setEnabled(true);
    }

    public void generateKeys()
    {
        this.closeKeyboardOnGenerateKeys();

        final String filesDirectory = activity.getFilesDir().toString();

        final String publicKeyPath = filesDirectory + "/public.pem";
        final String privateKeyPath = filesDirectory + "/private.pem";
        final String passphrase = binding.passwordEditText.getText().toString();

        this.disableInputsOnGenerateKeys();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            // Todo: apply encryption over private key;
            OnionServices onionServices = OnionServices.getInstance();
            final int status = onionServices.generatePrivateKey(publicKeyPath, privateKeyPath, keySize,
                    false, passphrase);

            handler.post(() -> {
                this.enableInputsOnGenerateKeysDone();

                if(status == 0)
                {
                    SharedPreferences.Editor editor = activity.getSharedPreferences(
                            getString(R.string.shared_preferences), Context.MODE_PRIVATE).edit();

                    editor.putString("publicKey", publicKeyPath);
                    editor.putString("privateKey", privateKeyPath);

                    editor.apply();

                    NavHostFragment.findNavController(GenerateKeyFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                } else {
                    Toast.makeText(activity, "Error while generating private key",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        activity = requireActivity();
        binding = FragmentGenerateKeyBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.generateKeyButton.setOnClickListener(v -> generateKeys());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
