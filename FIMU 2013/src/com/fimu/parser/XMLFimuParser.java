package com.fimu.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

/**
 * 
 * @author Julien Salvi
 *
 */
public class XMLFimuParser {
	
	private Document xmlDoc = null;
	private String xml = null;
	private Context context = null;
	
	/**
	 * 
	 * @param c
	 */
	public XMLFimuParser(Context c) {
		this.context = c;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public String getXMLfromURL(String url) {
		xml = null;
		try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(context, "Encoding error while getting the data", Toast.LENGTH_SHORT).show();
        } catch (ClientProtocolException e) {
        	Toast.makeText(context, "Protocol error while getting the data", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        	Toast.makeText(context, "Error while getting the data", Toast.LENGTH_SHORT).show();
        }
		return xml;
	}
	
	/**
	 * Get the XML document from the xml string.
	 * @param _xml XML String.
	 * @return A XML Document.
	 */
	public Document getDomElement(String _xml) {
		xmlDoc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                xmlDoc = db.parse(is);
 
            } catch (ParserConfigurationException e) {
            	Toast.makeText(context, "Error while parsing the document", Toast.LENGTH_SHORT).show();
                return null;
            } catch (SAXException e) {
            	Toast.makeText(context, "Error while parsing the document", Toast.LENGTH_SHORT).show();
                return null;
            } catch (IOException e) {
            	Toast.makeText(context, "Error while parsing the document", Toast.LENGTH_SHORT).show();
                return null;
            }
  		return xmlDoc;
	}
	
	/**
	 * Get the local xml wich is stored in the assets folder.
	 * @param assetPath Asset path of the xml.
	 * @return A XML document.
	 */
	public Document getLocalXMLDocument(String assetPath) {
		InputStream is = null;
		AssetManager assetManager = this.context.getAssets();
		
		try {
			is = assetManager.open("xml/fimu.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			xmlDoc = docBuilder.parse(is);
		} catch (IOException e) {
        	Toast.makeText(context, "Error while parsing the document", Toast.LENGTH_SHORT).show();
		} catch (ParserConfigurationException e) {
        	Toast.makeText(context, "Error while parsing the document", Toast.LENGTH_SHORT).show();
		} catch (SAXException e) {
        	Toast.makeText(context, "Error while parsing the document", Toast.LENGTH_SHORT).show();
		}
		
		return xmlDoc;
	}

}
