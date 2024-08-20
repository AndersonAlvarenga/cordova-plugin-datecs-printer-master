package com.giorgiofellipe.datecsprinter;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class DatecsPrinter extends CordovaPlugin {
	private DatecsSDKWrapper printer;

	private enum Option {
		listBluetoothDevices,
				connect,
				disconnect,
				feedPaper,
				printText,
				getStatus,
				getTemperature,
				setBarcode,
				printBarcode,
				printQRCode,
				printImage,
				printLogo,
				printSelfTest,
				setPageRegion,
				selectPageMode,
				selectStandardMode,
				drawPageRectangle,
				drawPageFrame,
				printPage,
				write,
				writeHex;
	}

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		printer = new DatecsSDKWrapper(cordova);
		printer.setWebView(webView);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		printer.setCallbackContext(callbackContext);





		Option option = null;
		try {
			option = Option.valueOf(action);
		} catch (Exception e) {
			return false;
		}
		switch (option) {
			case listBluetoothDevices:
			checkPermission();
				Toast.makeText(this, "Entrou", Toast.LENGTH_SHORT).show();
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter == null) {
				Toast.makeText(this, "Bluetooth Adapter Null", Toast.LENGTH_SHORT).show();
				// O dispositivo não suporta Bluetooth
			} 
			if (!bluetoothAdapter.isEnabled()) {
				Toast.makeText(this, "Bluetooth Enable", Toast.LENGTH_SHORT).show();
				// Solicitar ao usuário para habilitar o Bluetooth
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				checkPermission();
				startActivityForResult(enableBtIntent, 0);
			}

				printer.getBluetoothPairedDevices(callbackContext);
				break;
			case setPermissionScan:
				
				try{
					cordova.plugins.diagnostic.runtimePermission.BLUETOOTH_SCAN;
					callbackContext.sucess(true)
				}catch(e){
					callbackContext.error(false)
				}
				break;
			case setPermissionConnect:
				try{
					cordova.plugins.diagnostic.runtimePermission.BLUETOOTH_CONNECT;
					callbackContext.sucess(true)
				}catch(e){
					callbackContext.error(false)
				}
				break;
			case connect:
				printer.setAddress(args.getString(0));
				printer.connect(callbackContext);
				break;
			case disconnect:
				try {
					printer.closeActiveConnections();
					callbackContext.success(DatecsUtil.getStringFromStringResource(this.cordova.getActivity().getApplication(), "printer_disconnected"));
				} catch (Exception e) {
					callbackContext.success(DatecsUtil.getStringFromStringResource(this.cordova.getActivity().getApplication(), "err_disconnect_printer"));
				}
				break;
			case feedPaper:
				printer.feedPaper(args.getInt(0));
				break;
			case printText:
				String text = args.getString(0);
				String charset = args.getString(1);
				printer.printTaggedText(text, charset);
				break;
			case getStatus:
				printer.getStatus();
				break;
			case getTemperature:
				printer.getTemperature();
				break;
			case setBarcode:
				int align = args.getInt(0);
				boolean small = args.getBoolean(1);
				int scale = args.getInt(2);
				int hri = args.getInt(3);
				int height = args.getInt(4);
				printer.setBarcode(align, small, scale, hri, height);
				break;
			case printBarcode:
				int type = args.getInt(0);
				String data = args.getString(1);
				printer.printBarcode(type, data);
				break;
			case printQRCode:
				int size = args.getInt(0);
				int eccLv = args.getInt(1);
				data = args.getString(2);
				printer.printQRCode(size, eccLv, data);
				break;
			case printImage:
				String image = args.getString(0);
				int imgWidth = args.getInt(1);
				int imgHeight = args.getInt(2);
				int imgAlign = args.getInt(3);
				printer.printImage(image, imgWidth, imgHeight, imgAlign);
				break;
			case printLogo:
				break;
			case printSelfTest:
				printer.printSelfTest();
				break;
			case drawPageRectangle:
			  int x = args.getInt(0);
			  int y = args.getInt(1);
			  int width = args.getInt(2);
			  height = args.getInt(3);
			  int fillMode = args.getInt(4);
			  printer.drawPageRectangle(x, y, width, height, fillMode);
			  break;
			case selectPageMode:
			  printer.selectPageMode();
			  break;
			case selectStandardMode:
			  printer.selectStandardMode();
			  break;
			case setPageRegion:
			  x = args.getInt(0);
			  y = args.getInt(1);
			  width = args.getInt(2);
			  height = args.getInt(3);
			  int direction = args.getInt(4);
			  printer.setPageRegion(x, y, width, height, direction);
			  break;
			case drawPageFrame:
			  x = args.getInt(0);
			  y = args.getInt(1);
			  width = args.getInt(2);
			  height = args.getInt(3);
			  fillMode = args.getInt(4);
			  int thickness = args.getInt(5);
			  printer.drawPageFrame(x, y, width, height, fillMode, thickness);
			  break;
			case printPage:
			  printer.printPage();
			  break;
			case write:
				byte[] bytes = args.getString(0).getBytes();
			  printer.write(bytes);
			  break;
			case writeHex:
				String hex = args.getString(0);
			  printer.writeHex(hex);
			  break;
		}
		return true;
	}
	 public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
        } else {
            // Permission is already granted, proceed with Bluetooth operations
            // Your Bluetooth code here
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_CONNECT);
        } else {
            // Permission is already granted, proceed with Bluetooth operations
            // Your Bluetooth code here
        }
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (bt.isEnabled()) {
            Toast.makeText(this, "Bluetooth está ligado", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }

}
