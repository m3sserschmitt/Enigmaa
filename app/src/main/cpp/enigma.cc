#include <jni.h>
#include <string>
#include <cstring>

#include "./enigma4/libcryptography/include/cryptography/rsa.hh"
#include "./enigma4/libenigma4-client/include/enigma4-client/enigma4_client.hh"

using namespace std;

static Client *enigma4Client = nullptr;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_enigma_setup_GenerateKeyFragment_generatePrivateKey(
        JNIEnv *env,jobject
        thiz,jstring publicKeyFile,
        jstring privateKeyFile,
        jint bits,
        jboolean encrypt,
        jstring passphrase) {

    string publicKeyPath = env->GetStringUTFChars(publicKeyFile, nullptr);
    string privateKeyPath = env->GetStringUTFChars(privateKeyFile, nullptr);

    unsigned char *encryptPassphrase = nullptr;
    int passlen = 0;

    if(encrypt)
    {
        encryptPassphrase = (unsigned char *)env->GetStringUTFChars(passphrase, nullptr);
        passlen = strlen((char *)encryptPassphrase);
    }

    return CRYPTO::RSA_generate_keys(publicKeyPath, privateKeyPath, bits, encrypt,
                                     encryptPassphrase, passlen,
                                     nullptr);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_enigma_communications_MessagingService_nativeInitializeClient(
        JNIEnv *env,
        jobject thiz,
        jstring publicKey,
        jstring privateKey,
        jboolean useTls) {

    delete enigma4Client;

    const char *publicKeyPEM = env->GetStringUTFChars(publicKey, nullptr);
    const char *privateKeyPEM = env->GetStringUTFChars(privateKey, nullptr);

    if(useTls)
    {
        enigma4Client = new TlsClient();
    } else{
        enigma4Client = new Client();
    }

    if(not enigma4Client)
    {
        return false;
    }

    if(enigma4Client->setClientPublicKeyPEM(publicKeyPEM) < 0)
    {
        return false;
    }

    if(enigma4Client->loadClientPrivateKeyPEM(privateKeyPEM) < 0)
    {
        return false;
    }

    return true;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_enigma_communications_MessagingService_nativeOpenConnection(
        JNIEnv *env,
        jobject thiz,
        jstring hostname,
        jstring port,
        jstring guardPublicKeyPEM) {

    if(not enigma4Client)
    {
        return nullptr;
    }

    const char *serverPublicKey = env->GetStringUTFChars(guardPublicKeyPEM, nullptr);
    const char *host = env->GetStringUTFChars(hostname, nullptr);
    const char *portNumber = env->GetStringUTFChars(port, nullptr);

    if (enigma4Client->createConnection(host, portNumber, serverPublicKey) < 0)
    {
        return nullptr;
    }

    string guardAddress = enigma4Client->getGuardAddress();

    return env->NewStringUTF(guardAddress.c_str());
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_enigma_communications_MessagingService_nativeClientConnected(
        JNIEnv *env,
        jobject thiz) {

    if(not enigma4Client)
    {
        return false;
    }

    return enigma4Client->isConnected();
}

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_example_enigma_ScanQrCodeActivity_getContactAddressFromPublicKey(
//        JNIEnv *env,
//        jobject thiz,
//        jstring publicKeyPEM) {
//
//    const char *publicKey = env->GetStringUTFChars(publicKeyPEM, nullptr);
//
//    string address;
//    KEY_UTIL::getKeyHexDigest(publicKey, address);
//
//    return env->NewStringUTF(address.c_str());
//}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_enigma_ChatActivity_circuitLoaded(
        JNIEnv *env,
        jobject thiz,
        jstring destination) {

    if(not enigma4Client)
    {
        return false;
    }

    const char *destinationNode = env->GetStringUTFChars(destination, nullptr);

    return enigma4Client->circuitExists(destinationNode);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_enigma_ChatActivity_loadNode(
        JNIEnv *env,
        jobject thiz,
        jstring lastAddress,
        jstring publicKey,
        jboolean generateSessionId) {

    if(not enigma4Client)
    {
        return nullptr;
    }

    const char *lastNodeAddress = env->GetStringUTFChars(lastAddress, nullptr);
    const char *publicKeyPEM= env->GetStringUTFChars(publicKey, nullptr);

    string newAddress = enigma4Client->addNode(publicKeyPEM, lastNodeAddress, generateSessionId);

    return env->NewStringUTF(newAddress.c_str());
}

static unsigned char* as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    auto* buf = new unsigned char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_enigma_ChatActivity_loadLastNodeInCircuit(
        JNIEnv *env,
        jobject thiz,
        jstring address,
        jstring lastAddress,
        jbyteArray sessionId,
        jbyteArray sessionKey) {

    if(not enigma4Client)
    {
        return -1;
    }

    const char *destinationAddress = env->GetStringUTFChars(address, nullptr);
    const char *lastNodeAddress = env->GetStringUTFChars(lastAddress, nullptr);
    const unsigned char *id = as_unsigned_char_array(env, sessionId);
    const unsigned char *key = as_unsigned_char_array(env, sessionKey);

    return enigma4Client->addNode(destinationAddress, lastNodeAddress, id, key);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_enigma_AddContactFragment_getLocalAddressFromPublicKey(
        JNIEnv *env, jobject thiz,
        jstring publicKeyPEM) {

    const char *publicKey = env->GetStringUTFChars(publicKeyPEM, nullptr);

    string address;
    KEY_UTIL::getKeyHexDigest(publicKey, address);

    return env->NewStringUTF(address.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_enigma_ChatActivity_getClientGuardAddress(JNIEnv *env, jobject thiz) {
    if(not enigma4Client)
    {
        return nullptr;
    }

    return env->NewStringUTF(enigma4Client->getGuardAddress().c_str());
}