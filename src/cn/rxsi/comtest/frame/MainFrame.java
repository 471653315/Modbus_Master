package cn.rxsi.comtest.frame;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import cn.rxsi.comtest.manager.SerialPortManager;
import cn.rxsi.comtest.util.ByteUtils;
import cn.rxsi.comtest.util.CRCUtils;
import cn.rxsi.comtest.util.ShowUtils;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

/**
 * 
 * @author Rxsi
 * 
 */
@SuppressWarnings("all")
public class MainFrame extends JFrame {


	public final int WIDTH = 630;

	public final int HEIGHT = 490;
	
	public int i=0;
	CRCUtils crcUtils=new CRCUtils();


	private JTextArea mDataView = new JTextArea();
	private JScrollPane mScrollDataView = new JScrollPane(mDataView);
	private JTextField mField=new JTextField();
	

	private JPanel mSerialPortPanel = new JPanel();
	private JLabel mSerialPortLabel = new JLabel("串口");
	private JLabel mBaudrateLabel = new JLabel("波特率");
	private JLabel mTimesLabel = new JLabel("从站接收次数");
	private JComboBox mCommChoice = new JComboBox();
	private JComboBox mBaudrateChoice = new JComboBox();
	private ButtonGroup mDataChoice = new ButtonGroup();
	private JRadioButton mDataASCIIChoice = new JRadioButton("ASCII", true);
	private JRadioButton mDataHexChoice = new JRadioButton("Hex");


	private JPanel mOperatePanel = new JPanel();
	private JTextArea mDataInput = new JTextArea();
	private JButton mSerialPortOperate = new JButton("打开串口");
	private JButton mSendData = new JButton("发送数据");


	private List<String> mCommList = null;

	private SerialPort mSerialport;

	public MainFrame() {
		initView();
		initComponents();
		actionListener();
		initData();
	}


	private void initView() {
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	
		setResizable(false);

		
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		this.setLayout(null);

		setTitle("ModBus Master");
	}


	private void initComponents() {
		
		mDataView.setFocusable(false);
		mScrollDataView.setBounds(10, 10, 590, 200);
		add(mScrollDataView);

		
		mSerialPortPanel.setBorder(BorderFactory.createTitledBorder("串口设置"));
		mSerialPortPanel.setBounds(10, 220, 220, 200);
		mSerialPortPanel.setLayout(null);
		add(mSerialPortPanel);

		mSerialPortLabel.setForeground(Color.gray);
		mSerialPortLabel.setBounds(15, 20, 60, 60);
		mSerialPortPanel.add(mSerialPortLabel);

		mCommChoice.setFocusable(false);
		mCommChoice.setBounds(65, 40, 120, 25);
		mSerialPortPanel.add(mCommChoice);

		mBaudrateLabel.setForeground(Color.gray);
		mBaudrateLabel.setBounds(15, 50, 60, 60);
		mSerialPortPanel.add(mBaudrateLabel);

		mBaudrateChoice.setFocusable(false);
		mBaudrateChoice.setBounds(65, 67, 120, 25);
		mSerialPortPanel.add(mBaudrateChoice);

		mDataASCIIChoice.setBounds(20, 95, 55, 20);
		mDataHexChoice.setBounds(95, 95, 55, 20);
		mDataChoice.add(mDataASCIIChoice);
		mDataChoice.add(mDataHexChoice);
		mSerialPortPanel.add(mDataASCIIChoice);
		mSerialPortPanel.add(mDataHexChoice);
		
		mTimesLabel.setForeground(Color.gray);
		mTimesLabel.setBounds(15, 110, 90, 60);
		mSerialPortPanel.add(mTimesLabel);
		
		mField.setBounds(100, 130, 70, 20);
		mSerialPortPanel.add(mField);
		mField.setText("0");

	
		mOperatePanel.setBorder(BorderFactory.createTitledBorder("操作"));
		mOperatePanel.setBounds(250, 220, 370, 200);
		mOperatePanel.setLayout(null);
		add(mOperatePanel);

		mDataInput.setBounds(25, 25, 320, 100);
		mDataInput.setLineWrap(true);
		mDataInput.setWrapStyleWord(true);
		mOperatePanel.add(mDataInput);

		mSerialPortOperate.setFocusable(false);
		mSerialPortOperate.setBounds(45, 135, 90, 30);
		mOperatePanel.add(mSerialPortOperate);

		mSendData.setFocusable(false);
		mSendData.setBounds(180, 135, 90, 30);
		mOperatePanel.add(mSendData);
		
		/*mAutoSendData.setFocusable(false);
		mAutoSendData.setBounds(175, 95, 90, 20);
		mOperatePanel.add(mAutoSendData);*/
	}


