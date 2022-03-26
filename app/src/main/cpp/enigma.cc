#include <jni.h>
#include <string>
#include <string.h>

#include "./enigma4/libcryptography/include/cryptography/rsa.hh"
#include "./enigma4/libenigma4-client/include/enigma4-client/enigma4_client.hh"

using namespace std;

static Client *enigma4Client = nullptr;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_enigma_FirstFragment_generatePrivateKey(
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
JNIEXPORT jint JNICALL
Java_com_example_enigma_MainActivity_initializeClient(
        JNIEnv *env,
        jobject thiz,
        jstring publicKeyPath,
        jstring privateKeyPath,
        jstring port,
        jstring hostname,
        jboolean useTls,
        jstring serverPublicKeyPath) {

    string publicKey = env->GetStringUTFChars(publicKeyPath, nullptr);
    string privateKey = env->GetStringUTFChars(privateKeyPath, nullptr);
    string serverPublicKey = env->GetStringUTFChars(serverPublicKeyPath, nullptr);
    string host = env->GetStringUTFChars(hostname, nullptr);
    string portNumber = env->GetStringUTFChars(port, nullptr);

    if(useTls)
    {
        enigma4Client = new TlsClient(publicKey, privateKey);
    } else {
        enigma4Client = new Client(publicKey, privateKey);
    }

    return enigma4Client->createConnection(host, portNumber, serverPublicKey);
}