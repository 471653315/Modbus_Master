package cn.rxsi.comtest.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import cn.rxsi.comtest.util.ArrayUtils;
import cn.rxsi.comtest.util.ShowUtils;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * 
 * @author Rxsi
 */
@SuppressWarnings("all")
public class SerialPortManager {

	public static final ArrayList<String> findPorts() {

		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> portNameList = new ArrayList<String>();

		while (portList.hasMoreElements()) {
			String portName = portList.nextElement().getName();
			portNameList.add(portName);
		}
		return portNameList;
	}


	public static final SerialPort openPort(String portName, int baudrate) throws PortInUseException {
		try {

			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

			CommPort commPort = portIdentifier.open(portName, 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				try {
					
					serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e) {
					e.printStackTrace();
				}
				return serialPort;
			}
		} catch (NoSuchPortException e1) {
			e1.printStackTrace();
		}
		return null;
	}


	public static void closePort(SerialPort serialPort) {
		if (serialPort != null) {
			serialPort.close();
		}
	}


	public static void sendToPort(SerialPort serialPort, byte[] order) {
		OutputStream out = null;
		try {
			out = serialPort.getOutputStream();
			out.write(order);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static byte[] readFromPort(SerialPort serialPort) {
		InputStream in = null;
		byte[] bytes = {};
		try {
			in = serialPort.getInputStream();

			byte[] readBuffer = new byte[1];
			int bytesNum = in.read(readBuffer);
			while (bytesNum > 0) {
				bytes = ArrayUtils.concat(bytes, readBuffer);
				bytesNum = in.read(readBuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}


	public static void addListener(SerialPort serialPort, DataAvailableListener listener) {
		try {

			serialPort.addEventListener(new SerialPortListener(listener));

			serialPort.notifyOnDataAvailable(true);

			serialPort.notifyOnBreakInterrupt(true);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
	}


	public static class SerialPortListener implements SerialPortEventListener {

		private DataAvailableListener mDataAvailableListener;

		public SerialPortListener(DataAvailableListener mDataAvailableListener) {
			this.mDataAvailableListener = mDataAvailableListener;
		}

		public void serialEvent(SerialPortEvent serialPortEvent) {
			switch (serialPortEvent.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE: 
				if (mDataAvailableListener != null) {
					mDataAvailableListener.dataAvailable();
				}
				break;

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: 
				break;

			case SerialPortEvent.CTS: 
				break;

			case SerialPortEvent.DSR: 
				break;

			case SerialPortEvent.RI: 
				break;

			case SerialPortEvent.CD: 
				break;

			case SerialPortEvent.OE: 
				break;

			case SerialPortEvent.PE: 
				break;

			case SerialPortEvent.FE: 
				break;

			case SerialPortEvent.BI: 
				ShowUtils.errorMessage("与串口设备通讯中断");
				break;

			default:
				break;
			}
		}
	}


	public interface DataAvailableListener {

		void dataAvailable();
	}
}
