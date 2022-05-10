#include <jni.h>
#include <string>
#include <cstring>

#include "./enigma4/libcryptography/include/cryptography/rsa.hh"
#include "./enigma4/libenigma4-client/include/enigma4-client/enigma4_client.hh"

using namespace std;

static Client *enigma4Client = nullptr;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_enigma_GenerateKeyFragment_generatePrivateKey(
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
JNIEXPORT jstring JNICALL
Java_com_example_enigma_communications_MessagingService_initializeClient(
        JNIEnv *env,
        jobject thiz,
        jstring publicKey,
        jstring privateKey,
        jstring hostname,
        jstring port,
        jboolean useTls,
        jstring guardPublicKey) {

    const char *publicKeyPEM = env->GetStringUTFChars(publicKey, nullptr);
    const char *privateKeyPEM = env->GetStringUTFChars(privateKey, nullptr);
    const char *serverPublicKeyPEM = env->GetStringUTFChars(guardPublicKey, nullptr);
    const char *host = env->GetStringUTFChars(hostname, nullptr);
    const char *portNumber = env->GetStringUTFChars(port, nullptr);

    delete enigma4Client;

    if(useTls)
    {
        enigma4Client = new TlsClient();
    } else{
        enigma4Client = new Client();
    }

    if(not enigma4Client)
    {
        return nullptr;
    }

    if(enigma4Client->setClientPublicKeyPEM(publicKeyPEM) < 0)
    {
        return nullptr;
    }

    if(enigma4Client->loadClientPrivateKeyPEM(privateKeyPEM) < 0)
    {
        return nullptr;
    }

    if (enigma4Client->createConnection(host, portNumber, serverPublicKeyPEM) < 0)
    {
        return nullptr;
    }

    string guardAddress = enigma4Client->getGuardAddress();

    return env->NewStringUTF(guardAddress.c_str());
}
