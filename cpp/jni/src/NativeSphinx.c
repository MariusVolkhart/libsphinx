#include <jni.h>
#include <sphinx.h>

const int COMMIT_AND_FREE = 0;

JNIEXPORT void JNICALL
Java_com_volkhart_sphinx_NativeSphinx_nativeChallenge(JNIEnv* env,
                                                    jobject ignore,
                                                    jbyteArray jMasterPassword,
                                                    jbyteArray jBlindingFactor,
                                                    jbyteArray jChallenge
) {
  uint8_t* masterPassword = (uint8_t*) (*env)->GetByteArrayElements(env, jMasterPassword, NULL);
  size_t passwordLength = (size_t) (*env)->GetArrayLength(env, jMasterPassword);
  uint8_t* blindingFactor = (uint8_t*) (*env)->GetByteArrayElements(env, jBlindingFactor, NULL);
  uint8_t* challenge = (uint8_t*) (*env)->GetByteArrayElements(env, jChallenge, NULL);

  sphinx_challenge(masterPassword, passwordLength, blindingFactor, challenge);

  // Release the input values
  (*env)->ReleaseByteArrayElements(env, jChallenge, (jbyte*) challenge, COMMIT_AND_FREE);
  (*env)->ReleaseByteArrayElements(env, jBlindingFactor, (jbyte*) blindingFactor, COMMIT_AND_FREE);
  (*env)->ReleaseByteArrayElements(env, jMasterPassword, (jbyte*) masterPassword, JNI_ABORT);
}

JNIEXPORT jint JNICALL
Java_com_volkhart_sphinx_NativeSphinx_nativeRespond(JNIEnv* env,
                                                  jobject ignore,
                                                  jbyteArray jChallenge,
                                                  jbyteArray jSecret,
                                                  jbyteArray jResponse
) {
  uint8_t* challenge = (uint8_t*) (*env)->GetByteArrayElements(env, jChallenge, NULL);
  uint8_t* secret = (uint8_t*) (*env)->GetByteArrayElements(env, jSecret, NULL);
  uint8_t* response = (uint8_t*) (*env)->GetByteArrayElements(env, jResponse, NULL);

  int result = sphinx_respond(challenge, secret, response);

  // Release the input values
  (*env)->ReleaseByteArrayElements(env, jResponse, (jbyte*) response, COMMIT_AND_FREE);
  (*env)->ReleaseByteArrayElements(env, jSecret, (jbyte*) secret, JNI_ABORT);
  (*env)->ReleaseByteArrayElements(env, jChallenge, (jbyte*) challenge, JNI_ABORT);

  return result;
}

JNIEXPORT jint JNICALL
Java_com_volkhart_sphinx_NativeSphinx_nativeFinish(JNIEnv* env,
                                                 jobject ignore,
                                                 jbyteArray jMasterPassword,
                                                 jbyteArray jBlindingFactor,
                                                 jbyteArray jResponse,
                                                 jbyteArray jRwd
) {
  uint8_t* masterPassword = (uint8_t*) (*env)->GetByteArrayElements(env, jMasterPassword, NULL);
  size_t passwordLength = (size_t) (*env)->GetArrayLength(env, jMasterPassword);
  uint8_t* blindingFactor = (uint8_t*) (*env)->GetByteArrayElements(env, jBlindingFactor, NULL);
  uint8_t* response = (uint8_t*) (*env)->GetByteArrayElements(env, jResponse, NULL);
  uint8_t* rwd = (uint8_t*) (*env)->GetByteArrayElements(env, jRwd, NULL);

  int result = sphinx_finish(masterPassword, passwordLength, blindingFactor, response, rwd);

  // Release the input values
  (*env)->ReleaseByteArrayElements(env, jRwd, (jbyte*) rwd, COMMIT_AND_FREE);
  (*env)->ReleaseByteArrayElements(env, jResponse, (jbyte*) response, JNI_ABORT);
  (*env)->ReleaseByteArrayElements(env, jBlindingFactor, (jbyte*) blindingFactor, JNI_ABORT);
  (*env)->ReleaseByteArrayElements(env, jMasterPassword, (jbyte*) masterPassword, JNI_ABORT);

  return result;
}