//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Speech-TTS
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.rgk.android.translator.microsoft.tts;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Environment;

import com.rgk.android.translator.tts.ITTSListener;
import com.rgk.android.translator.utils.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Synthesizer {

    private static final String TAG = "wuwu";
    private Voice m_serviceVoice;
    private Voice m_localVoice;
    private static Synthesizer mInstance;
    public String m_audioOutputFormat = AudioOutputFormat.Raw16Khz16BitMonoPcm;
    ITTSListener mListener;
    private AudioTrack audioTrack;
    private int bufferSizeInBytes;

    private void playSound(final byte[] sound, final String name, final String path, final ITTSListener listener, final Runnable callback) {
        Logger.d(TAG,"playSound");
        mListener = listener;
        if (sound == null || sound.length == 0) {
            mListener.onTTSEvent(MSConstants.STATE_SPEAK_ERROR);
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                writeBytesToFile(sound,name,path);
                final int SAMPLE_RATE = 16000;
                bufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);
                if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                    audioTrack.play();
                    audioTrack.write(sound, 0, sound.length);
                    audioTrack.stop();
                    audioTrack.release();
                }
                if (callback != null) {
                    callback.run();
                }
            }
        });

    }

    //stop playing audio data
    // if use STREAM mode, will wait for the end of the last write buffer data will stop.
    // if you stop immediately, call the pause() method and then call the flush() method to discard the data that has not yet been played
    public void stopSound() {
        try {
            if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                audioTrack.pause();
                audioTrack.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (m_ttsServiceClient != null) {
            m_ttsServiceClient.release();
        }
    }

    public String synthesizeToUri(String text, String fileName, String filePath,ITTSListener listener) {
        this.mListener = listener;
        return writeBytesToFile(speak(text),fileName,filePath);
    }

    public static Synthesizer getInstance(String apiKey) {
        if (mInstance == null) {
            synchronized (Synthesizer.class) {
                if (mInstance == null) {
                    mInstance = new Synthesizer(apiKey);
                }
            }
        }
        return mInstance;
    }

    public enum ServiceStrategy {
        AlwaysService//, WiFiOnly, WiFi3G4GOnly, NoService
    }

    private Synthesizer(String apiKey) {
        m_serviceVoice = new Voice("en-US");
        m_localVoice = null;
        m_eServiceStrategy = ServiceStrategy.AlwaysService;
        m_ttsServiceClient = new TtsServiceClient(apiKey);
    }

    public void setVoice(Voice serviceVoice, Voice localVoice) {
        m_serviceVoice = serviceVoice;
        m_localVoice = localVoice;
    }

    public void setServiceStrategy(ServiceStrategy eServiceStrategy) {
        m_eServiceStrategy = eServiceStrategy;
    }

    public byte[] speak(String text) {
        String ssml = "<speak version='1.0' xml:lang='" + m_serviceVoice.lang + "'><voice xml:lang='" + m_serviceVoice.lang + "' xml:gender='" + m_serviceVoice.gender + "'";
        if (m_eServiceStrategy == ServiceStrategy.AlwaysService) {
            if (m_serviceVoice.voiceName.length() > 0) {
                ssml += " name='" + m_serviceVoice.voiceName + "'>";
            } else {
                ssml += ">";
            }
            ssml +=  text + "</voice></speak>";
        }
        return speakSSML(ssml);
    }

    public void speakToAudio(String text, String name, String path, ITTSListener mListener,Runnable callback) {
        playSound(speak(text), name, path, mListener, callback);
    }

    public void speakSSMLToAudio(String ssml,String name,String path,ITTSListener mListener,Runnable callback) {
        playSound(speakSSML(ssml), name, path, mListener, callback);
    }

    public byte[] speakSSML(String ssml) {
        byte[] result = null;
        /*
         * check current network environment
         * to do...
         */
        if (m_eServiceStrategy == ServiceStrategy.AlwaysService) {
            result = m_ttsServiceClient.speakSSML(ssml);
            if (result == null || result.length == 0) {
                return null;
            }

        }
        return result;
    }

    private String writeBytesToFile(byte[] bytes,String name,String path) {
        File sampleDir = Environment.getExternalStorageDirectory();
        if (!sampleDir.canWrite()) {
            sampleDir = new File("/sdcard");
        }
        sampleDir = new File(sampleDir.getAbsolutePath() + "/" + path);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        File file = new File(sampleDir.getAbsolutePath() + "/" + name + ".mav");
        try {
            OutputStream out = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff,0,len);
            }
            is.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            if (mListener != null) {
                mListener.onTTSEvent(MSConstants.STATE_SYN_ERROR);
            }
            e.printStackTrace();
        }
        if (mListener != null) {
            mListener.onTTSEvent(MSConstants.STATE_SYN_SUCCESS);
        }
        return file.getAbsolutePath();
    }

    private TtsServiceClient m_ttsServiceClient;
    private ServiceStrategy m_eServiceStrategy;
}
