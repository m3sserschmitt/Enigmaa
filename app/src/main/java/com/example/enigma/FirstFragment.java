package com.example.enigma;

import android.content.Context;
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

import com.example.enigma.databinding.FragmentFirstBinding;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private FragmentActivity activity;

    private final int keySize = 4096;

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
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
        /*
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
            final int status = generatePrivateKey(publicKeyPath, privateKeyPath, keySize,
                    false, passphrase);

            handler.post(() -> {

                this.enableInputsOnGenerateKeysDone();

                if(status == 0)
                {
                    InitialSetupActivity.resultData.putExtra("publicKey", publicKeyPath);
                    InitialSetupActivity.resultData.putExtra("privateKey", privateKeyPath);

                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                } else {
                    Toast.makeText(activity, "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });*/
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        activity = Objects.requireNonNull(getActivity());

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

    public native int generatePrivateKey(String publicKeyFile, String privateKeyFile, int bits, boolean encrypt, String passphrase);
}