	private void initData() {
		mCommList = SerialPortManager.findPorts();

		if (mCommList == null || mCommList.size() < 1) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : mCommList) {
				mCommChoice.addItem(s);
			}
		}

		mBaudrateChoice.addItem("9600");
		mBaudrateChoice.addItem("19200");
		mBaudrateChoice.addItem("38400");
		mBaudrateChoice.addItem("57600");
		mBaudrateChoice.addItem("115200");
	}

	
	private void actionListener() {
		
		mCommChoice.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				mCommList = SerialPortManager.findPorts();
				
				if (mCommList == null || mCommList.size() < 1) {
					ShowUtils.warningMessage("没有搜索到有效串口！");
				} else {
					int index = mCommChoice.getSelectedIndex();
					mCommChoice.removeAllItems();
					for (String s : mCommList) {
						mCommChoice.addItem(s);
					}
					mCommChoice.setSelectedIndex(index);
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// NO OP
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// NO OP
			}
		});

	
		mSerialPortOperate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("打开串口".equals(mSerialPortOperate.getText()) && mSerialport == null) {
					openSerialPort(e);
				} else {
					closeSerialPort(e);
				}
			}
		});

	
		mSendData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendData(e);
			}
		});
	}


	private void openSerialPort(java.awt.event.ActionEvent evt) {
		
		String commName = (String) mCommChoice.getSelectedItem();
		
		int baudrate = 9600;
		String bps = (String) mBaudrateChoice.getSelectedItem();
		baudrate = Integer.parseInt(bps);

		
		if (commName == null || commName.equals("")) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			try {
				mSerialport = SerialPortManager.openPort(commName, baudrate);
				if (mSerialport != null) {
					SerialPortManager.sendToPort(mSerialport, "00".getBytes());
					mDataView.setText("串口已打开" + "\r\n");
					mSerialPortOperate.setText("关闭串口");
				}
			} catch (PortInUseException e) {
				ShowUtils.warningMessage("串口已被占用！");
			}
		}

		
		SerialPortManager.addListener(mSerialport, new SerialPortManager.DataAvailableListener() {

			@Override
			public void dataAvailable() {
				byte[] data = null;
				
				try {
					if (mSerialport == null) {
						ShowUtils.errorMessage("串口对象为空，监听失败！");
					} else {
						
						data = SerialPortManager.readFromPort(mSerialport);

						
						if (mDataASCIIChoice.isSelected()) {
							mDataView.append(new String(data) + "\r\n");
							i++;
							mField.setText(i+"");
						}

						
						if (mDataHexChoice.isSelected()) {
							mDataView.append(ByteUtils.byteArrayToHexString(data) + "\r\n");
						}
					}
				} catch (Exception e) {
					ShowUtils.errorMessage(e.toString());
					
					System.exit(0);
				}
			}
		});
	}


	private void closeSerialPort(java.awt.event.ActionEvent evt) {
		SerialPortManager.sendToPort(mSerialport, "0".getBytes());
		SerialPortManager.closePort(mSerialport);
		mDataView.setText("串口已关闭" + "\r\n");
		mSerialPortOperate.setText("打开串口");
		mSerialport = null;
	}

	
	private void sendData(java.awt.event.ActionEvent evt) {
		
		String data = mDataInput.getText().toString();

		if (mSerialport == null) {
			ShowUtils.warningMessage("请先打开串口！");
			return;
		}

		if ("".equals(data) || data == null) {
			ShowUtils.warningMessage("请输入要发送的数据！");
			return;
		}

		
		if (mDataASCIIChoice.isSelected()) {
			String data1=crcUtils.getCrc(data.getBytes());
			String data2=data+"-"+data1.substring(0, 2)+"-"+data1.substring(2,4);
			SerialPortManager.sendToPort(mSerialport, data2.getBytes());
		}

		
		if (mDataHexChoice.isSelected()) {
			SerialPortManager.sendToPort(mSerialport, ByteUtils.hexStr2Byte(data));
		}
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
}