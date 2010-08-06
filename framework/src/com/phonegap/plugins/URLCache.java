package com.phonegap.plugins;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.IllegalModeException;

import org.json.me.JSONArray;
import org.json.me.JSONException;

import com.phonegap.PhoneGap;
import com.phonegap.api.CommandResult;
import com.phonegap.api.PluginCommand;
import com.phonegap.util.MD5;

public final class URLCache implements PluginCommand {
	
	private PhoneGap app;
	
	public void setContext(PhoneGap app) {
		this.app = app;
	}
	
	public CommandResult execute(String action, JSONArray args) {

		CommandResult.Status status = CommandResult.Status.OK;
		String result = "";

		if (action.equals("getCachedPathForURI") && args.length() == 2)
		{
			String uri = null;
			String fileName = null;
			String fileDir = null;
			
			try {
				uri = args.getString(0);
				fileName = MD5.hash(uri);
				fileDir = args.getString(1);
			} catch (JSONException e1) { }

			if (uri != null && !uri.equals("") && fileDir != null && !fileDir.equals("")) {
			
				FileConnection fc = null;
	
				// First check if the file exists already
				String filePath = fileDir + System.getProperty("file.separator") + fileName;
				
				boolean validDir = createDir(fileDir + System.getProperty("file.separator"));
				
	            try {
					fc = (FileConnection)Connector.open(filePath);
				} catch (IOException e1) {
				}

				if (fc.exists()) {
					result = "{ file: '"+filePath+"', status: 0 }";
				} else {
	
					DataInputStream dis = null;
					DataOutputStream out = null;
					StreamConnection c = null;
					byte[] buffer = new byte[1024];
					int length = -1;
	
					try {
						c = (StreamConnection)Connector.open(uri);
						dis = c.openDataInputStream();
						fc.create();
						out = fc.openDataOutputStream();
						while ((length = dis.read(buffer)) != -1) {
							out.write(buffer, 0, length);
						}
						out.flush();
						result = "{ file: '"+filePath+"', status: 0 }";
					} catch (SecurityException e) {
						status = CommandResult.Status.IOEXCEPTION;
						result = "{ message: 'SecurityException creating cache file "+e.getMessage()+"', status: "+status.ordinal()+" }";
					} catch (IllegalModeException e) {
						status = CommandResult.Status.IOEXCEPTION;
						result = "{ message: 'IllegalModeException creating cache file "+e.getMessage()+"', status: "+status.ordinal()+" }";
					} catch (IOException e) {
						status = CommandResult.Status.IOEXCEPTION;
						result = "{ message: 'IOException creating cache file "+e.getMessage()+"', status: "+status.ordinal()+" }";
					} finally {
						try {
							if (dis != null) {
								dis.close();
							}
							if (c != null) {
								c.close();
							}
							if (out != null) {
								out.close();
							}
						} catch (IOException e) {
							status = CommandResult.Status.IOEXCEPTION;
							result = "{ message: 'IOException', status: "+status.ordinal()+" }";
						}
					}
				}
			} else {
				status = CommandResult.Status.JSONEXCEPTION;
				result = "{ message: 'JSONException. Could not parse file path or name args.', status: "+status.ordinal()+" }";
			}
		} else {
			status = CommandResult.Status.INVALIDACTION;
			result = "{ message: 'InvalidAction', status: "+status.ordinal()+" }";
		}
		return new CommandResult(status, result);
	}
	
	protected boolean createDir(String dir) {
		try {
			FileConnection fc;
			String[] path = splitString(dir, '/', false);
			String tempDir = "file:///" + path[1];
			for (int i=2; i<path.length; i++) {
				tempDir += System.getProperty("path.separator") + path[i];
				fc = (FileConnection)Connector.open(tempDir);
				if (!fc.exists()) {
					fc.mkdir();
				}
				fc.close();
			}
		} catch(IOException e2) {
			return false;
		}
		return true;
	}
	
	public static final String[] splitString(final String data, final char splitChar, final boolean allowEmpty)
    {
        Vector v = new Vector();

        int indexStart = 0;
        int indexEnd = data.indexOf(splitChar);
        if (indexEnd != -1)
        {
            while (indexEnd != -1)
            {
                String s = data.substring(indexStart, indexEnd);
                if (allowEmpty || s.length() > 0)
                {
                    v.addElement(s);
                }
                indexStart = indexEnd + 1;
                indexEnd = data.indexOf(splitChar, indexStart);
            }

            if (indexStart != data.length())
            {
                // Add the rest of the string
                String s = data.substring(indexStart);
                if (allowEmpty || s.length() > 0)
                {
                    v.addElement(s);
                }
            }
        }
        else
        {
            if (allowEmpty || data.length() > 0)
            {
                v.addElement(data);
            }
        }

        String[] result = new String[v.size()];
        v.copyInto(result);
        return result;
    }
}
