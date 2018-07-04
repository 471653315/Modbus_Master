package cn.rxsi.comtest.util;

import javax.swing.JOptionPane;

/**
 * 
 * @author Rxsi
 */
public class ShowUtils {


	public static void message(String message) {
		JOptionPane.showMessageDialog(null, message);
	}


	public static void warningMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "¾¯¸æ",
				JOptionPane.WARNING_MESSAGE);
	}


	public static void errorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "´íÎó",
				JOptionPane.ERROR_MESSAGE);
	}


	public static void plainMessage(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title,
				JOptionPane.PLAIN_MESSAGE);
	}


	public static boolean selectMessage(String title, String message) {
		int isConfirm = JOptionPane.showConfirmDialog(null, message, title,
				JOptionPane.YES_NO_OPTION);
		if (0 == isConfirm) {
			return true;
		}
		return false;
	}
}
