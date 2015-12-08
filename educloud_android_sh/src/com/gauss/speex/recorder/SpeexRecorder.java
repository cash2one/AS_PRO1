package com.gauss.speex.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.gauss.speex.encode.SpeexEncoder;
import com.linkage.lib.util.LogUtils;

public class SpeexRecorder implements Runnable {

//	private Logger log = LoggerFactory.getLogger(SpeexRecorder.class);
	private volatile boolean forbiden;
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static int packagesize = 160;
	public int value;
	private String fileName = null;
	private SpeexEncoder encoder;
	public SpeexRecorder(String fileName) {
		super();
		this.fileName = fileName;
	}

	public void run() {

	    encoder = new SpeexEncoder(this.fileName);
		Thread encodeThread = new Thread(encoder);
		encoder.setRecording(true);
		encodeThread.start();

		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding);

		short[] tempBuffer = new short[packagesize];

		AudioRecord recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding,
				bufferSize);
		
		try{
			recordInstance.startRecording();
		}catch(Exception e){
			e.printStackTrace();
			setForbiden(true);
			return;
		}

		while (this.isRecording) {
//			log.debug("start to recording.........");
//			 LogUtils.e("","==aaa1==");
			bufferRead = recordInstance.read(tempBuffer, 0, packagesize);
			
//			 LogUtils.e("","==aaa2==");
			// bufferRead = recordInstance.read(tempBuffer, 0, 320);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				setForbiden(true);
				return;
				/*throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");*/
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				setForbiden(true);
				return;
				/*throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");*/
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				setForbiden(true);
				return;
				/*throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");*/
			}
//			 LogUtils.e("","==aaa3==");
//			log.debug("put data into encoder collector....");
			encoder.putData(tempBuffer, bufferRead);
//			 LogUtils.e("","==aaa4==");
			int v=0;
		    for (int i = 0; i < tempBuffer.length; i+=2) {
		        v += tempBuffer[i] * tempBuffer[i];
		    }
//		    LogUtils.e("","==aaa5==");
		     value = (int) (Math.abs((int)(v /(float)bufferRead/2)/10000) >> 1)/10+1;
//		    LogUtils.e("","===="+value);
		     
		}
		LogUtils.e("===="+"tell encoder to start");
		recordInstance.stop();
		//tell encoder to stop.
		recordInstance.release(); 
		LogUtils.e("===="+"tell encoder to stop");
		encoder.setRecording(false);
		recordInstance=null;
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (!this.isRecording) {
				if(encoder!=null)
				{
					this.encoder.setRecording(false);
				}
			}
				mutex.notify();
//			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}

	public boolean isForbiden() {
		return forbiden;
	}

	public void setForbiden(boolean forbiden) {
		this.forbiden = forbiden;
	}
	
}
