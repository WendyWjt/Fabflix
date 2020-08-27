package funcScripts.xmlParserHandler;

import dataClass.StarsRecordClass;
import funcScripts.HelperFunc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class StarXMLParserHandler extends DefaultHandler {
    private List<StarsRecordClass> starsRecord = new ArrayList<StarsRecordClass>();

    //to maintain context
    private String tempVal;
    private StarsRecordClass tempStar;
    private boolean recordIsValid = true;

    private int starRecordCount = 0;

    public List<StarsRecordClass> getStarsRecordList(){
        return starsRecord;
    }


    //Event Handlers
    public void startDocument() throws SAXException {
        HelperFunc.initializeLogFile("StarXMLParser");
        starRecordCount = 0;
        HelperFunc.xmlHandlerLog("Start parsing StarXML.");
    }


    public void endDocument() throws SAXException {
        HelperFunc.printToConsole(starRecordCount);
        HelperFunc.xmlHandlerLog("Finish parsing StarXML. Data count: " + Integer.toString(starRecordCount) + ".");
        HelperFunc.closeLogFile();
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            HelperFunc.printToConsole(tempStar);
            tempStar = new StarsRecordClass();
            recordIsValid = true;
        }
    }


    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal = tempVal.trim();
        tempVal = tempVal.replace("~", "");
        tempVal = tempVal.replace("+", "");
        tempVal = tempVal.replace("[1]", "");
    }



    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equalsIgnoreCase("stagename")) {
            if(tempVal.equalsIgnoreCase("")){
                recordIsValid = false;
            }
            else {
                HelperFunc.printToConsole("Stagename: " + tempVal);
                tempStar.name = tempVal;
            }
        }
        else if (qName.equalsIgnoreCase("dob")){
            try {
                HelperFunc.printToConsole("dob: " + tempVal);
                if (!tempVal.equalsIgnoreCase("n.a.")){
                    if (tempVal.length() != 0) {
                        tempStar.birthYear = Integer.parseInt(tempVal);
                    }
                }
            }
            catch (NumberFormatException e){
                recordIsValid = false;
                HelperFunc.xmlHandlerLog("Error: " + tempStar.toString() + " -> Invalid year format: " + tempVal);
            }
        }
        else if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            HelperFunc.printToConsole("End of actor: " + tempVal);
            if(recordIsValid) {
                starsRecord.add(tempStar);
                starRecordCount++;
            }
        }
    }

}
