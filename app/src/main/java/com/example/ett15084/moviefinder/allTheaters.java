package com.example.ett15084.moviefinder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



public class allTheaters {


    private static allTheaters aT = new allTheaters();

    ArrayList <Theatre> theatreList = new ArrayList();
    NodeList nList;


    public static allTheaters getInstance(){
        return aT;
    }

    public ArrayList readXML(){ // metodin tehtävä on lukea XML-tiedosto ja rakentaa arrayList joka sisältää kaikki teatterit ja palauttaa se pääohjelmalle
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");

            for(int i = 0; i < nList.getLength(); i++){
                Node node = nList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    if(element.getElementsByTagName("Name").item(0).getTextContent().contains(":")){ //Laitetaan tämä, jotta pelkät kaupungin nimet eivät näy (joissain kaupungeissa enemmän kuin 1 teatteri)
                        System.out.print("Nimi: ");
                        System.out.println(element.getElementsByTagName("Name").item(0).getTextContent());
                        Theatre theatre = new Theatre(element.getElementsByTagName("Name").item(0).getTextContent(), element.getElementsByTagName("ID").item(0).getTextContent());
                        theatreList.add(theatre);

                    }
                }
            }
            System.out.println(theatreList);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (SAXException e){
            e.printStackTrace();
        } finally {
            System.out.println("##########DONE##########");
        }
        return theatreList;
    }
}
