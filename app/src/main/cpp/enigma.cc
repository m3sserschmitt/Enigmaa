#include <jni.h>
#include <string>
#include <cstring>
#include <thread>

#include "./enigma4/libcryptography/include/cryptography/rsa.hh"
#include "./enigma4/libenigma4-client/include/enigma4-client/enigma4_client.hh"

using namespace std;

static Client *enigma4Client = nullptr;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_enigma_OnionServices_initializeClient(
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
Java_com_example_enigma_OnionServices_openConnection(
        JNIEnv *env,
        jobject thiz,
        jstring hostname,
        jstring port,
        jstring guardPublicKeyPEM) {

    if(not enigma4Client)
    {
        return nullptr;
    }

    string serverPublicKey = env->GetStringUTFChars(guardPublicKeyPEM, nullptr);
    string host = env->GetStringUTFChars(hostname, nullptr);
    string portNumber = env->GetStringUTFChars(port, nullptr);

    if (enigma4Client->createConnection(host, portNumber, serverPublicKey) < 0)
    {
        return nullptr;
    }

    string guardAddress = enigma4Client->getGuardAddress();

    return env->NewStringUTF(guardAddress.c_str());
}

static unsigned char* as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    auto* buf = new unsigned char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

extern "C"
JNIEXPORT int JNICALL
Java_com_example_enigma_OnionServices_loadContact(
        JNIEnv *env,
        jobject thiz,
        jstring address,
        jbyteArray sessionId,
        jbyteArray sessionKey) {

    if(not enigma4Client)
    {
        return -1;
    }

    string contactAddress = env->GetStringUTFChars(address, nullptr);
    unsigned char *id = as_unsigned_char_array(env, sessionId);
    unsigned char *key = as_unsigned_char_array(env, sessionKey);

    return enigma4Client->addSession(contactAddress, id, key);
}

static int lastDataSize;
static string lastSessionId;
static unsigned char *lastMessageContent = nullptr;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_enigma_OnionServices_checkNewMessage(
        JNIEnv *env,
        jobject thiz) {

    if(not enigma4Client)
    {
        return false;
    }

    delete[] lastMessageContent;
    lastMessageContent = nullptr;

    lastDataSize = enigma4Client->readData(&lastMessageContent, lastSessionId);
    return lastDataSize > 0;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_enigma_OnionServices_readLastSessionId(
        JNIEnv *env,
        jobject thiz) {

    return env->NewStringUTF(lastSessionId.c_str());
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_enigma_OnionServices_readLastMessage(
        JNIEnv *env,
        jobject thiz) {

    jbyteArray bytes = env->NewByteArray(lastDataSize);
    env->SetByteArrayRegion(bytes, 0, lastDataSize, (jbyte *)lastMessageContent);

    return bytes;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_enigma_OnionServices_generatePrivateKey(
        JNIEnv *env,
        jobject thiz,
        jstring publicKeyFile,
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
JNIEXPORT jstring JNICALL
Java_com_example_enigma_OnionServices_getAddressFromPublicKey(
        JNIEnv *env,
        jobject thiz,
        jstring publicKeyPEM){

    const char *publicKey = env->GetStringUTFChars(publicKeyPEM, nullptr);

    string address;
    KEY_UTIL::getKeyHexDigest(publicKey, address);

    return env->NewStringUTF(address.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_enigma_OnionServices_getClientGuardAddress(
        JNIEnv *env,
        jobject thiz) {

    if(not enigma4Client)
    {
        return nullptr;
    }

    return env->NewStringUTF(enigma4Client->getGuardAddress().c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_enigma_OnionServices_loadNode(
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

    string newAddress = enigma4Client->addNodeToCircuit(publicKeyPEM, lastNodeAddress, generateSessionId);

    return env->NewStringUTF(newAddress.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_enigma_OnionServices_loadLastNodeInCircuit(
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

    return enigma4Client->addNodeToCircuit(destinationAddress, lastNodeAddress, id, key);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_enigma_OnionServices_circuitLoaded(
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
JNIEXPORT jint JNICALL
Java_com_example_enigma_OnionServices_sendMessage(
        JNIEnv *env,
        jobject thiz,
        jstring message,
        jstring destination) {

    if(not enigma4Client)
    {
        return -1;
    }

    const char *text = env->GetStringUTFChars(message, nullptr);
    const char *destinationAddress = env->GetStringUTFChars(destination, nullptr);

    return enigma4Client->writeDataWithEncryption((unsigned char *)text, strlen(text), destinationAddress);
}