/**
 * 
 */
package com.gauss.speex.recorder;

import java.io.File;

import com.gauss.speex.encode.SpeexDecoder;

/**
 * @author Gauss
 * 
 */
public class SpeexPlayer {
	private String fileName = null;
	private SpeexDecoder speexdec = null;
	private static Thread th;
	private static boolean stop_flag;

	public SpeexPlayer(String fileName) {
		stop_flag = false;
		this.fileName = fileName;
		System.out.println(this.fileName);
		try {
			speexdec = new SpeexDecoder(new File(this.fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPlay() {
		RecordPlayThread rpt = new RecordPlayThread();
		th = new Thread(rpt);
		stop_flag = false;
		th.start();
	}

	public void endPlay() {

		if (th != null) {
			// th.stop();
			th.interrupt();
			// th.stop();
			th = null;
		}

	}

	public boolean isAlive() {
		if (th != null) {
			return th.isAlive();
		} else {
			return false;
		}
		// return true;
	}

	boolean isPlay = true;

	class RecordPlayThread extends Thread {
		public void run() {
			try {

				if (speexdec != null)
					speexdec.decode();
			} catch (Exception t) {
				t.printStackTrace();
			}
		}
	};
}
