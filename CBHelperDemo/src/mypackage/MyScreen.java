package mypackage;

import java.io.DataInputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.w3c.dom.*;

import com.cloudbase.CBHelper;
import com.cloudbase.CBHelperAttachment;
import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBPayPalBill;
import com.cloudbase.CBPayPalBillItem;
import com.cloudbase.datacommands.CBDataAggregationCommandGroup;
import com.cloudbase.datacommands.CBDataAggregationCommandProject;
import com.cloudbase.datacommands.CBDataAggregationGroupOperator;
import com.cloudbase.datacommands.CBSearchCondition;
import com.cloudbase.datacommands.CBSearchConditionOperator;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;
import net.rim.device.api.ui.toolbar.ToolbarButtonField;
import net.rim.device.api.ui.toolbar.ToolbarManager;
import net.rim.device.api.util.StringProvider;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class MyScreen extends MainScreen implements CBHelperResponder
{
	private static CBHelper helper = null;
	private BasicEditField appCodeField;
	private BasicEditField appUniqField;
	private BasicEditField appPwdField;
	private Hashtable settingsTable;
	private PersistentObject persistentObject;
	private TestResponder responder;
	private BasicEditField functionCodeField;
	private BrowserField payPalBrowser;
	private MyScreen screenPointer;
	private UiApplication appPointer;
    /**
     * Creates a new MyScreen object
     */
    public MyScreen()
    {        
        // Set the displayed title of the screen       
        setTitle("Cloudbase.io Demo Application");
        screenPointer = this;
        responder = new TestResponder();
        appPointer = UiApplication.getUiApplication();
        
        LabelField initLabel = new LabelField("Initialization parameters", LabelField.FIELD_HCENTER);
        this.add(initLabel);
        
        appCodeField = new BasicEditField("Application Code: ", "");
        this.add(appCodeField);
        appUniqField = new BasicEditField("Application Secret: ", "");
        this.add(appUniqField);
        appPwdField = new BasicEditField("Application password: ", "");
        this.add(appPwdField);
        
        // load the settings from the persistent store
        persistentObject = PersistentStore.getPersistentObject(0x41dd06f3);
        
        synchronized(persistentObject) {
        	settingsTable = (Hashtable)persistentObject.getContents();
        	if (null == settingsTable) {
        		settingsTable = new Hashtable();
        		persistentObject.setContents(settingsTable);
                persistentObject.commit();
            } else {
            	appCodeField.setText((String)settingsTable.get("app_code"));
            	appUniqField.setText((String)settingsTable.get("app_uniq"));
            	appPwdField.setText((String)settingsTable.get("app_pwd"));
            }
        }
        
        ButtonField initButton = new ButtonField("Initialise CBHelper") {
        	protected boolean navigationClick(int status, int time) {
        		helper = new CBHelper(appCodeField.getText(), appUniqField.getText());
            	helper.setApplicationRefernce(appPointer);
        		helper.setPassword(appPwdField.getText());
        		
        		settingsTable.put("app_code", appCodeField.getText());
        		settingsTable.put("app_uniq", appUniqField.getText());
        		settingsTable.put("app_pwd", appPwdField.getText());
        		
        		synchronized(persistentObject) {
                	persistentObject.setContents(settingsTable);
                    persistentObject.commit();
                }
        		
        		return true;
        	}
        };
        this.add(initButton);
        
        LabelField logLabel = new LabelField("Log APIs", LabelField.FIELD_HCENTER);
        logLabel.setMargin(30, 0, 0, 0);
        this.add(logLabel);
        
        ButtonField logButton = new ButtonField("Debug Log") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			if (helper != null)
        				helper.logDebug("test log from BB10");
                	
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(logButton);
        
        LabelField dataLabel = new LabelField("Data APIs", LabelField.FIELD_HCENTER);
        dataLabel.setMargin(30, 0, 0, 0);
        this.add(dataLabel);
        
        ButtonField dataInsertButton = new ButtonField("Insert Data") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			if (helper != null) {
        				TestDataObject obj = new TestDataObject();
        				obj.setFirstName("Cloud");
        				obj.setLastName("Base");
        				obj.setTitle(".io");
        				
        				helper.insertDocument(obj, "bb10_test");
        			}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(dataInsertButton);
        
        ButtonField dataInsertFilesButton = new ButtonField("Insert With Files Data") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			String imageExtensions[] = {"jpg", "jpeg",
                            "bmp", "png", "gif"};

        			FileSelectorPopupScreen fps = new FileSelectorPopupScreen(null, imageExtensions);
        			fps.pickFile();
        			String theFile = fps.getFile();
        			System.out.println("Selected file: " + theFile);
        			
        			if (theFile == null) {
        				Dialog.alert("Screen was dismissed. No file was selected.");
        			} else {
        				theFile = "file:///" + theFile;
        				if (helper != null) {
        					CBHelperAttachment att = new CBHelperAttachment();
        					
        					FileConnection fconn = (FileConnection)Connector.open(theFile, Connector.READ_WRITE);
        					DataInputStream is = fconn.openDataInputStream();
        	                att.setFileData(IOUtilities.streamToBytes(is));
        	                att.setFileName(fconn.getName());
        	                
        	                System.out.println("File Size: " + att.getFileData().length);
        	                is.close();
        	                fconn.close();
        	                
        	                Vector files = new Vector();
        	                files.addElement(att);
        	                
        					TestDataObject obj = new TestDataObject();
        					obj.setFirstName("Cloud");
        					obj.setLastName("Base");
        					obj.setTitle(".io");
               				
        					helper.insertDocument(obj, "bb10_test", files, responder);
        				}
        			}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(dataInsertFilesButton);
        
        ButtonField dataSearchButton = new ButtonField("Search Data") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			if (helper != null) {
        				CBSearchCondition con = new CBSearchCondition("first_name", CBSearchConditionOperator.CBOperatorEqual, "Cloud");
        				
        				helper.searchDocument("bb10_test", con, responder);
        			}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(dataSearchButton);
        
        ButtonField dataSearchAggregateButton = new ButtonField("Search Aggregate Data") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			if (helper != null) {
        				Vector aggregationCommands = new Vector();
        				
        				CBDataAggregationCommandProject projectCommand = new CBDataAggregationCommandProject();
        				projectCommand.getIncludeFields().addElement("Symbol");
        				projectCommand.getIncludeFields().addElement("Price");
        				projectCommand.getIncludeFields().addElement("total");
        				aggregationCommands.addElement(projectCommand);
        				
        				CBDataAggregationCommandGroup groupCommand = new CBDataAggregationCommandGroup();
        				groupCommand.addOutputField("Symbol");
        				groupCommand.addGroupFormulaForField("total", CBDataAggregationGroupOperator.CBDataAggregationGroupSum, "Price");
        				aggregationCommands.addElement(groupCommand);
        				
        				helper.searchDocumentAggregate("security_master_3", aggregationCommands, responder);
        			}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(dataSearchAggregateButton);
        
        
        ButtonField dataDownloadButton = new ButtonField("Download File") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			if (helper != null) {
        				helper.downloadFile("a4d24eb5e8f89f09d42a98aabc99f4e1", responder);
        			}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(dataDownloadButton);
        
        LabelField functionLabel = new LabelField("CloudFunction APIs", LabelField.FIELD_HCENTER);
        functionLabel.setMargin(30, 0, 0, 0);
        this.add(functionLabel);
        
        functionCodeField = new BasicEditField("CloudFunction Code: ", "");
        this.add(functionCodeField);
        
        ButtonField functionExecButton = new ButtonField("Execute CloudFunction") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			if (helper != null) {
        				helper.runCloudFunction(functionCodeField.getText(), new Hashtable(), responder);
        			}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(functionExecButton);
        
        
        LabelField payPalLabel = new LabelField("PayPal APIs", LabelField.FIELD_HCENTER);
        payPalLabel.setMargin(30, 0, 0, 0);
        this.add(payPalLabel);
        
        ButtonField ppButton = new ButtonField("Start PayPal Transaction") {
        	protected boolean navigationClick(int status, int time) {
        		try {
        			if (helper != null) {
        				CBPayPalBillItem item = new CBPayPalBillItem();
        				item.setAmount(9.99);
        				item.setTax(0);
        				item.setQuantity(1);
        				item.setName("test item");
        				item.setDescription("test item for $9.99");
        				
        				CBPayPalBill bill = new CBPayPalBill();
        				bill.setCurrency("USD");
        				bill.setDescription("test transaction for $9.99");
        				bill.setName("test transaction");
        				bill.setInvoiceNumber("TST_INV_001");
        				bill.addNewItem(item);
        				
        				helper.preparePayPalPurchase(bill, true, screenPointer);
        			}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			System.out.println("Exception: " + e.getMessage());
        		}
        		
        		return true;
        	}
        };
        this.add(ppButton);
        
        payPalBrowser = new BrowserField();
        add(payPalBrowser);
    }
    
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
   
    private String generateSessionVal(String username) throws Exception
    {
        byte[] bytes = username.getBytes();
        MD5Digest digest = new MD5Digest();
        digest.update(bytes, 0, bytes.length);
        int length = digest.getDigestLength();
        byte[] md5 = new byte[length];
        digest.getDigest(md5, 0, true);
        return convertToHex(md5);
   }

	public void handleResponse(CBHelperResponse res) throws JSONException {
		// TODO Auto-generated method stub
		System.out.println("received response from PayPal");
		JSONObject jsonResp = (JSONObject)res.getData();
		BrowserFieldListener listener = new BrowserFieldListener() {
			public void documentLoaded(BrowserField browserField, Document document) throws Exception
			{
				String url = browserField.getDocumentUrl();
				if (url.indexOf("paypal/update-status") != -1) {
					helper.completePayPalPurchase(url, responder);
				}
			}
		};
		payPalBrowser.addListener( listener );
		
		payPalBrowser.requestContent(jsonResp.getString("checkout_url"));
		System.out.println("requesting checkout url: " + jsonResp.getString("checkout_url"));
		
	}
}
