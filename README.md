Before building the application in Android Studio you have to complete the following steps in order to compile OpenSSL library.

Clone repository and prepare [OpenSSL](https://www.openssl.org/) library for compilation:

```
git clone https://github.com/m3sserschmitt/enigma-app.git --recursive 
cd <local-repository-path>/app/src/main/cpp/
```

where `<local-repository-path>` is the location of cloned repository on your local machine.

Prepare OpenSSL for compilation:

```
export ANDROID_NDK_HOME=<ndk-path>
PATH=<toolchain-path>/bin:$PATH
```

where,

`<ndk-path>` is the location of Android NDK.

`<toolchain-path>` has the form `<ndk-path>/toolchains/llvm/prebuilt/<platform>` where `<platform>` is one of the following: `linux-x86_64`, `darwin-x86_64`, `windows-x86_64` or `windows`, depending on your system.

Compile OpenSSL:

```
chmod +x compile-openssl.sh
./compile-openssl.sh
```

`compile-openssl.sh` script will download, extract and compile the source code for OpenSSL library for `armeabi-v7a`, `arm64-v8a`, `x86` and `x86_64`. Resulted binaries are located into `openssl-bin` directory.


