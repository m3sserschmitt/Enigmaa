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

public class OnionServices {

    static {
        System.loadLibrary("enigma");
    }

    private static final String defaultAddress =
            "0000000000000000000000000000000000000000000000000000000000000000";

    private static OnionServices instance;

    public static OnionServices getInstance() {

        if(instance == null)
        {
            instance = new OnionServices();
        }

        return instance;
    }

    private OnionServices() {}

    public static String getDefaultAddress() { return defaultAddress; }

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
