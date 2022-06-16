package com.example.enigma;

public class OnionServices {

    static {
        System.loadLibrary("enigma");
    }

    private static OnionServices instance;

    public static OnionServices getInstance() {

        if(instance == null)
        {
            instance = new OnionServices();
        }

        return instance;
    }

    private OnionServices() {}

    public native int generatePrivateKey(String publicKeyFile, String privateKeyFile, int bits,
                                         boolean encrypt, String passphrase);

    public native String getAddressFromPublicKey(String publicKey);

    public native String getClientGuardAddress();

    public native String loadNode(String lastAddress, String publicKey, boolean generateSessionId);

    public native int loadLastNodeInCircuit(String address, String lastAddress, byte[] sessionId, byte[] sessionKey);

    public native boolean circuitLoaded(String destination);

    public native int sendMessage(String message, String destination);

    public native boolean initializeClient(String publicKeyPEM, String privateKeyPEM, boolean useTls);

    public native String openConnection(String hostname, String port, String guardPublicKeyPEM);

    public native boolean checkNewMessage();

    public native int loadContact(String address, byte[] sessionId, byte[] sessionKey);

    public native String readLastSessionId();

    public native byte[] readLastMessage();
}
