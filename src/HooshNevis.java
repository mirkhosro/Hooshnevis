import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import dictionary.Dictionary;
import editor.EditorItem;
import editor.KeyMap;

public class HooshNevis extends MIDlet implements CommandListener {
	private Command cmdExit;

	private Form form;

	private Command cmdSendSMS;

	private static final String DICT_PATH = "dict/all.dct";
	
	private Alert alertSMSError;
	
	private Alert alertSMSSuccess;
	
	private Alert alertWait;
	
	private Display display;
	
	private TextBox txtPhoneNumber;
	
	private EditorItem hn;
	
	public HooshNevis() {
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {

	}

	protected void pauseApp() {

	}

	protected void startApp() throws MIDletStateChangeException {
		form = new Form("Hush Nevis");
		// cmdLoad = new Command("Load", Command.OK, 0);
		// form.addCommand(cmdLoad);
		cmdExit = new Command("Exit", Command.EXIT, 0);
		form.addCommand(cmdExit);

		Dictionary dict = null;
		try {
			dict = loadDictionary();
		} catch (Exception e) {
			form.append("Error loading dictionary!\n" + e.getMessage());
		}
		if (dict != null) {
			hn = new EditorItem(form, dict, 6, KeyMap.SONY_ERICSSON);
			hn.setReverseText(true);
			hn.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER);
			form.append(hn);
		}
		
		// SMS related
		cmdSendSMS = new Command("SMS it!", Command.SCREEN, 1);
		form.addCommand(cmdSendSMS);
		
		alertSMSError = new Alert("Error!");
		alertSMSError.setType(AlertType.ERROR);
		
		alertSMSSuccess = new Alert("Sent!", "SMS successfully sent!", null, AlertType.INFO);
		
		alertWait = new Alert("", "Please wait...", null, AlertType.INFO);
		alertWait.setTimeout(Alert.FOREVER);
		
		txtPhoneNumber = new TextBox("Phone Number", "+98912", 13, TextField.PHONENUMBER);
		final Command cmdSend = new Command("Send", Command.OK, 0);
		final Command cmdCancel = new Command("Cancel", Command.CANCEL, 1);
		txtPhoneNumber.addCommand(cmdSend);
		txtPhoneNumber.addCommand(cmdCancel);		
		
		txtPhoneNumber.setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				if (c == cmdCancel) {
					display.setCurrent(form);
				} else if (c == cmdSend) {
					display.setCurrent(alertWait);
					TextBox t = (TextBox)d;
					try {
						HooshNevis.this.sendSMS(t.getString());
					} catch (Exception e) {
						alertSMSError.setString(e.getMessage());
						display.setCurrent(alertSMSError, form);
					}
					display.setCurrent(alertSMSSuccess, form);					
				}
			}
		});
		form.setCommandListener(this);
		display = Display.getDisplay(this);
		display.setCurrent(form);
	}

	private Dictionary loadDictionary() throws IOException {
		InputStream in = this.getClass().getResourceAsStream(DICT_PATH);
		if (in == null)
			throw new IOException("Resourse not found!");

		Dictionary dict = new Dictionary(in);
		return dict;
	}
	
	public void commandAction(Command c, Displayable d) {
		if (c == cmdExit) {
			notifyDestroyed();
		} else if (c == cmdSendSMS) {
			display.setCurrent(txtPhoneNumber);
		}
	}
	
	private void sendSMS(String number) throws IOException {
		String addr = "sms://" + number;
		MessageConnection conn = (MessageConnection) Connector.open(addr);
		TextMessage msg = (TextMessage) conn
				.newMessage(MessageConnection.TEXT_MESSAGE);
		msg.setPayloadText(hn.getText());
		conn.send(msg);		
	}

}
